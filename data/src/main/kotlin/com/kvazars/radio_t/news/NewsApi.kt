package com.kvazars.radio_t.news

import com.kvazars.radio_t.news.models.ActiveNewsId
import com.kvazars.radio_t.news.models.NewsItem
import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by lza on 12.03.2017.
 */
interface NewsApi {

    @GET("api/v1/news")
    fun getNews(): Single<List<NewsItem>>

    @GET("api/v1/news/active/id")
    fun getActiveNewsId(): Single<ActiveNewsId>

}