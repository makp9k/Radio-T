package com.kvazars.radio_t.gitter

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

/**
 * Created by lza on 28.02.2017.
 */
class GitterClientFacadeTest {

    private val mockHttpClient = OkHttpClient.Builder().addInterceptor(MockedGitterServerInterceptor()).build()
    private val loggingHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    @Test
    fun getMessageStream() {
        val observer = GitterClientFacade(mockHttpClient).getMessageStream().doOnNext(::println).test()

        println(observer.values())

        observer.await()
    }

}

private class MockedGitterServerInterceptor : Interceptor {
    var counter = 0
    override fun intercept(chain: Interceptor.Chain?): Response {
        val url = chain?.request()?.url().toString()

        val responseBuilder = Response.Builder().protocol(Protocol.HTTP_1_1).request(chain?.request())
        when (url) {
            "https://gitter.im/testtestasd%2FLobby/~chat" -> {
                responseBuilder.code(200)
                responseBuilder.body(ResponseBody.create(MediaType.parse("text/html"), getMockResponseBody("chat.html")))
            }
            "https://ws.gitter.im/bayeux" -> {
                responseBuilder.code(200)
                if (counter++ < 3) {
                    responseBuilder.body(ResponseBody.create(MediaType.parse("text/json"), getMockResponseBody("handshake_unsuccessful.json")))
                } else {
                    responseBuilder.body(ResponseBody.create(MediaType.parse("text/json"), getMockResponseBody("handshake_successful.json")))
                }
            }
        }

        val response = responseBuilder.build()
        println(url)
        if (chain != null && response.code() == 0) {
            return chain.proceed(chain.request())
        }
        return response;
    }

    fun getMockResponseBody(name:String): String {
        return javaClass.getResourceAsStream("/mock/gitter/$name").bufferedReader().readText()
    }

}
