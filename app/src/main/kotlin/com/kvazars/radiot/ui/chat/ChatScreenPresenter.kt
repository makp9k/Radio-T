package com.kvazars.radiot.ui.chat

import com.kvazars.radiot.domain.chat.ChatInteractor
import com.kvazars.radiot.domain.chat.models.ChatMessage
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.format.DateTimeFormatterBuilder
import java.util.concurrent.TimeUnit

/**
 * Created by Leo on 27.04.2017.
 */
class ChatScreenPresenter(
    private val view: ChatScreenContract.View,
    private val chatInteractor: ChatInteractor
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

    private val messages = mutableListOf<ChatScreenContract.View.ChatMessageModel>()

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun init() {
        view.showLoadingIndicator()

        chatInteractor.messages.forEach {
            messages.add(mapChatMessage(it))
        }
        view.showChatMessages(messages)

        chatInteractor
            .events
            .doOnNext { processChatEvent(it) }
            .debounce(100L, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { view.showChatMessages(messages) },
                { it.printStackTrace() }
            )
            .addTo(disposableBag)
    }

    override fun loadPrevious() {
        view.showLoadingIndicator()
        chatInteractor.requestEarlierMessages()
    }

    private fun processChatEvent(event: ChatInteractor.Event) {
        when (event) {
            is ChatInteractor.Event.Add -> {
                messages.add(event.position, mapChatMessage(event.message))
            }
            is ChatInteractor.Event.Remove -> {
                messages.removeAt(event.position)
            }
            is ChatInteractor.Event.Update -> {
            }
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