package com.kvazars.radiot.domain.preferences

interface ApplicationPreferences {

    val notificationsEnabled: Preference<Boolean>

    val crashReportingEnabled: Preference<Boolean>
}