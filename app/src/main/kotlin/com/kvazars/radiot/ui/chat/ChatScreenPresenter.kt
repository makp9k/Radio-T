package com.kvazars.radiot.ui.chat

import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.domain.chat.models.ChatEvent
import com.kvazars.radiot.domain.chat.models.ChatMessage
import com.kvazars.radiot.domain.chat.models.ChatMessageAdd
import com.kvazars.radiot.domain.chat.models.ChatMessageRemove
import com.kvazars.radiot.utils.addTo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.*

/**
 * Created by Leo on 27.04.2017.
 */
class ChatScreenPresenter(
    private val view: ChatScreenContract.View,
    private val gitterClient: GitterClientFacade
) : ChatScreenContract.Presenter {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    private val dateFormat = DateTimeFormatterBuilder()
        .appendPattern("dd MMM, HH:mm")
        .toFormatter()

    private val disposableBag = CompositeDisposable()

    private val messages = Collections.synchronizedSet(
        TreeSet<ChatScreenContract.View.ChatMessageModel> { o1, o2 -> o2.timestamp.compareTo(o1.timestamp) }
    )

    init {
        gitterClient
            .stream
            .doOnNext { processStreamEvent(it) }
            .doOnNext { println(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.showChatMessages(messages.toList())
                },
                {
                    it.printStackTrace()
                }
            ).addTo(disposableBag)
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun loadPrevious() {
        if (messages.isNotEmpty()) {
            gitterClient.getMessagesBefore(messages.last().id)
                .flatMap {
                    Observable
                        .fromIterable(it)
                        .map { mapChatMessage(it) }
                        .doOnNext { println(it) }
                        .toList()
                }
                .doOnSuccess {
                    messages.addAll(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        view.showChatMessages(messages)
                    },
                    {
                        it.printStackTrace()
                    }
                )
                .addTo(disposableBag)
        }
    }

    private fun processStreamEvent(event: ChatEvent) {
        when (event) {
            is ChatMessageAdd -> {
                removeMessageById(event.chatMessage.id)

                val message = mapChatMessage(event.chatMessage)
                messages.add(message)
            }
            is ChatMessageRemove -> removeMessageById(event.chatMessageId)
        }
    }

    private fun removeMessageById(id: String) {
        val existingMessage = messages.find { (_id) -> _id == id }
        if (existingMessage != null) {
            messages.remove(existingMessage)
        }
    }

    private fun mapChatMessage(chatMessage: ChatMessage) = ChatScreenContract.View.ChatMessageModel(
        chatMessage.id,
        chatMessage.user.displayName,
        view.buildFormattedMessageText(chatMessage.text),
        chatMessage.sent.toInstant().epochSecond,
        dateFormat.format(chatMessage.sent)
    )

    override fun onDestroy() {
        disposableBag.dispose()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}