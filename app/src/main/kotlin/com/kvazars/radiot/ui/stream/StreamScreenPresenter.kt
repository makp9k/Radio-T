package com.kvazars.radiot.ui.stream

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import com.kvazars.radiot.domain.util.Optional
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Leo on 27.04.2017.
 */
class StreamScreenPresenter(
    private val view: StreamScreenContract.View,
    newsInteractor: NewsInteractor,
    private val streamPlayer: PodcastStreamPlayer
) : StreamScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private var activeNews: Optional<NewsItem> = Optional.empty

    private val disposableBag = CompositeDisposable()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor
            .activeNews
            .doOnNext { activeNews = it }
            .map {
                val newsItem = it.value
                if (newsItem != null) {
                    StreamScreenContract.View.NewsViewModel(
                        newsItem.title,
                        System.currentTimeMillis(),
                        newsItem.snippet,
                        newsItem.pictureUrl
                    )
                } else {
                    StreamScreenContract.View.NewsViewModel(
                        "Empty",
                        System.currentTimeMillis(),
                        "Empty", null
                    )
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { view.showReconnectSnackbar() }
            .retryWhen { it.flatMap { reconnectSubject } }
            .subscribe(
                { view.setActiveNews(it) },
                { it.printStackTrace() }
            )
            .addTo(disposableBag)

        streamPlayer
            .statusUpdates
            .subscribe(
                {
                    handlePlayerStatus(it)
                },
                {
                    it.printStackTrace()
                }
            )
            .addTo(disposableBag)

        onPlaybackToggleClick()
    }

    private fun handlePlayerStatus(status: PodcastStreamPlayer.Status) {
        when(status) {
            PodcastStreamPlayer.Status.PLAYING ->
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.PLAYING)

            PodcastStreamPlayer.Status.STOPPED ->
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.STOPPED)

            PodcastStreamPlayer.Status.ERROR -> {
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.ERROR)
            }

            PodcastStreamPlayer.Status.BUFFERING ->
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.STOPPED)
        }
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onDestroy() {
        disposableBag.dispose()
    }

    override fun onPlaybackToggleClick() {
        if (!streamPlayer.isPlaying()) {
            streamPlayer.play("http://stream.radio-t.com/")
        } else {
            streamPlayer.stop()
        }
    }

    override fun onInfoClick() {

    }

    override fun onSettingsClick() {

    }

    override fun onReadMoreClick() {
        activeNews.value?.link?.let { view.openNewsUrl(it) }
    }

    var reconnectSubject = PublishSubject.create<Boolean>()!!
    override fun onReconnectClick() {
        reconnectSubject.onNext(true)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}