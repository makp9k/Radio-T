package com.kvazars.radiot.domain.preferences

interface Preference<T> {
    fun get(): T

    fun set(value: T)

    fun observe(observer: Observer<T>): Connection<T>

    fun disconnect(observer: Observer<T>)

    interface Observer<T> {
        fun call(preference: Preference<T>, value: T)
    }

    class Connection<T>(private val preference: Preference<T>, private val observer: Observer<T>) {
        fun dispose() {
            preference.disconnect(observer)
        }
    }
}