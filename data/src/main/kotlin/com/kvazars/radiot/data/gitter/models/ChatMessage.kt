package com.kvazars.radiot.data.gitter.models

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
    private lateinit var sent: String

    companion object {
        val inputDateFormat = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            }
        }

        val outputDateFormat = object : ThreadLocal<SimpleDateFormat>() {
            override fun initialValue(): SimpleDateFormat {
                return SimpleDateFormat("dd MMMM, HH:mm", Locale.getDefault())
            }
        }
    }

    val time: Date by lazy {
        inputDateFormat.get().parse(sent)
    }

    val timestamp: Long by lazy {
        time.time
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

    override fun toString(): String {
        return "ChatMessage(id='$id', user=$user, text='$text', sent='$sent')"
    }


}