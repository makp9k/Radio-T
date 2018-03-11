package com.kvazars.radiot.di

import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.domain.chat.ChatInteractor
import com.kvazars.radiot.domain.news.NewsInteractor
import dagger.Module
import dagger.Provides
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
            gitterClientFacade.chatEventStream,
            newsClient
        )
    }

    @Singleton
    @Provides
    fun provideChatInteractor(
        gitterClientFacade: GitterClientFacade
    ): ChatInteractor {
        return ChatInteractor(
            gitterClientFacade
        )
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}