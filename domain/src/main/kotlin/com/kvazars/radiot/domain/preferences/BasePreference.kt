package com.kvazars.radiot.domain.preferences

import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.atomic.AtomicReference

open class BasePreference<T>(defaultValue: T) : Preference<T> {

    private val observers = CopyOnWriteArraySet<Preference.Observer<T>>()

    protected val value = AtomicReference<T>(defaultValue)

    override fun get(): T {
        return value.get()
    }

    override fun set(value: T) {
        val oldValue = this.value.get()
        this.value.set(value)
        if (oldValue !== value) {
            notifyObservers(value)
        }
    }

    override fun observe(observer: Preference.Observer<T>): Preference.Connection<T> {
        observers += observer
        observer.call(this, get())
        return Preference.Connection(this, observer)
    }

    override fun disconnect(observer: Preference.Observer<T>) {
        observers -= observer
    }

    private fun notifyObservers(value: T) {
        observers.forEach { it.call(this, value) }
    }
}