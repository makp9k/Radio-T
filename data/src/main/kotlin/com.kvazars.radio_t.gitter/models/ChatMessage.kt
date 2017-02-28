package com.kvazars.radio_t.gitter.models

import com.google.gson.annotations.SerializedName

/**
 * Created by lza on 28.02.2017.
 */
data class ChatMessage(
        val id: String,
        @SerializedName("fromUser")
        val user: GitterUser,
        val text: String
)