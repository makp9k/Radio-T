package com.kvazars.radio_t.data.gitter.models

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by lza on 28.02.2017.
 */
class ChatMessage {
    lateinit var id: String
    @SerializedName("fromUser")
    lateinit var user: GitterUser
    lateinit var text: String
    lateinit var sent: String

    companion object {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    }

    val timestamp: Long by lazy {
        dateFormat.parse(sent).time
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ChatMessage

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}