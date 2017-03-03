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

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getMessageStream(): Observable<ChatMessage> {
        return authHelper.getAccessData("testtestasd/Lobby")
                .flatMapObservable { streamingClient.connect(it) }
                .compose(applyRetry())
    }

    fun getLastMessages(count: Int): Single<List<ChatMessage>> {
        return restClient.getLastMessages(count)
    }

    private fun applyRetry(): ObservableTransformer<in ChatMessage, out ChatMessage>? {
        return ObservableTransformer {
            it.retryWhen {
                it.zipWith(Observable.just(0, 0, 0, 1, 2), BiFunction<Throwable, Int, Int> { e, i -> i })
                        .flatMap { Observable.timer(Math.pow(3.0, it * 1.0).toLong(), TimeUnit.SECONDS, scheduler) }
            }
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}