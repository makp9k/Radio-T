package com.kvazars.radio_t.data.gitter.streaming.models

/**
 * Created by lza on 28.02.2017.
 */
data class HandshakeResponse(
        val clientId: String,
        val advice: HandshakeAdvice
)