package com.kvazars.radiot.data.gitter

import com.kvazars.radiot.data.gitter.auth.GitterAuthHelper
import com.kvazars.radiot.data.gitter.models.ChatEvent
import com.kvazars.radiot.data.gitter.models.ChatMessage
import com.kvazars.radiot.data.gitter.models.ChatMessageAdd
import com.kvazars.radiot.data.gitter.rest.GitterReadonlyRestClient
import com.kvazars.radiot.data.gitter.streaming.GitterReadonlyStreamingClient
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
class GitterClientFacade(
    httpClient: OkHttpClient = OkHttpClient(),
    private val scheduler: Scheduler = Schedulers.io()
) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    private val authHelper = GitterAuthHelper(httpClient)
    private val streamingClient = GitterReadonlyStreamingClient(httpClient, scheduler)
    private val restClient = GitterReadonlyRestClient(httpClient)

    private val accessDataObservable = Observable
        .defer {
            authHelper
                .getAccessData("radio-t/chat")
//                        .getAccessData("testtestasd/Lobby")
                .toObservable()
        }
        .replay(1)
        .refCount()

    val stream: Observable<ChatEvent> =
        Observable
            .defer {
                accessDataObservable
                    .observeOn(scheduler)
                    .switchMap { accessData ->
                        streamingClient.connect(accessData)
                            .startWith(
                                getLastMessages(accessData.accessToken, accessData.roomId)
                                    .map { ChatMessageAdd(it) }

                            )
                            .compose<ChatEvent>(applyRetry())
                    }
                    .compose<ChatEvent>(applyRetry())
            }
            .replay(1)
            .refCount()
            .doOnError { println(it.toString() + "!@!@!@!@") }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getMessagesBefore(messageId: String): Single<List<ChatMessage>> {
        return accessDataObservable
            .singleOrError()
            .subscribeOn(scheduler)
            .flatMap {
                restClient.getMessagesBefore(it.accessToken, it.roomId, messageId)
                    .flatMapObservable { Observable.fromIterable(it) }
                    .toList()
            }
    }

    fun getMessagesAfter(messageId: String): Single<List<ChatMessage>> {
        return accessDataObservable
            .singleOrError()
            .subscribeOn(scheduler)
            .flatMap {
                restClient.getMessagesAfter(it.accessToken, it.roomId, messageId)
                    .flatMapObservable { Observable.fromIterable(it) }
                    .toList()
            }
    }

    private fun getLastMessages(accessToken: String, roomId: String): Observable<ChatMessage> {
        return restClient.getLastMessages(accessToken, roomId).toObservable().flatMapIterable { it }
    }

    private fun <T> applyRetry(): ObservableTransformer<in T, out T>? {
        return ObservableTransformer {
            it.retryWhen {
                it.zipWith(Observable.rangeLong(1, 3).startWithArray(1, 1, 1),
                    BiFunction<Throwable, Long, Pair<Throwable, Long>> { e, i -> Pair(e, i) })
                    .flatMap {
                        when (it.second) {
                            3L -> Observable.error(it.first)
                            else -> Observable.timer(it.second, TimeUnit.SECONDS, scheduler)
                        }
                    }
            }
        }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}