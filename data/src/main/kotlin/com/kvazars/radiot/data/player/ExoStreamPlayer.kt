package com.kvazars.radiot.data.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ExoStreamPlayer(
    context: Context,
    private val okHttpClient: OkHttpClient
) : PodcastStreamPlayer {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val statusUpdatesRelay = BehaviorSubject.create<PodcastStreamPlayer.Status>()

    override val currentPositions: Observable<Long>
        get() = Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .map { exoPlayer.currentPosition }

    override val statusUpdates: Observable<PodcastStreamPlayer.Status>
        get() = statusUpdatesRelay

    private var exoPlayer: ExoPlayer = ExoPlayerFactory.newSimpleInstance(
        DefaultRenderersFactory(context),
        DefaultTrackSelector(DefaultBandwidthMeter()),
        DefaultLoadControl()
    )

    private var isPlaying = false

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        exoPlayer.addListener(Listener())
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun play(streamUrl: String) {
        if (isPlaying) return

        val audioUri = Uri.parse(streamUrl)
        val dataSourceFactory = OkHttpDataSourceFactory(
            okHttpClient,
            "Radio-T Android App",
            null
        )
        val extractor = DefaultExtractorsFactory()
        val audioSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(extractor)
            .createMediaSource(audioUri)

        exoPlayer.prepare(audioSource)
        exoPlayer.playWhenReady = true
    }

    override fun stop() {
        if (!isPlaying) return

        exoPlayer.stop()
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    //endregion

    //region ACCESSORS -----------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    inner class Listener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

        }

        override fun onSeekProcessed() {

        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        ) {

        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            statusUpdatesRelay.onNext(
                PodcastStreamPlayer.Status.ERROR
            )
        }

        override fun onLoadingChanged(isLoading: Boolean) {
            if (isLoading) {
                statusUpdatesRelay.onNext(
                    PodcastStreamPlayer.Status.BUFFERING
                )
            }
        }

        override fun onPositionDiscontinuity(reason: Int) {

        }

        override fun onRepeatModeChanged(repeatMode: Int) {

        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

        }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    isPlaying = true

                    statusUpdatesRelay.onNext(
                        PodcastStreamPlayer.Status.BUFFERING
                    )
                }
                Player.STATE_ENDED, Player.STATE_IDLE -> {
                    isPlaying = false

                    statusUpdatesRelay.onNext(
                        PodcastStreamPlayer.Status.STOPPED
                    )
                }
                Player.STATE_READY -> {
                    isPlaying = true

                    statusUpdatesRelay.onNext(
                        if (exoPlayer.playWhenReady) {
                            PodcastStreamPlayer.Status.PLAYING
                        } else {
                            PodcastStreamPlayer.Status.STOPPED
                        }
                    )
                }
            }
        }

    }

    //endregion

}