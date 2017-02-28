package com.kvazars.radio_t.gitter

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by lza on 28.02.2017.
 */
class GitterClientFacadeTest {

    @Test
    fun getMessageStream() {
        val observer = GitterClientFacade().getMessageStream().doOnNext(::println).test()

        println(observer.values())

        observer.await()
    }

}