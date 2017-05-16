package com.kvazars.radio_t.domain.news

import com.kvazars.radio_t.domain.news.models.ChatMessageNotification
import com.kvazars.radio_t.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
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

    private val reconnectTrigger = PublishSubject.create<Boolean>()

    private val activeNewsIds: Observable<String> = activeNewsUpdateTrigger
            .startWith(true)
            .buffer(1, TimeUnit.SECONDS, scheduler)
            .filter { !it.isEmpty() }
            .switchMap { newsProvider.getActiveNewsId().toObservable() }
            .filter(String::isNotEmpty)
            .distinctUntilChanged()
            .share()

    val errorNotifications: PublishSubject<Throwable> = PublishSubject.create<Throwable>()

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
            .doOnError { errorNotifications.onNext(it) }
            .compose<List<NewsItem>>(applyRetry())
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
            .doOnError { errorNotifications.onNext(it) }
            .compose<NewsItem>(applyRetry())
            .replay(1)
            .autoConnect()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun reconnect() {
        reconnectTrigger.onNext(true)
    }

    private fun <T> applyRetry(): ObservableTransformer<in T, out T>? {
        return ObservableTransformer {
            it.retryWhen {
                Observable.merge(it.zipWith(Observable.rangeLong(1, 3),
                        BiFunction<Throwable, Long, Long> { e, i -> e.printStackTrace(); i }
                )
                        .flatMap {
                            Observable.timer(it, TimeUnit.SECONDS, scheduler)
                        },
                        it.zipWith(reconnectTrigger, BiFunction<Throwable, Boolean, Boolean> { e, i -> e.printStackTrace(); i })
                )
                        .doOnNext { println("RETRY!") }
            }
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}