package com.kvazars.radio_t.gitter

import com.kvazars.radio_t.gitter.auth.GitterAuthHelper
import com.kvazars.radio_t.gitter.models.ChatMessage
import com.kvazars.radio_t.gitter.rest.GitterReadonlyRestClient
import com.kvazars.radio_t.gitter.streaming.GitterReadonlyStreamingClient
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 28.02.2017.
 */
class GitterClientFacade(httpClient: OkHttpClient = OkHttpClient(),
                         private val scheduler: Scheduler = Schedulers.io()) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    private val authHelper = GitterAuthHelper(httpClient)
    private val streamingClient = GitterReadonlyStreamingClient(httpClient, scheduler)
    private val restClient = GitterReadonlyRestClient(httpClient)

    private val reconnectSubject = PublishSubject.create<Boolean>()

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getMessageStream(): Observable<ChatMessage> {
        return authHelper.getAccessData("testtestasd/Lobby")
                .flatMapObservable { streamingClient.connect(it).compose(applyRetry()) }
                .compose(applyRetry())
    }

    fun reconnect() {
        reconnectSubject.onNext(true)
    }

    fun getLastMessages(count: Int): Single<List<ChatMessage>> {
        return restClient.getLastMessages(count)
    }

    private fun <T> applyRetry(): ObservableTransformer<in T, out T>? {
        return ObservableTransformer {
            it.retryWhen {
                Observable.merge(
                        it.zipWith(Observable.rangeLong(1, 10000).startWithArray(1, 1, 1)
                                .map {
                                    Math.min(Math.pow(2.0, it * 1.0).toLong(), 15)
                                },
                                BiFunction<Throwable, Long, Long> { e, i -> i })
                                .flatMap {
                                    Observable.timer(it, TimeUnit.SECONDS, scheduler)
                                },
                        reconnectSubject.observeOn(scheduler))
            }
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}