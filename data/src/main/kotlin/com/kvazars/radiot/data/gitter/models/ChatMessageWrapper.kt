package com.kvazars.radiot.data.gitter.models

/**
 * Created by Collider on 09.07.2017.
 */
sealed class ChatEvent

class Connect : ChatEvent()

data class ChatMessageAdd(val chatMessage: ChatMessage) : ChatEvent()

data class ChatMessageRemove(val chatMessageId: String) : ChatEvent()