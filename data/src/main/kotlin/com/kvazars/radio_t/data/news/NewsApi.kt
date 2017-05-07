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
    @Headers("Cache-Control:max-age=0")
    fun getNews(): Single<List<NewsItem>>

    @GET("api/v1/news/active")
    @Headers(
            "Accept:text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Encoding:gzip, deflate, sdch, br",
            "Accept-Language:ru,en;q=0.8,de;q=0.6",
            "Cache-Control:max-age=0",
            "Connection:keep-alive",
            "DNT:1",
            "Host:news.radio-t.com",
            "Upgrade-Insecure-Requests:1",
            "User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 YaBrowser/17.3.1.840 Yowser/2.5 Safari/537.36"
    )
    fun getActiveNewsId(): Single<ActiveNewsId>

}