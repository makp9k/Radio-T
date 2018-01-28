package com.kvazars.radiot.data.gitter.models

import com.google.gson.annotations.SerializedName
import com.kvazars.radiot.domain.chat.models.ChatMessage
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by lza on 28.02.2017.
 */
class GitterChatMessage {
    @Transient
    private val dateFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME

    lateinit var id: String
    @SerializedName("fromUser")
    lateinit var user: GitterUser
    lateinit var text: String
    private lateinit var sent: String

    fun asChatMessage(): ChatMessage {
        return ChatMessage(
            id, user.asChatUser(), text, ZonedDateTime.parse(sent, dateFormat)
        )
    }
}
