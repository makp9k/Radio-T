package com.kvazars.radiot.data.gitter.models

import com.google.gson.annotations.SerializedName
import com.kvazars.radiot.domain.chat.models.ChatUser

/**
 * Created by lza on 28.02.2017.
 */
class GitterUser(
    val username: String,
    val displayName: String,
    @SerializedName("avatarUrlSmall")
    val avatarUrl: String
) {
    fun asChatUser(): ChatUser {
        return ChatUser(username, displayName, avatarUrl)
    }
}