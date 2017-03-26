package com.kvazars.radio_t.data.news

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

}