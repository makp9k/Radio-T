package com.kvazars.radio_t.gitter.streaming

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.kvazars.radio_t.gitter.auth.models.GitterChatAccessData
import com.kvazars.radio_t.gitter.models.ChatMessage
import com.kvazars.radio_t.gitter.streaming.models.HandshakeResponse
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 24.02.2017.
 */
class GitterReadonlyStreamingClient(private val httpClient: OkHttpClient,
                                    private val scheduler: Scheduler) {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val gitterApi = Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://ws.gitter.im/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WebsocketGitterApi::class.java)

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun connect(accessData: GitterChatAccessData): Observable<ChatMessage> {
        return handshake(createHandshakePayload(accessData.accessToken))
                .flatMap {
                    val request = Request.Builder().url("wss://ws.gitter.im/bayeux").build()

                    val webSocketOnSubscribe = WebSocketOnSubscribe(it.clientId, accessData.roomId, scheduler)
                    httpClient.newWebSocket(request, webSocketOnSubscribe)

                    Observable.create(webSocketOnSubscribe)
                }
    }

    private fun handshake(payload: String): Observable<HandshakeResponse> {
        return gitterApi
                .handshake(RequestBody.create(MediaType.parse("text/plain"), payload))
                .toObservable()
                .flatMapIterable { it }
    }

    private fun createHandshakePayload(accessToken: String): String {
        val uniqueClientId = Math.floor(1e5 * Math.random())
        return "message=" + URLEncoder.encode("[{\"channel\":\"/meta/handshake\"," +
                "\"id\":\"1\",\"ext\":{\"token\":\"$accessToken\"," +
                "\"version\":\"b23011\",\"connType\":\"online\",\"client\":\"web\"," +
                "\"uniqueClientId\":\"$uniqueClientId\", \"realtimeLibrary\":\"halley\"},\"version\":\"1.0\"," +
                "\"supportedConnectionTypes\":[\"websocket\",\"long-polling\"]}]", "UTF-8")
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    private class WebSocketOnSubscribe(private val clientId: String,
                                       private val roomId: String,
                                       private val scheduler: Scheduler) : WebSocketListener(), ObservableOnSubscribe<ChatMessage> {

        var emitter: ObservableEmitter<ChatMessage>? = null
        var socket: WebSocket? = null
        var isClosed = true

        val gson = GsonBuilder().create()
        val messageChannel = "/api/v1/rooms/$roomId/chatMessages"

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

            sendConnectMessage()

            Observable
                    .interval(30, TimeUnit.SECONDS, scheduler)
                    .takeUntil { ignore: Long -> isClosed }
                    .subscribe { webSocket?.send("[{\"channel\":\"/api/v1/ping2\",\"data\":{\"reason\":\"ping\"},\"id\":\"$it\",\"clientId\":\"$clientId\"}]") }
        }

        override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
            println(response?.message())
            emitter?.onError(t)
        }

        override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
            println(reason)
            emitter?.onComplete()
        }

        override fun onMessage(webSocket: WebSocket?, text: String?) {
            println(text)
            if (text != null) {
                val json = gson.fromJson(text, JsonArray::class.java).get(0).asJsonObject

                val channel = json.get("channel").asString
                if (json.has("successful") && !json.get("successful").asBoolean) {
                    emitter?.onError(RuntimeException("error"))
                } else if (messageChannel == channel) {
                    val model = json.get("data")?.asJsonObject?.get("model")
                    emitter?.onNext(gson.fromJson(model, ChatMessage::class.java))
                } else if (channel == "/meta/connect") {
                    sendConnectMessage()
                }

            }
        }

        fun sendConnectMessage() {
            socket?.send("[{\"channel\":\"/meta/connect\",\"id\":\"2\",\"connectionType\":\"websocket\",\"clientId\":\"$clientId\"}]")
            socket?.send("[{\"channel\":\"/meta/subscribe\",\"subscription\":\"/api/v1/rooms/$roomId/chatMessages\",\"id\":\"3\",\"ext\":{\"snapshot\":false},\"clientId\":\"$clientId\"}]")
        }
    }

    //endregion
}