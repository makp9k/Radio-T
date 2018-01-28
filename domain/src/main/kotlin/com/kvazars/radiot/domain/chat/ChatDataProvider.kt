package com.kvazars.radiot.domain.chat

import com.kvazars.radiot.domain.chat.models.ChatEvent
import com.kvazars.radiot.domain.chat.models.ChatMessage
import io.reactivex.Observable
import io.reactivex.Single

interface ChatDataProvider {

    val chatEventStream: Observable<ChatEvent>

    fun getMessagesBefore(messageId: String): Single<List<ChatMessage>>

    fun getMessagesAfter(messageId: String): Single<List<ChatMessage>>
}