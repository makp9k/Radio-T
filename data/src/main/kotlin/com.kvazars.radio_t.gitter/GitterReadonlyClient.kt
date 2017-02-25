package com.kvazars.radio_t.gitter

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

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

    init {

    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun handshake(): Observable<HandshakeResponse> {
        val gitterApi = Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://ws.gitter.im/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(GitterApi::class.java)

        return gitterApi.handshake(RequestBody.create(MediaType.parse("text/plain"), createHandshakePayload())).flatMapIterable { it }.take(1)
    }

    fun connect(): Observable<ChatMessage> {
        return handshake()
                .map { it.clientId }
                .flatMap { clientId ->
                    val request = Request.Builder().url("wss://ws.gitter.im/bayeux").build()

                    val webSocketOnSubscribe = WebSocketOnSubscribe(clientId)
                    httpClient.newWebSocket(request, webSocketOnSubscribe)

                    Observable.create(webSocketOnSubscribe)
                }

    }

    private fun createHandshakePayload(): String {
        return "message=[{\"channel\":\"/meta/handshake\",\"id\":\"1\",\"ext\":{\"token\":\"\$IjXRJ4GyJruWjm1r00W7bdezIxuRsgiZgKdg34yXRz4=\",\"version\":\"b23011\",\"connType\":\"online\",\"client\":\"web\",\"uniqueClientId\":61000,\"realtimeLibrary\":\"halley\"},\"version\":\"1.0\",\"supportedConnectionTypes\":[\"websocket\",\"long-polling\"]}]"
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    private class WebSocketOnSubscribe(val clientId: String) : WebSocketListener(), ObservableOnSubscribe<ChatMessage> {

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
            webSocket?.send("[{\"channel\":\"/meta/subscribe\",\"subscription\":\"/api/v1/rooms/58b0ccf4d73408ce4f4cb9fa/chatMessages\",\"id\":\"3\",\"ext\":{\"snapshot\":false},\"clientId\":\"$clientId\"}]")

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