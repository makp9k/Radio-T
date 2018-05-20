package com.kvazars.radiot.ui.stream

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import com.kvazars.radiot.domain.stream.StreamInteractor
import com.kvazars.radiot.domain.util.Optional
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatterBuilder

/**
 * Created by Leo on 27.04.2017.
 */
class StreamScreenPresenter(
    private val view: StreamScreenContract.View,
    newsInteractor: NewsInteractor,
    streamInteractor: StreamInteractor,
    private val streamPlayer: PodcastStreamPlayer
) : StreamScreenContract.Presenter {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private var activeNews: Optional<NewsItem> = Optional.empty

    private val disposableBag = CompositeDisposable()
    private val dateFormat = DateTimeFormatterBuilder()
        .appendPattern("dd MMM, HH:mm")
        .toFormatter()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor
            .activeNews
            .doOnNext { activeNews = it }
            .map(::mapNewsViewModel)
            .flatMap ({ streamInteractor.getStreamState().toObservable() }, { news, streamState -> Pair(news, streamState) })
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { view.showReconnectSnackbar() }
            .retryWhen { it.flatMap { reconnectSubject } }
            .subscribe(
                {
                    val (news, streamState) = it
                    handleNewsStatus(news, streamState)
                },
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
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onDestroy() {
        disposableBag.dispose()
    }

    private fun handlePlayerStatus(status: PodcastStreamPlayer.Status) {
        when (status) {
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

    private fun mapNewsViewModel(news:Optional<NewsItem>): Optional<StreamScreenContract.View.NewsViewModel> {
        val newsItem = news.value
        return if (newsItem != null) {
            Optional(
                StreamScreenContract.View.NewsViewModel(
                    newsItem.title,
                    "${newsItem.domain} - ${newsItem.time.withZoneSameInstant(ZoneId.systemDefault()).format(dateFormat)}",
                    newsItem.snippet,
                    newsItem.link,
                    newsItem.pictureUrl
                )
            )
        } else {
            Optional.empty
        }
    }

    private fun handleNewsStatus(
        news: Optional<StreamScreenContract.View.NewsViewModel>,
        streamState: StreamInteractor.StreamState
    ) {
        when (streamState) {
            is StreamInteractor.StreamState.Live -> {
                val activeNews = news.value
                if (activeNews != null) {
                    view.setActiveNews(activeNews)
                    view.showActiveNewsCard()
                } else {
                    view.showNoActiveNewsCard()
                }
            }
            is StreamInteractor.StreamState.Offline -> {
                view.showOfflineCard(streamState.airDate)
            }
        }
    }

    override fun onPlaybackToggleClick() {
        if (!streamPlayer.isPlaying()) {
            streamPlayer.play("http://stream.radio-t.com/")
        } else {
            streamPlayer.stop()
        }
    }

    override fun onInfoClick() {
        view.openUrl("https://radio-t.com/info/")
    }

    override fun onSettingsClick() {

    }

    override fun onActiveNewsClick() {
        activeNews.value?.link?.let { view.openUrl(it) }
    }

    var reconnectSubject = PublishSubject.create<Boolean>()!!
    override fun onReconnectClick() {
        reconnectSubject.onNext(true)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}