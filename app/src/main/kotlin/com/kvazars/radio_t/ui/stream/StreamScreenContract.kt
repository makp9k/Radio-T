package com.kvazars.radio_t.ui.stream

/**
 * Created by Leo on 08.04.2017.
 */
interface StreamScreenContract {
    interface View {
        enum class PlaybackState {
            BUFFERING, PAUSED, PLAYING
        }

        fun setPlaybackState(state: PlaybackState)

        data class NewsViewModel(
                val title: String,
                val author: String,
                val timestamp: Long,
                val details: String
        )

        fun setActiveNews(news: NewsViewModel)

        fun openNewsUrl(url: String)

        fun showReconnectSnackbar()
    }

    interface Presenter {
        fun onDestroy()

        fun onPlaybackToggleClick()

        fun onInfoClick()

        fun onSettingsClick()

        fun onReadMoreClick()

        fun onReconnectClick()
    }
}