package com.kvazars.radiot.ui.settings

import android.databinding.ObservableBoolean

/**
 * Created by Leo on 08.04.2017.
 */
interface SettingsScreenContract {
    interface View {

    }

    interface Presenter {
        val notificationsEnabled: ObservableBoolean
        val trackingEnabled: ObservableBoolean

        fun dispose()
    }
}