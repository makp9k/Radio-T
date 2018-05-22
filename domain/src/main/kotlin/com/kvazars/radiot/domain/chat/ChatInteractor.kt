package com.kvazars.radiot.domain.chat

import com.kvazars.radiot.domain.chat.models.ChatEvent
import com.kvazars.radiot.domain.chat.models.ChatMessage
import com.kvazars.radiot.domain.chat.models.ChatMessageAdd
import com.kvazars.radiot.domain.chat.models.ChatMessageRemove
import com.kvazars.radiot.domain.util.addTo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.*

class ChatInteractor(
    private val chatDataProvider: ChatDataProvider,
    private val reconnectTrigger: Observable<Unit>
) {

    private val eventsEmitter = PublishSubject.create<Event>()
    val events: Observable<Event> = eventsEmitter

    sealed class Event {
        class Add(val message: ChatMessage, val position: Int) : Event()
        class Remove(val position: Int) : Event()
        class Update(val message: ChatMessage, val position: Int) : Event()
    }

    private val messagesCache = Collections.synchronizedSortedSet(
        TreeSet<ChatMessage> { o1, o2 -> o1.sent.compareTo(o2.sent) }
    )
    val messages: Set<ChatMessage> = messagesCache

    private val disposableBag = CompositeDisposable()

    init {
        chatDataProvider
            .chatEventStream
            .retryWhen { reconnectTrigger }
            .subscribe(
                {
                    processStreamEvent(it)
                },
                {
                    it.printStackTrace()
                }
            ).addTo(disposableBag)
    }

    fun requestEarlierMessages() {
        if (messages.isEmpty()) {
            return
        }

        chatDataProvider
            .getMessagesBefore(messages.first().id)
            .toObservable()
            .flatMapIterable { it }
            .subscribe(
                {
                    addMessage(it)
                },
                {
                    it.printStackTrace()
                }
            )
            .addTo(disposableBag)
    }

    fun requestLaterMessages() {
        if (messages.isEmpty()) {
            return
        }

        chatDataProvider
            .getMessagesAfter(messages.last().id)
            .toObservable()
            .flatMapIterable { it }
            .subscribe(
                {
                    addMessage(it)
                },
                {
                    it.printStackTrace()
                }
            )
            .addTo(disposableBag)
    }

    fun dispose() {
        messagesCache.clear()
        disposableBag.clear()
    }

    private fun processStreamEvent(event: ChatEvent) {
        when (event) {
            is ChatMessageAdd -> addMessage(event.chatMessage)
            is ChatMessageRemove -> tryRemoveMessageById(event.chatMessageId)
        }
    }

    private fun tryRemoveMessageById(id: String) {
        val message = messagesCache.find { it.id == id }
        val position = messagesCache.indexOf(message)
        if (message != null && messagesCache.remove(message)) {
            eventsEmitter.onNext(Event.Remove(position))
        }

    }

    private fun addMessage(message: ChatMessage) {
        tryRemoveMessageById(message.id)
        if (messagesCache.add(message)) {
            val position = messagesCache.indexOf(message)
            eventsEmitter.onNext(Event.Add(message, position))
        }
    }

}