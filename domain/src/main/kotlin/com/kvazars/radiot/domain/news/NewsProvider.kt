package com.kvazars.radiot.domain.news

import com.kvazars.radiot.domain.news.models.NewsItem
import io.reactivex.Single

interface NewsProvider {
    fun getActiveNewsId(): Single<String>

    fun getNewsList(): Single<List<NewsItem>>
}