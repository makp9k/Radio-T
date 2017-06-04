package com.kvazars.radiot.ui.chat

/**
 * Created by Leo on 08.04.2017.
 */
interface ChatScreenContract {
    interface View {
        fun buildFormattedMessageText(rawMessage: String): CharSequence

        fun showChatMessages(messages: Collection<ChatMessageModel>)

        data class ChatMessageModel(
                val id: String,
                val author: String,
                val message: CharSequence,
                val timestamp: Long
        )
    }

    interface Presenter {
        fun loadPrevious()

        fun onDestroy()
    }
}