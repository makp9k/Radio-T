package com.kvazars.radiot.data.news

import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

/**
 * Created by lza on 12.03.2017.
 */
class NewsClientTest {

    private val loggingHttpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    @Test
    fun getNews() {
        val observer = NewsClient(loggingHttpClient).news.test()

        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun getActiveNewsId() {
        val observer = NewsClient(loggingHttpClient).activeNews.test()

        observer.assertNoErrors()
        observer.assertComplete()
    }

    @Test
    fun test() {
        val chatMessages = Observable.just(1,2,3,4,5)
        val hundredMessages = Observable.just(100)

        println(chatMessages.flatMap {
            if (it == 2) {
                hundredMessages.startWith(it)
            } else {
                Observable.just(it)
            }
        }.test().values())
    }

}