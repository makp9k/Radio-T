package com.kvazars.radiot.ui.news

import com.kvazars.radiot.domain.news.NewsInteractor

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

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun onDestroy() {

    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}