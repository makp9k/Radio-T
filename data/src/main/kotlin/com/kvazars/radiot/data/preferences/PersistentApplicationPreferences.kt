package com.kvazars.radiot.data.preferences

import android.content.SharedPreferences
import com.kvazars.radiot.domain.preferences.ApplicationPreferences

class PersistentApplicationPreferences(prefs: SharedPreferences) : ApplicationPreferences {

    override val notificationsEnabled by prefs.boolean(true)

    override val trackingEnabled by prefs.boolean(true)

//    override val trackingEnabled by prefs.generic(
//        true,
//        { key, defaultValue -> getBoolean(key, defaultValue) },
//        { key, value -> edit().putBoolean(key, value).apply() }
//    )
}