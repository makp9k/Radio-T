package com.kvazars.radiot.ui.settings

/**
 * Created by Leo on 08.04.2017.
 */
interface SettingsScreenContract {
    interface View {
        fun setNotificationsEnabledSwitchChecked(checked: Boolean)
        fun setTrackingEnabledSwitchChecked(checked: Boolean)
    }

    interface Presenter {
        fun onNotificationsEnabledChanged(checked: Boolean)
        fun onTrackingEnabledChecked(checked: Boolean)
    }
}