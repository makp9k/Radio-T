package com.kvazars.radiot.ui.base

import android.support.v7.app.AppCompatActivity
import net.hockeyapp.android.CrashManager

/**
 * Created by Leo on 08.04.2017.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        CrashManager.register(this)
    }

}