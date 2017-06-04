package com.kvazars.radiot

import android.app.Application
import android.content.Context
import com.kvazars.radiot.di.AppComponent
import com.kvazars.radiot.di.AppModule
import com.kvazars.radiot.di.DaggerAppComponent
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by Leo on 01.05.2017.
 */
class RadioTApplication : Application() {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var appComponent: AppComponent

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()

        RxJavaPlugins.setErrorHandler { it.printStackTrace() }
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