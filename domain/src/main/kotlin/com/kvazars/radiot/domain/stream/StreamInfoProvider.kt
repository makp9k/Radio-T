package com.kvazars.radiot.domain.stream

import io.reactivex.Single

interface StreamInfoProvider {

    fun isOnAir(): Single<Boolean>

}