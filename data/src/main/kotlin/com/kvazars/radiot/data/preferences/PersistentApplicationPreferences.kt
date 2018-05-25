package com.kvazars.radiot.data.preferences

import android.content.SharedPreferences
import com.kvazars.radiot.domain.preferences.ApplicationPreferences

class PersistentApplicationPreferences(prefs: SharedPreferences) : ApplicationPreferences {

    override val notificationsEnabled by prefs.boolean(true)

    override val crashReportingEnabled by prefs.boolean(false)
}