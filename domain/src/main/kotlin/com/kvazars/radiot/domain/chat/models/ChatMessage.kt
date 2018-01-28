package com.kvazars.radiot.domain.chat.models

import org.threeten.bp.ZonedDateTime

class ChatMessage(
    val id: String,
    val user: ChatUser,
    val text: String,
    val sent: ZonedDateTime
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "ChatMessage(id='$id', user=$user, text='$text', sent=$sent)"
    }
}