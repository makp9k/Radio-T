package com.kvazars.radio_t.domain.news

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 19.03.2017.
 */
class NewsInteractor(chatMessageNotifications: Observable<ChatMessageNotification>,
                     newsProvider: NewsProvider) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val activeNewsUpdateTrigger = chatMessageNotifications
            .filter { it.authorName == "Makp9k" && it.message.startsWith("==>") }
            .map { true }

    val activeNewsIds: Observable<String> = activeNewsUpdateTrigger
            .startWith(true)
            .buffer(1, TimeUnit.SECONDS)
            .switchMap { newsProvider.getActiveNewsId().toObservable() }
            .onErrorReturnItem("")
            .filter(String::isNotEmpty)
            .distinctUntilChanged()
            .share()

    private var newsCache: List<NewsItem>? = null
    val allNews: Observable<List<NewsItem>> = Observable.concat(
            newsProvider.getNewsList().toObservable(),
            activeNewsIds.flatMap { activeNewsId ->
                val cache = newsCache
                if (cache != null && cache.find { it.id == activeNewsId } != null) {
                    Observable.empty()
                } else {
                    newsProvider.getNewsList().toObservable()
                }
            })
            .doOnNext { newsCache = it }
            .share()

    val activeNews: Observable<Maybe<NewsItem>> = Observable.combineLatest(
            activeNewsIds,
            allNews,
            BiFunction {id, news ->
                val newsItem = news.find { it.id == id }
                if (newsItem == null) {
                    Maybe.empty<NewsItem>()
                } else {
                    Maybe.just(newsItem)
                }
            }
    )

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}

data class ChatMessageNotification(
        val authorName: String,
        val message: String
)

class NewsItem(
        val id: String,
        val title: String,
        val snippet: String,
        val link: String?,
        val pictureUrl: String?
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
    fun getActiveNewsId(): Single<String>

    fun getNewsList(): Single<List<NewsItem>>
}