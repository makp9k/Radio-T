package com.kvazars.radiot.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kvazars.radiot.BuildConfig
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import com.kvazars.radiot.databinding.ScreenSettingsBinding
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

        val appComponent = RadioTApplication.getAppComponent(this)
        presenter = SettingsScreenPresenter(appComponent.appPreferences())

        val binding = DataBindingUtil.setContentView<ScreenSettingsBinding>(this, R.layout.screen_settings)
        binding.model = presenter

        binding.rateAppBtn.setOnClickListener {
            val uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        binding.sourceCodeBtn.setOnClickListener {
            CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
                .launchUrl(this, Uri.parse("https://github.com/Makp9k/Radio-T"))
        }

        binding.appVersion.text = BuildConfig.VERSION_NAME

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onDestroy() {
        presenter.dispose()
        super.onDestroy()
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

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}