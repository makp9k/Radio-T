package com.kvazars.radio_t.domain.news

import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import java.util.*

/**
 * Created by lza on 25.03.2017.
 */
class NewsInteractorTest {

    @Test
    fun shouldLoadActiveNewsOnChatNotification() {
        val chatMessageNotifications = Observable.range(0, 2).map { ChatMessageNotification("@rt-bot", "==>") }
        val newsProvider = object : NewsProvider {
            override fun getActiveNewsId(): Single<String> {
                return Single.just("abc")
            }

            override fun getNewsList(): Single<List<NewsItem>> {
                return Single.just(Collections.emptyList())
            }
        }

        val newsInteractor = NewsInteractor(chatMessageNotifications, newsProvider)
        val observer = newsInteractor.activeNews.test().await()

        observer.assertNoErrors()
        observer.assertValueCount(3)
    }

    @Test
    fun shouldUpdateNewsListWhenActiveNewsIsNotPresented() {
        val chatMessageNotifications = Observable.just(ChatMessageNotification("@rt-bot", "==>"))
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