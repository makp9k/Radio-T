package com.kvazars.radiot.ui.settings

import android.databinding.ObservableBoolean
import com.kvazars.radiot.data.preferences.PreferenceBinding
import com.kvazars.radiot.data.preferences.connectTo
import com.kvazars.radiot.domain.preferences.ApplicationPreferences

/**
 * Created by Leo on 27.04.2017.
 */
class SettingsScreenPresenter(
    appPreferences: ApplicationPreferences
) : SettingsScreenContract.Presenter {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val disposableBag = mutableListOf<PreferenceBinding<*>>()

    override val notificationsEnabled = ObservableBoolean(false)
        .apply { appPreferences.notificationsEnabled.connectTo(this).apply { disposableBag += this } }

    override val trackingEnabled = ObservableBoolean(false)
        .apply {
            appPreferences.trackingEnabled.connectTo(this).apply { disposableBag += this }
        }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun dispose() {
        disposableBag.forEach { it.dispose() }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}