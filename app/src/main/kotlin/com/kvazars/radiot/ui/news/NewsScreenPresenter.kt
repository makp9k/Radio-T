package com.kvazars.radiot.ui.news

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.utils.save
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Leo on 27.04.2017.
 */
class NewsScreenPresenter(
    private val view: NewsScreenContract.View,
    newsInteractor: NewsInteractor
) : NewsScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val disposableBag = CompositeDisposable()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor.allNews.subscribe(
            { println(it) }
        ).save(disposableBag)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun onDestroy() {
        disposableBag.clear()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}