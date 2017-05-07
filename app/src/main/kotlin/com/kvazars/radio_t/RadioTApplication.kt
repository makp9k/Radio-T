package com.kvazars.radio_t

import android.app.Application
import android.content.Context
import com.kvazars.radio_t.di.AppComponent
import com.kvazars.radio_t.di.AppModule
import com.kvazars.radio_t.di.DaggerAppComponent

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