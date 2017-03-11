package com.kvazars.radio_t.gitter.rest.models

/**
 * Created by lza on 11.03.2017.
 */
data class ChatMessageResponseModel (
            val id: String,
            val text: String,
            val sent: String,
            val fromUser: UserResponseModel
)