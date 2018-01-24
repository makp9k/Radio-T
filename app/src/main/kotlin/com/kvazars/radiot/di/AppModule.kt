package com.kvazars.radiot.di

import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.gitter.models.ChatMessageAdd
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.NewsProvider
import com.kvazars.radiot.domain.news.models.ChatMessageNotification
import com.kvazars.radiot.domain.news.models.NewsItem
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton


/**
 * Created by Leo on 02.05.2017.
 */
@Module
class AppModule {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    @Singleton
    @Provides
    fun provideNewsInteractor(
        gitterClientFacade: GitterClientFacade,
        newsClient: NewsClient
    ): NewsInteractor {
        return NewsInteractor(
            gitterClientFacade
                .stream
                .subscribeOn(Schedulers.io())
                .filter { it is ChatMessageAdd }
                .map { (it as ChatMessageAdd).chatMessage }
                .map { ChatMessageNotification(it.user.username, it.text) },
            object : NewsProvider {
                override fun getActiveNewsId(): Single<String> {
//                        return Single.just("5a6789720107ccc2a6b3b6ff")
                    return newsClient.activeNews
                }

                override fun getNewsList(): Single<List<NewsItem>> {
                    return newsClient.news.flatMap {
                        Observable.fromIterable(it).map {
                            NewsItem(it.id, it.title, it.snippet, it.link, it.pictureUrl)
                        }.toList()
                    }
                }
            }
        )
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}