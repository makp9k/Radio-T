package com.kvazars.radiot.ui.main

/**
 * Created by Leo on 08.04.2017.
 */
interface MainScreenContract {
    interface View {
        fun showReconnectSnackbar()
    }

    interface Presenter {
        fun onReconnectClick()
    }
}