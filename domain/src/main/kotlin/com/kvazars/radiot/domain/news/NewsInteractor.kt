package com.kvazars.radiot.domain.news

import com.kvazars.radiot.domain.news.models.ChatMessageNotification
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.news.usecase.GetActiveNewsUseCase
import com.kvazars.radiot.domain.news.usecase.GetAllNewsUseCase
import com.kvazars.radiot.domain.util.Optional
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

import java.util.concurrent.TimeUnit

/**
 * Created by lza on 19.03.2017.
 */
class NewsInteractor(
    chatMessageNotifications: Observable<ChatMessageNotification>,
    newsProvider: NewsProvider,
    scheduler: Scheduler = Schedulers.io()
) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val activeNewsUpdateTrigger = chatMessageNotifications
        .filter { it.authorName == "rt-bot" && it.message.startsWith("==>") }
        .map { true }
        .subscribeOn(scheduler)

    private val activeNewsIds: Observable<String> = activeNewsUpdateTrigger
        .startWith(true)
        .buffer(1, TimeUnit.SECONDS, scheduler)
        .filter { !it.isEmpty() }
        .switchMap { newsProvider.getActiveNewsId().toObservable() }
        .distinctUntilChanged()
        .share()

    private val getAllNewsUseCase = GetAllNewsUseCase(
        activeNewsIds, newsProvider.getNewsList()
    )

    val allNews: Observable<List<NewsItem>> = Observable
        .defer {
            getAllNewsUseCase
                .allNews
        }
        .replay(1)
        .refCount()
        .subscribeOn(scheduler)

    private val getActiveNewsUseCase = GetActiveNewsUseCase(
        activeNewsIds,
        allNews
    )

    val activeNews: Observable<Optional<NewsItem>> = Observable
        .defer {
            getActiveNewsUseCase
                .activeNews
        }
        .replay(1)
        .refCount()
        .subscribeOn(scheduler)

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}