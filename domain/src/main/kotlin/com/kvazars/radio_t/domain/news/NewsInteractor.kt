package com.kvazars.radio_t.domain.news

import com.kvazars.radio_t.domain.news.models.ChatMessageNotification
import com.kvazars.radio_t.domain.news.usecase.GetActiveNewsUseCase
import com.kvazars.radio_t.domain.news.usecase.GetAllNewsUseCase
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 19.03.2017.
 */
class NewsInteractor(chatMessageNotifications: Observable<ChatMessageNotification>,
                     newsProvider: NewsProvider,
                     private val scheduler: Scheduler = Schedulers.io()) {
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
            .filter(String::isNotEmpty)
            .distinctUntilChanged()
            .share()

    private val getAllNewsUseCase = GetAllNewsUseCase(
            activeNewsIds, newsProvider.getNewsList()
    )

    val allNews = Observable
            .defer {
                getAllNewsUseCase
                        .allNews
                        .replay(1)
                        .autoConnect()
            }
            .share()
            .subscribeOn(scheduler)!!

    private val getActiveNewsUseCase = GetActiveNewsUseCase(
            activeNewsIds,
            allNews
    )

    val activeNews = Observable
            .defer {
                getActiveNewsUseCase
                        .activeNews
                        .replay(1)
                        .autoConnect()
            }
            .share()
            .subscribeOn(scheduler)!!

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}