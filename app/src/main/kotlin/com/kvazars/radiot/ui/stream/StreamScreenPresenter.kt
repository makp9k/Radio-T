package com.kvazars.radiot.ui.stream

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import com.kvazars.radiot.domain.stream.StreamInteractor
import com.kvazars.radiot.domain.util.Optional
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatterBuilder

/**
 * Created by Leo on 27.04.2017.
 */
class StreamScreenPresenter(
    private val view: StreamScreenContract.View,
    newsInteractor: NewsInteractor,
    streamInteractor: StreamInteractor,
    private val streamPlayer: PodcastStreamPlayer,
    private val reconnectTrigger: Observable<Unit>
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
            .flatMap({ streamInteractor.getStreamState().toObservable() }, { news, streamState -> Pair(news, streamState) })
            .retryWhen { reconnectTrigger }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val (news, streamState) = it
                    handleNewsStatus(news, streamState)
                }
            )
            .addTo(disposableBag)

        streamPlayer
            .statusUpdates
            .retryWhen { reconnectTrigger }
            .subscribe(
                {
                    handlePlayerStatus(it)
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
            PodcastStreamPlayer.Status.PLAYING, PodcastStreamPlayer.Status.BUFFERING ->
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.PLAYING)

            PodcastStreamPlayer.Status.STOPPED ->
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.STOPPED)

            PodcastStreamPlayer.Status.ERROR -> {
                view.setPlaybackState(StreamScreenContract.View.PlaybackState.ERROR)
            }
        }
    }

    private fun mapNewsViewModel(news: Optional<NewsItem>): Optional<StreamScreenContract.View.NewsViewModel> {
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
            streamPlayer.play("http://media.blubrry.com/zavtracast/s/zavtracast.ru/p/105_mixdown.mp3")
        } else {
            streamPlayer.stop()
        }
    }

    override fun onInfoClick() {
        view.openUrl("https://radio-t.com/info/")
    }

    override fun onActiveNewsClick() {
        activeNews.value?.link?.let { view.openUrl(it) }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}