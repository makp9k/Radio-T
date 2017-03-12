package com.kvazars.radio_t.news.models

/**
 * Created by lza on 12.03.2017.
 */
data class NewsItem(
        val id: String,
        val title: String,
        val snippet: String,
        val link: String,
        val pictureUrl: String
)