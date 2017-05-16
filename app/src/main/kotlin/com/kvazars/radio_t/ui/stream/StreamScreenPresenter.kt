package com.kvazars.radio_t.ui.stream

import com.kvazars.radio_t.domain.news.NewsInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

/**
 * Created by Leo on 27.04.2017.
 */
class StreamScreenPresenter(
        private val view: StreamScreenContract.View,
        private val newsInteractor: NewsInteractor
) : StreamScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    private val disposableBag = CompositeDisposable()

    init {
        disposableBag.add(
                newsInteractor
                        .activeNews
                        .observeOn(AndroidSchedulers.mainThread())
                        .map {
                            StreamScreenContract.View.NewsViewModel(
                                    it.title,
                                    it.id,
                                    System.currentTimeMillis(),
                                    it.snippet
                            )
                        }
                        .subscribe(
                                {
                                    view.setActiveNews(it)
                                },
                                {
                                    view.showReconnectSnackbar()
                                }
                        )
        )

        disposableBag.add(
                newsInteractor
                        .errorNotifications
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.showReconnectSnackbar()
                        }
        )
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onDestroy() {
        disposableBag.dispose()
    }

    override fun onPlaybackToggleClick() {
        view.setPlaybackState(if (Math.random() > 0.5f) StreamScreenContract.View.PlaybackState.BUFFERING else StreamScreenContract.View.PlaybackState.PLAYING)
    }

    override fun onInfoClick() {

    }

    override fun onSettingsClick() {

    }

    override fun onActiveNewsClick() {

    }

    override fun onReconnectClick() {
        newsInteractor.reconnect()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}