package com.kvazars.radiot

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.kvazars.radiot.data.DataModule
import com.kvazars.radiot.di.AppComponent
import com.kvazars.radiot.di.DaggerAppComponent
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import com.kvazars.radiot.services.BackgroundPlayerService
import io.reactivex.plugins.RxJavaPlugins
import java.io.File

/**
 * Created by Leo on 01.05.2017.
 */
class RadioTApplication : Application() {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var appComponent: AppComponent
    private lateinit var serviceIntent: Intent

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    @SuppressWarnings("CheckReturnValue")
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .dataModule(DataModule(this, File(cacheDir, "http-cache")))
            .build()

        RxJavaPlugins.setErrorHandler { it.printStackTrace() }

        serviceIntent = BackgroundPlayerService.createLaunchIntent(this)

        appComponent
            .streamPlayer()
            .statusUpdates
            .distinctUntilChanged()
            .subscribe(
                {
                    when (it) {
                        PodcastStreamPlayer.Status.PLAYING, PodcastStreamPlayer.Status.BUFFERING ->
                            startBackgroundPlayerService()
                        PodcastStreamPlayer.Status.STOPPED, PodcastStreamPlayer.Status.ERROR ->
                            stopBackgroundPlayerService()
                    }
                },
                { it.printStackTrace() }
            )
    }

    private fun startBackgroundPlayerService() {
        ContextCompat.startForegroundService(
            this,
            serviceIntent
        )
    }

    private fun stopBackgroundPlayerService() {
        stopService(serviceIntent)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    companion object {
        fun getAppComponent(context: Context): AppComponent {
            return (context.applicationContext as RadioTApplication).appComponent
        }
    }

    //endregion
}