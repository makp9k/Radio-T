package com.kvazars.radio_t.domain.news

import com.kvazars.radio_t.domain.news.models.NewsItem
import io.reactivex.Single

interface NewsProvider {
    fun getActiveNewsId(): Single<String>

    fun getNewsList(): Single<List<NewsItem>>
}