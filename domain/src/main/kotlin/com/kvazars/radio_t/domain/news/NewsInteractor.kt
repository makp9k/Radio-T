package com.kvazars.radio_t.domain.news

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by lza on 19.03.2017.
 */
class NewsInteractor(chatMessageNotifications: Observable<ChatMessageNotification>,
                     newsProvider: NewsProvider) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    val activeNews: Observable<NewsItem> = chatMessageNotifications.flatMap { newsProvider.getActiveNews().toObservable() }.share()

    private var newsCache: List<NewsItem>? = null

    val news: Observable<List<NewsItem>> = Observable.concat(
            newsProvider.getNewsList().toObservable(),
            activeNews.flatMap {
                val cache = newsCache
                if (cache != null && cache.contains(it)) {
                    Observable.empty()
                } else {
                    newsProvider.getNewsList().toObservable()
                }
            }
    ).doOnNext { newsCache = it }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}

class ChatMessageNotification

data class NewsItem(
        val id: String,
        val title: String,
        val snippet: String,
        val link: String,
        val pictureUrl: String
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

interface NewsProvider {
    fun getActiveNews(): Single<NewsItem>

    fun getNewsList(): Single<List<NewsItem>>
}