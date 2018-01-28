package com.kvazars.radiot.domain.news.models

import org.threeten.bp.ZonedDateTime

class NewsItem(
        val id: String,
        val title: String,
        val snippet: String,
        val link: String?,
        val pictureUrl: String?,
        val active: Boolean,
        val time: ZonedDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NewsItem

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}