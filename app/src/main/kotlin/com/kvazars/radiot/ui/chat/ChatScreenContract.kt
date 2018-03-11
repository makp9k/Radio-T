package com.kvazars.radiot.ui.chat

/**
 * Created by Leo on 08.04.2017.
 */
interface ChatScreenContract {
    interface View {
        fun buildFormattedMessageText(rawMessage: String): CharSequence

        fun showChatMessages(messages: Collection<ChatMessageModel>)

        fun showLoadingIndicator()

        data class ChatMessageModel(
                val id: String,
                val author: String,
                val message: CharSequence,
                val timestamp: Long,
                val sent: String
        )
    }

    interface Presenter {
        fun init()

        fun loadPrevious()

        fun onDestroy()
    }
}