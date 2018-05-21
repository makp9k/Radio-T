package com.kvazars.radiot.ui.stream

import org.threeten.bp.ZonedDateTime

/**
 * Created by Leo on 08.04.2017.
 */
interface StreamScreenContract {
    interface View {
        enum class PlaybackState {
            BUFFERING, STOPPED, PLAYING, ERROR
        }

        fun setPlaybackState(state: PlaybackState)

        data class NewsViewModel(
            val title: String,
            val footer: String,
            val details: String,
            val link: String,
            val pictureUrl: String?
        )

        fun setActiveNews(news: NewsViewModel)

        fun showActiveNewsCard()

        fun showNoActiveNewsCard()

        fun showOfflineCard(airDate: ZonedDateTime)

        fun openUrl(url: String)
    }

    interface Presenter {
        fun onDestroy()

        fun onPlaybackToggleClick()

        fun onInfoClick()

        fun onSettingsClick()

        fun onActiveNewsClick()
    }
}