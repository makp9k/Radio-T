package com.kvazars.radiot.domain.news.usecase

import com.kvazars.radiot.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test

/**
 * Created by lza on 25.05.2017.
 */
class GetGetAllNewsUseCaseTest {

    @Test
    fun givenAllIdsInTheSameList_shouldEmitOnce() {
        val activeNewsIds = Observable.just("id_1", "id_2")
        val newsList = Single.just(listOf(
                NewsItem("id_1", "title_1", "snippet_1", null, null),
                NewsItem("id_2", "title_2", "snippet_2", null, null),
                NewsItem("id_3", "title_3", "snippet_3", null, null)
        ))

        val test = GetAllNewsUseCase(
                activeNewsIds,
                newsList
        ).allNews.test()

        test.assertNoErrors()
        test.assertValueCount(1)
    }

    @Test
    fun givenAllIdsNotInTheSameList_shouldEmitTwice() {
        var attempt = 0
        val activeNewsIds = Observable.just("id_1", "id_4", "id_5")
        val newsList: Single<List<NewsItem>> = Single.defer {
            attempt++
            when (attempt) {
                1 -> Single.just(listOf(
                        NewsItem("id_1", "title_1", "snippet_1", null, null),
                        NewsItem("id_2", "title_2", "snippet_2", null, null),
                        NewsItem("id_3", "title_3", "snippet_3", null, null)
                ) )
                else -> Single.just(listOf(
                        NewsItem("id_4", "title_4", "snippet_4", null, null),
                        NewsItem("id_5", "title_5", "snippet_5", null, null),
                        NewsItem("id_6", "title_6", "snippet_6", null, null)
                ) )
            }
        }

        val useCase = GetAllNewsUseCase(
                activeNewsIds,
                newsList
        )

        val test = useCase.allNews.test()

        test.assertNoErrors()
        test.assertValueCount(2)
    }
}