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
                val author: String,
                val text: String
        )

        fun setCurrentNews(news: NewsViewModel)
    }

    interface Presenter {
        fun onPlaybackToggleClick()

        fun onInfoClick()

        fun onSettingsClick()

        fun onCurrentNewsClick()
    }
}