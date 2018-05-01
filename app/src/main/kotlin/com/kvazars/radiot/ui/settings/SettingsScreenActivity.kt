package com.kvazars.radiot.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.CompoundButton
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import kotlinx.android.synthetic.main.screen_settings.*

/**
 * Created by Leo on 12.04.2017.
 */
class SettingsScreenActivity : AppCompatActivity(), SettingsScreenContract.View {

    //region CONSTANTS -----------------------------------------------------------------------------

    companion object {

        fun createLaunchIntent(context: Context): Intent {
            return Intent(context, SettingsScreenActivity::class.java)
        }

    }

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var presenter: SettingsScreenPresenter

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.screen_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val appComponent = RadioTApplication.getAppComponent(this)
        presenter = SettingsScreenPresenter(this, appComponent.appPreferences())

        notification_switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            presenter.onNotificationsEnabledChanged(isChecked)
        })

        tracking_switch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            presenter.onTrackingEnabledChecked(isChecked)
        })
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setNotificationsEnabledSwitchChecked(checked: Boolean) {
        notification_switch.isChecked = checked
    }

    override fun setTrackingEnabledSwitchChecked(checked: Boolean) {
        tracking_switch.isChecked = checked
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}