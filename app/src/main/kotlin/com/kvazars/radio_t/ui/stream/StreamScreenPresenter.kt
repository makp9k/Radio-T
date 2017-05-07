package com.kvazars.radio_t.ui.stream

import com.kvazars.radio_t.domain.news.NewsInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Leo on 27.04.2017.
 */
class StreamScreenPresenter(
        private val view: StreamScreenContract.View,
        newsInteractor: NewsInteractor
) : StreamScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor
                .activeNews
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    StreamScreenContract.View.NewsViewModel(
                            it.title,
                            it.id,
                            System.currentTimeMillis(),
                            it.snippet
                    )
                }
//                .subscribe({
//                    view.setActiveNews(it)
//                }, Throwable::printStackTrace)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onPlaybackToggleClick() {
        view.setPlaybackState(if (Math.random() > 0.5f) StreamScreenContract.View.PlaybackState.BUFFERING else StreamScreenContract.View.PlaybackState.PLAYING)
    }

    override fun onInfoClick() {

    }

    override fun onSettingsClick() {

    }

    override fun onActiveNewsClick() {

    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}