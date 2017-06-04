package com.kvazars.radiot.data.gitter.streaming.models

/**
 * Created by lza on 28.02.2017.
 */
data class HandshakeAdvice(
        val reconnect: String,
        val interval: Int,
        val timeout: Int
)