package com.kvazars.radiot.domain.chat

import com.kvazars.radiot.domain.chat.models.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit

class ChatInteractorTest {

    private class TestChatDataProvider(scheduler: Scheduler) : ChatDataProvider {
        val chatUser = ChatUser("User", "User", "")
        val now: ZonedDateTime = ZonedDateTime.now()

        override val chatEventStream: Observable<ChatEvent> =
            Observable.timer(1, TimeUnit.SECONDS, scheduler)
                .concatMapIterable {
                    listOf(
                        ChatMessageAdd(ChatMessage("id1", chatUser, "Message 1", now)),
                        ChatMessageAdd(ChatMessage("id2", chatUser, "Message 2", now.plusHours(1))),
                        ChatMessageAdd(ChatMessage("id3", chatUser, "Message 3", now.minusHours(1))),
                        ChatMessageRemove("id1")
                    )
                }

        override fun getMessagesBefore(messageId: String): Single<List<ChatMessage>> {
            return if (messageId == "id3") {
                Single.just(
                    listOf(
                        ChatMessage("id0", chatUser, "Message 1", now.minusHours(2))
                    )
                )
            } else {
                Single.just(listOf())
            }
        }

        override fun getMessagesAfter(messageId: String): Single<List<ChatMessage>> {
            return if (messageId == "id2") {
                Single.just(
                    listOf(
                        ChatMessage("id4", chatUser, "Message 1", now.plusHours(2))
                    )
                )
            } else {
                Single.just(listOf())
            }
        }
    }

    private lateinit var chatInteractor: ChatInteractor
    private lateinit var testObserver: TestObserver<ChatInteractor.Event>

    @Before
    fun setUp() {
        val testScheduler = TestScheduler()
        chatInteractor = ChatInteractor(TestChatDataProvider(testScheduler), PublishSubject.create())
        testObserver = chatInteractor.events.test()
        testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
    }

    @Test
    fun getEvents() {
        testObserver.assertNotTerminated()
        testObserver.assertValueCount(4)
    }

    @Test
    fun getMessages() {
        assertEquals(2, chatInteractor.messages.size)
        assertEquals("id3", chatInteractor.messages.first().id)
        assertEquals("id2", chatInteractor.messages.last().id)
    }

    @Test
    fun requestEarlierMessages() {
        chatInteractor.requestEarlierMessages()
        assertEquals(3, chatInteractor.messages.size)
        assertEquals("id0", chatInteractor.messages.first().id)
        assertEquals("id2", chatInteractor.messages.last().id)
    }

    @Test
    fun requestLaterMessages() {
        chatInteractor.requestLaterMessages()
        assertEquals(3, chatInteractor.messages.size)
        assertEquals("id3", chatInteractor.messages.first().id)
        assertEquals("id4", chatInteractor.messages.last().id)
    }
}