package com.kvazars.radiot.ui.main

import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

/**
 * Created by Leo on 08.04.2017.
 */
class MainScreenPresenter(
    private val view: MainScreenContract.View,
    newsInteractor: NewsInteractor,
    private val reconnectTrigger: PublishSubject<Unit>
) : MainScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val disposableBag = CompositeDisposable()

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        newsInteractor
            .activeNews
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { view.showReconnectSnackbar() }
            .retryWhen { reconnectTrigger.delay(300, TimeUnit.MILLISECONDS) }
            .subscribe()
            .addTo(disposableBag)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onReconnectClick() {
        reconnectTrigger.onNext(Unit)
    }

    override fun onDestroy() {
        disposableBag.clear()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}