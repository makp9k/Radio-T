package com.kvazars.radiot

import android.content.Context
import android.support.multidex.MultiDex

class MultiDexRadioTApplication : RadioTApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}