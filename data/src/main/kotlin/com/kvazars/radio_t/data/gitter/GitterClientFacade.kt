package com.kvazars.radio_t.data.gitter

import com.kvazars.radio_t.data.gitter.auth.GitterAuthHelper
import com.kvazars.radio_t.data.gitter.models.ChatMessage
import com.kvazars.radio_t.data.gitter.rest.GitterReadonlyRestClient
import com.kvazars.radio_t.data.gitter.streaming.GitterReadonlyStreamingClient
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Scheduler
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

    private val accessDataObservable = reconnectSubject.startWith(true)
            .flatMap { authHelper.getAccessData("testtestasd/Lobby").toObservable() }
            .replay(1)
            .autoConnect()

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getMessageStream(): Observable<ChatMessage> {
        return accessDataObservable
                .observeOn(scheduler)
                .switchMap { accessData ->
                    getLastMessages(accessData.accessToken, accessData.roomId)
                            .concatWith(streamingClient.connect(accessData))
                            .compose<ChatMessage>(applyRetry())
                }
                .compose<ChatMessage>(applyRetry())
    }

    fun getMessagesBefore(messageId: String, count: Int): Observable<List<ChatMessage>> {
        return accessDataObservable
                .observeOn(scheduler)
                .take(1)
                .flatMap {
                    restClient.getMessagesBefore(it.accessToken, it.roomId, messageId, count)
                            .flatMapObservable { Observable.fromIterable(it) }
                            .toList()
                            .toObservable()
                }
    }

    fun getMessagesAfter(messageId: String, count: Int): Observable<List<ChatMessage>> {
        return accessDataObservable
                .observeOn(scheduler)
                .take(1)
                .flatMap {
                    restClient.getMessagesAfter(it.accessToken, it.roomId, messageId, count)
                            .flatMapObservable { Observable.fromIterable(it) }
                            .toList()
                            .toObservable()
                }
    }

    fun reconnect() {
        reconnectSubject.onNext(true)
    }

    private fun getLastMessages(accessToken: String, roomId: String): Observable<ChatMessage> {
        return restClient.getLastMessages(accessToken, roomId).toObservable().flatMapIterable { it }
    }

    private fun <T> applyRetry(): ObservableTransformer<in T, out T>? {
        return ObservableTransformer {
            it.retryWhen {
                it.zipWith(Observable.rangeLong(1, 10000).startWithArray(1, 1, 1)
                        .map {
                            Math.min(Math.pow(2.0, it * 1.0).toLong(), 15)
                        },
                        BiFunction<Throwable, Long, Long> { e, i -> e.printStackTrace(); i })
                        .flatMap {
                            Observable.timer(it, TimeUnit.SECONDS, scheduler)
                        }
            }
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}