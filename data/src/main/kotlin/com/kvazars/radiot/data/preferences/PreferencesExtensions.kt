package com.kvazars.radiot.data.preferences

import android.databinding.*
import com.kvazars.radiot.domain.preferences.Preference

fun <T> Preference<T>.connectTo(observable: ObservableField<T>): PreferenceBinding<T> {
    return connectTo(observable, { observable.get() }, { value: T -> observable.set(value) })
}

fun Preference<Int>.connectTo(observable: ObservableInt): PreferenceBinding<Int> {
    return connectTo(observable, { observable.get() }, { value -> observable.set(value) })
}

fun Preference<Long>.connectTo(observable: ObservableLong): PreferenceBinding<Long> {
    return connectTo(observable, { observable.get() }, { value -> observable.set(value) })
}

fun Preference<Float>.connectTo(observable: ObservableFloat): PreferenceBinding<Float> {
    return connectTo(observable, { observable.get() }, { value -> observable.set(value) })
}

fun Preference<Boolean>.connectTo(observable: ObservableBoolean): PreferenceBinding<Boolean> {
    return connectTo(observable, { observable.get() }, { value -> observable.set(value) })
}

private inline fun <T> Preference<T>.connectTo(observable: Observable, crossinline getter: () -> T?, crossinline setter: (T) -> Unit): PreferenceBinding<T> {
    val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            getter()?.apply(::set)
        }
    }
    observable.addOnPropertyChangedCallback(callback)
    setter(get())

    val connection = observe({ _, value -> setter(value) })

    return PreferenceBinding(observable, callback, connection)
}

class PreferenceBinding<T>(private val observable: Observable,
                           private val propertyChangeCallback: Observable.OnPropertyChangedCallback,
                           private val connection: Preference.Connection<T>) {
    fun dispose() {
        observable.removeOnPropertyChangedCallback(propertyChangeCallback)
        connection.dispose()
    }
}