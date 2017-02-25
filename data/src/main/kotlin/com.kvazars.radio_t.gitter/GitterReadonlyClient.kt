package com.kvazars.radio_t.gitter

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

/**
 * Created by lza on 24.02.2017.
 */
class GitterReadonlyClient {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println)).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun connect(): Observable<ChatMessage> {
        return getAccessData()
                .flatMap(
                        { pair -> handshake(createHandshakePayload(pair.first)) },
                        { pair, handshakeResponse -> Pair(handshakeResponse.clientId, pair.second) }
                )
                .flatMap { pair ->
                    val request = Request.Builder().url("wss://ws.gitter.im/bayeux").build()

                    val webSocketOnSubscribe = WebSocketOnSubscribe(pair.first, pair.second)
                    httpClient.newWebSocket(request, webSocketOnSubscribe)

                    Observable.create(webSocketOnSubscribe)
                }
    }

    private fun getAccessData(): Observable<Pair<String, String>> {
        val gitterApi = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://gitter.im/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(GitterApi::class.java)

        val clientIdPattern = Pattern.compile("\"accessToken\":\"([^\"]+)\"");
        val roomIdPattern = Pattern.compile("\"troupe\":\\{\"id\":\"([^\"]+)\"");
        return gitterApi.getChatPage("testtestasd/Lobby")
                .map { it.string() }
                .map {
                    val matcherClientId = clientIdPattern.matcher(it)
                    val matcherRoomId = roomIdPattern.matcher(it)
                    if (matcherClientId.find() && matcherRoomId.find()) {
                        Pair(matcherClientId.group(1), matcherRoomId.group(1))
                    } else {
                        null
                    }
                }
    }

    private fun handshake(payload: String): Observable<HandshakeResponse> {
        val gitterApi = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://ws.gitter.im/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WebsocketGitterApi::class.java)

        return gitterApi.handshake(RequestBody.create(MediaType.parse("text/plain"), payload)).flatMapIterable { it }.take(1)
    }

    private fun createHandshakePayload(accessToken: String): String {
        return "message=[{\"channel\":\"/meta/handshake\",\"id\":\"1\",\"ext\":{\"token\":\"$accessToken\",\"version\":\"b23011\",\"connType\":\"online\",\"client\":\"web\",\"uniqueClientId\":61000,\"realtimeLibrary\":\"halley\"},\"version\":\"1.0\",\"supportedConnectionTypes\":[\"websocket\",\"long-polling\"]}]"
    }

//endregion

//region INNER CLASSES -------------------------------------------------------------------------

    private class WebSocketOnSubscribe(val clientId: String, val roomId: String) : WebSocketListener(), ObservableOnSubscribe<ChatMessage> {

        var emitter: ObservableEmitter<ChatMessage>? = null
        var socket: WebSocket? = null
        var isClosed = true

        override fun subscribe(e: ObservableEmitter<ChatMessage>?) {
            emitter = e

            val d = object : Disposable {
                override fun isDisposed(): Boolean = isClosed

                override fun dispose() {
                    socket?.close(1000, null)
                    isClosed = true
                }

            }
            e?.setDisposable(d)
        }

        override fun onOpen(webSocket: WebSocket?, response: Response?) {
            socket = webSocket
            isClosed = false

            webSocket?.send("[{\"channel\":\"/meta/connect\",\"id\":\"2\",\"connectionType\":\"websocket\",\"clientId\":\"$clientId\"}]")
            webSocket?.send("[{\"channel\":\"/meta/subscribe\",\"subscription\":\"/api/v1/rooms/$roomId/chatMessages\",\"id\":\"3\",\"ext\":{\"snapshot\":false},\"clientId\":\"$clientId\"}]")
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            emitter?.onError(t)
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            emitter?.onComplete()
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            if (text != null) {
                emitter?.onNext(ChatMessage("author", text))
            }
        }

        override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
            if (bytes != null) {
                emitter?.onNext(ChatMessage("author", bytes.utf8()))
            }
        }
    }

//endregion
}