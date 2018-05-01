package com.kvazars.radiot.data.preferences

import android.content.SharedPreferences
import com.kvazars.radiot.domain.preferences.BasePreference
import com.kvazars.radiot.domain.preferences.Preference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PersistentPreference<T>(
    reader: () -> T,
    private val writer: (T) -> Unit
) : BasePreference<T>(
    reader()
) {
    override fun set(value: T) {
        writer(value)
        super.set(value)
    }
}

private open class PersistentPreferenceDelegate<T>(
    private val initializer: (propertyName: String) -> Preference<T>
) : ReadOnlyProperty<Any, Preference<T>> {
    private lateinit var preference: Preference<T>

    override fun getValue(thisRef: Any, property: KProperty<*>): Preference<T> {
        if (!::preference.isInitialized) {
            preference = initializer(property.name)
        }
        return preference
    }
}

fun SharedPreferences.string(defaultValue: String): ReadOnlyProperty<Any, Preference<String>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getString(key, defaultValue) },
            { value -> edit().putString(key, value).apply() }
        )
    }
}

fun SharedPreferences.stringSet(defaultValue: Set<String>): ReadOnlyProperty<Any, Preference<Set<String>>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getStringSet(key, defaultValue) },
            { value -> edit().putStringSet(key, value).apply() }
        )
    }
}

fun SharedPreferences.int(defaultValue: Int): ReadOnlyProperty<Any, Preference<Int>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getInt(key, defaultValue) },
            { value -> edit().putInt(key, value).apply() }
        )
    }
}

fun SharedPreferences.long(defaultValue: Long): ReadOnlyProperty<Any, Preference<Long>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getLong(key, defaultValue) },
            { value -> edit().putLong(key, value).apply() }
        )
    }
}

fun SharedPreferences.float(defaultValue: Float): ReadOnlyProperty<Any, Preference<Float>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getFloat(key, defaultValue) },
            { value -> edit().putFloat(key, value).apply() }
        )
    }
}

fun SharedPreferences.boolean(defaultValue: Boolean): ReadOnlyProperty<Any, Preference<Boolean>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { getBoolean(key, defaultValue) },
            { value -> edit().putBoolean(key, value).apply() }
        )
    }
}

fun <T> SharedPreferences.generic(
    defaultValue: T,
    reader: SharedPreferences.(key: String, defaultValue: T) -> T,
    writer: SharedPreferences.(key: String, value: T) -> Unit
): ReadOnlyProperty<Any, Preference<T>> {
    return PersistentPreferenceDelegate { key ->
        PersistentPreference(
            { reader(key, defaultValue) },
            { value -> writer(key, value) }
        )
    }
}