package com.kvazars.radio_t.ui.stream

import com.kvazars.radio_t.domain.news.NewsInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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
                        .map {
                            StreamScreenContract.View.NewsViewModel(
                                    it.title,
                                    it.id,
                                    System.currentTimeMillis(),
                                    it.snippet
                            )
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnError { view.showReconnectSnackbar() }
                        .retryWhen { it.flatMap { reconnectSubject } }
                        .subscribe { view.setActiveNews(it) }
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

    var reconnectSubject = PublishSubject.create<Boolean>()
    override fun onReconnectClick() {
        reconnectSubject.onNext(true)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}