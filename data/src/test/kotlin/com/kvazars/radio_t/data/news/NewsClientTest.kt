package com.kvazars.radio_t.data.news

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
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
        val errors = Observable.range(0, 10)

        errors
                .zipWith(
                        Observable.range(0, 4),
                        BiFunction<Int, Int, Pair<Int, Int>> { error, i -> Pair(error, i) }
                )
                .flatMap { if (it.second != 4) Observable.just(it.second) else Observable.error(RuntimeException("${it.first}")) }
                .doOnError { println(it) }
                .doOnNext { println(it) }
                .test()
    }

}