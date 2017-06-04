package com.kvazars.radiot.domain.util

/**
 * Created by Leo on 27.10.2017.
 */
data class Optional<out T> (val value : T?) {
    companion object {
        val empty = Optional(null)
    }
}