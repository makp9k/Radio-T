package com.kvazars.radiot.domain.preferences

typealias Observer<T> = (preference: Preference<T>, value: T) -> Unit

interface Preference<T> {
    fun get(): T

    fun set(value: T)

    fun observe(observer: Observer<T>): Connection<T>

    fun disconnect(observer: Observer<T>)

    class Connection<T>(private val preference: Preference<T>, private val observer: Observer<T>) {
        fun dispose() {
            preference.disconnect(observer)
        }
    }
}