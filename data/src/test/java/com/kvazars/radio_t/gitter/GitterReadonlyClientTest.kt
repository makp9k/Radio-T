package com.kvazars.radio_t.gitter

import org.junit.Test

/**
 * Created by lza on 24.02.2017.
 */
class GitterReadonlyClientTest {

    @Test
    fun connect() {
        val observer = GitterReadonlyClient().connect().take(3).test()

        println(observer.values())

        observer.await()
    }
}