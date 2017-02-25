package com.kvazars.radio_t.gitter

/**
 * Created by lza on 24.02.2017.
 */

data class HandshakeResponse(
        val clientId: String,
        val advice: Advice
)

data class Advice(
        val reconnect: String,
        val interval: Int,
        val timeout: Int
)

data class ChatMessage(
        val author: String,
        val message: String
)