package com.kvazars.radio_t.ui.chat

import com.kvazars.radio_t.data.gitter.GitterClientFacade
import com.kvazars.radio_t.ui.chat.ChatScreenContract
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Leo on 27.04.2017.
 */
class ChatScreenPresenter(
        private val view: ChatScreenContract.View,
        gitterClient: GitterClientFacade
) : ChatScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    private val disposableBag = CompositeDisposable()

    init {
        disposableBag.addAll(
                gitterClient
                        .getMessageStream()
                        .subscribe(
                                {
                                    println(it)
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
        )
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onDestroy() {
        disposableBag.dispose()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}