package com.kvazars.radiot.ui.settings

import com.kvazars.radiot.domain.preferences.ApplicationPreferences

/**
 * Created by Leo on 27.04.2017.
 */
class SettingsScreenPresenter(
    view: SettingsScreenContract.View,
    private val appPreferences: ApplicationPreferences
) : SettingsScreenContract.Presenter {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    init {
        view.setNotificationsEnabledSwitchChecked(appPreferences.notificationsEnabled.get())
        view.setTrackingEnabledSwitchChecked(appPreferences.trackingEnabled.get())
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onNotificationsEnabledChanged(checked: Boolean) {
        appPreferences.notificationsEnabled.set(checked)
    }

    override fun onTrackingEnabledChecked(checked: Boolean) {
        appPreferences.trackingEnabled.set(checked)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}