package com.kvazars.radio_t.gitter

import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import java.util.concurrent.TimeUnit
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.net.CookiePolicy.ACCEPT_ALL




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
        val scheduler = TestScheduler()
        val observer = GitterClientFacade(loggingHttpClient, Schedulers.io()).getMessageStream().doOnNext(::println).test()

        scheduler.advanceTimeBy(15, TimeUnit.SECONDS)

        observer.await()
    }

}

private class MockedGitterServerInterceptor : Interceptor {
    var counter = 0
    override fun intercept(chain: Interceptor.Chain?): Response {
        val url = chain?.request()?.url().toString()
        println(chain?.request())

        val responseBuilder = Response.Builder().protocol(Protocol.HTTP_1_1).request(chain?.request())
        when (url) {
            "https://gitter.im/testtestasd%2FLobby/~chat" -> {
                responseBuilder.code(200)
                responseBuilder.body(ResponseBody.create(MediaType.parse("text/html"), getMockResponseBody("chat.html")))
            }
            "https://ws.gitter.im/bayeux" -> {
                if (chain?.request()?.method() == "GET") {
                    responseBuilder.code(101)
                    responseBuilder.addHeader("Connection", "Upgrade")
                } else {
                    responseBuilder.code(200)
                }

                if (counter++ < 10) {
                    responseBuilder.body(ResponseBody.create(MediaType.parse("text/json"), getMockResponseBody("handshake_unsuccessful.json")))
                } else {
                    responseBuilder.body(ResponseBody.create(MediaType.parse("text/json"), getMockResponseBody("handshake_successful.json")))
                }
            }
        }

        val response = responseBuilder.build()
        if (chain != null && response.code() == 0) {
            return chain.proceed(chain.request())
        }
        return response
    }

    fun getMockResponseBody(name: String): String {
        return javaClass.getResourceAsStream("/mock/gitter/$name").bufferedReader().readText()
    }

}
