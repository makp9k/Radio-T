package com.kvazars.radio_t.gitter

import com.google.gson.annotations.SerializedName

/**
 * Created by lza on 24.02.2017.
 */

data class HandshakeResponse(
        val clientId: String,
        val advice: HandshakeAdvice
)

data class HandshakeAdvice(
        val reconnect: String,
        val interval: Int,
        val timeout: Int
)

data class ChatMessage(
        val id: String,
        @SerializedName("fromUser")
        val user: GitterUser,
        val text: String
)

data class GitterUser(
        val username: String,
        val displayName: String,
        @SerializedName("avatarUrlSmall")
        val avatarUrl: String
)