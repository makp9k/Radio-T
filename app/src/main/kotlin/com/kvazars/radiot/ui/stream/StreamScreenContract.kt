package com.kvazars.radiot.ui.stream

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

        fun setActiveNews(news: NewsViewModel?)

        fun openNewsUrl(url: String)

        fun showReconnectSnackbar()
    }

    interface Presenter {
        fun onDestroy()

        fun onPlaybackToggleClick()

        fun onInfoClick()

        fun onSettingsClick()

        fun onActiveNewsClick()

        fun onReconnectClick()
    }
}