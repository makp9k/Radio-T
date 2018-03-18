package com.kvazars.radiot.domain.player

import io.reactivex.Observable

/**
 * Created by lza on 12.02.2018.
 */
interface PodcastStreamPlayer {

    enum class Status {
        PLAYING,
        STOPPED,
        BUFFERING,
        ERROR,
    }

    val currentPositions: Observable<Long>

    val statusUpdates: Observable<Status>

    fun play(streamUrl: String)

    fun stop()

    fun isPlaying(): Boolean
}
