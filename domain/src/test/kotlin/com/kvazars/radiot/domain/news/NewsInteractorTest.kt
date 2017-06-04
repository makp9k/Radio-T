package com.kvazars.radiot.domain.news

import com.kvazars.radiot.domain.news.models.ChatMessageNotification
import com.kvazars.radiot.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by lza on 25.03.2017.
 */
class NewsInteractorTest {

    @Test
    fun shouldLoadActiveNewsOnChatNotification() {
        val testScheduler = TestScheduler()
        val chatMessageNotifications = Observable.interval(1, TimeUnit.SECONDS, testScheduler).take(2).map { ChatMessageNotification("Makp9k", "==>") }
        val newsProvider = object : NewsProvider {
            var i = 0
            override fun getActiveNewsId(): Single<String> {
                return Single.just("abc" + ++i)
            }

            override fun getNewsList(): Single<List<NewsItem>> {
                return Single.just(listOf(NewsItem("abc1", "", "", "", ""), NewsItem("abc2", "", "", "", ""), NewsItem("abc3", "", "", "", "")))
            }
        }

        val newsInteractor = NewsInteractor(chatMessageNotifications, newsProvider, testScheduler)
        val observer = newsInteractor.activeNews.test()
        testScheduler.advanceTimeBy(3, TimeUnit.SECONDS)

        observer.assertNoErrors()
        observer.assertValueCount(3)
    }

    @Test
    fun shouldUpdateNewsListWhenActiveNewsIsNotPresented() {
        val chatMessageNotifications = Observable.just(ChatMessageNotification("Makp9k", "==>"))
        val newsProvider = object : NewsProvider {
            var counter = 0

            override fun getActiveNewsId(): Single<String> {
                return Single.just("abc")
            }

            override fun getNewsList(): Single<List<NewsItem>> {
                if (counter++ == 0) return Single.just(Collections.emptyList<NewsItem>())

                return Single.just(Arrays.asList(
                        NewsItem("abc", "title", "snippet", "link", "url")
                ))
            }
        }

        val newsInteractor = NewsInteractor(chatMessageNotifications, newsProvider)
        val observer = newsInteractor.allNews.test().await()

        observer.assertNoErrors()
        observer.assertValueCount(2)
    }

    @Test
    fun shouldNotUpdateNewsListWhenActiveNewsIsPresented() {
        val chatMessageNotifications = Observable.just(ChatMessageNotification("@rt-bot", "==>"))
        val newsProvider = object : NewsProvider {

            override fun getActiveNewsId(): Single<String> {
                return Single.just("abc")
            }

            override fun getNewsList(): Single<List<NewsItem>> {
                return Single.just(Arrays.asList(
                        NewsItem("abc", "title", "snippet", "link", "url")
                ))
            }
        }

        val newsInteractor = NewsInteractor(chatMessageNotifications, newsProvider)
        val observer = newsInteractor.allNews.test().await()

        observer.assertNoErrors()
        observer.assertValueCount(1)
    }

}