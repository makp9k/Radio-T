package com.kvazars.radiot.ui.main

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenPresenter(
        private val view: MainScreenContract.View,
        newsInteractor: NewsInteractor
) : MainScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val reconnectSubject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    private val disposableBag = CompositeDisposable()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor
            .activeNews
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { println(it); view.showReconnectSnackbar() }
            .retryWhen { it.flatMap { reconnectSubject } }
            .subscribe()
            .addTo(disposableBag)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onReconnectClick() {
        reconnectSubject.onNext(true)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}