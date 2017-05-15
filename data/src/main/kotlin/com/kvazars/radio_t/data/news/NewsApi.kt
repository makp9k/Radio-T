package com.kvazars.radio_t.data.news

import com.kvazars.radio_t.data.news.models.ActiveNewsId
import com.kvazars.radio_t.data.news.models.NewsItem
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

/**
 * Created by lza on 12.03.2017.
 */
interface NewsApi {

    @GET("api/v1/news")
    fun getNews(): Single<List<NewsItem>>

    @GET("api/v1/news/active")
    fun getActiveNewsId(): Single<ActiveNewsId>

}