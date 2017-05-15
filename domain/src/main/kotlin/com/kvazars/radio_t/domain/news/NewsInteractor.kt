package com.kvazars.radio_t.domain.news

import com.kvazars.radio_t.domain.news.models.ChatMessageNotification
import com.kvazars.radio_t.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 19.03.2017.
 */
class NewsInteractor(chatMessageNotifications: Observable<ChatMessageNotification>,
                     newsProvider: NewsProvider,
                     scheduler: Scheduler = Schedulers.io()) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val activeNewsUpdateTrigger = chatMessageNotifications
            .filter { it.authorName == "Makp9k" && it.message.startsWith("==>") }
            .map { true }
            .subscribeOn(scheduler)

    private val activeNewsIds: Observable<String> = activeNewsUpdateTrigger
            .startWith(true)
            .buffer(1, TimeUnit.SECONDS, scheduler)
            .filter { !it.isEmpty() }
            .switchMap { newsProvider.getActiveNewsId().toObservable() }
            .onErrorReturnItem("")
            .filter(String::isNotEmpty)
            .distinctUntilChanged()
            .share()

    private var newsCache: List<NewsItem>? = null
    val allNews: Observable<List<NewsItem>> = Observable
            .concat(
                    newsProvider.getNewsList().toObservable(),
                    activeNewsIds.flatMap { activeNewsId ->
                        val cache = newsCache
                        if (cache != null && cache.find { it.id == activeNewsId } != null) {
                            Observable.empty()
                        } else {
                            newsProvider.getNewsList().toObservable()
                        }
                    }
            )
            .subscribeOn(scheduler)
            .doOnNext { newsCache = it }
            .replay(1)
            .autoConnect()

    private val emptyNewsItem = NewsItem("", "", "", null, null)
    val activeNews: Observable<NewsItem> = Observable
            .combineLatest(
                    activeNewsIds,
                    allNews,
                    BiFunction<String, List<NewsItem>, NewsItem> { id, news ->
                        news.find { it.id == id } ?: emptyNewsItem
                    }
            )
            .filter { it != emptyNewsItem }
            .replay(1)
            .autoConnect()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}