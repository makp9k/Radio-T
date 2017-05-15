package com.kvazars.radio_t.ui.stream

import com.kvazars.radio_t.domain.news.NewsInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
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

    init {
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
                .doOnSubscribe { println(it) }
                .doOnError { view.showReconnectSnackbar() }
                .retryWhen { t -> t.flatMap { subject } }
                .subscribe(
                        {
                            view.setActiveNews(it)
                        },
                        {
                            view.showReconnectSnackbar()
                        }
                )
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

    private val subject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    override fun onReconnectClick() {
        subject.onNext(true)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}