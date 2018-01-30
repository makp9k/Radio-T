package com.kvazars.radiot.ui.stream

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.util.Optional
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

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

    private var activeNews: Optional<NewsItem> = Optional.empty

    private val disposableBag = CompositeDisposable()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        disposableBag.add(
                newsInteractor
//                        .allNews
//                        .flatMapIterable{it}
                        .activeNews
                        .doOnNext { activeNews = it }
                    .delay(3, TimeUnit.SECONDS)
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