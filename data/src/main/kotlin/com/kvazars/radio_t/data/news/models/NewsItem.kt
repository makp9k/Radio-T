package com.kvazars.radio_t.data.news.models

import com.google.gson.annotations.SerializedName

/**
 * Created by lza on 12.03.2017.
 */
data class NewsItem(
        val id: String,
        val title: String,
        val snippet: String,
        val link: String,
        @SerializedName("pic")
        val pictureUrl: String
)