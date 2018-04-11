package com.kvazars.radiot.domain.stream

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.DayOfWeek
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.temporal.ChronoUnit
import org.threeten.bp.temporal.TemporalAdjusters

class StreamInteractor(
    private val streamInfoProvider: StreamInfoProvider,
    private val scheduler: Scheduler = Schedulers.io()
) {

    sealed class StreamState {
        class Live : StreamState()
        class Offline(val airDate: ZonedDateTime): StreamState()
    }

    fun getStreamState(): Single<StreamState> {
        return streamInfoProvider
            .isOnAir()
            .map {
                if (it) {
                    StreamState.Live()
                } else {
                    StreamState.Offline(
                        ZonedDateTime.now(ZoneId.of("Europe/Moscow"))
                            .with(TemporalAdjusters.next(DayOfWeek.SATURDAY))
                            .truncatedTo(ChronoUnit.DAYS)
                            .withHour(23)
                    )
                }
            }
            .subscribeOn(scheduler)
    }

}