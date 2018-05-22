package com.kvazars.radiot.di

import android.app.Application
import android.preference.PreferenceManager
import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.data.preferences.PersistentApplicationPreferences
import com.kvazars.radiot.domain.chat.ChatInteractor
import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.preferences.ApplicationPreferences
import com.kvazars.radiot.domain.stream.StreamInfoProvider
import com.kvazars.radiot.domain.stream.StreamInteractor
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton


/**
 * Created by Leo on 02.05.2017.
 */
@Module
object AppModule {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    @Singleton
    @Provides
    @JvmStatic
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
    @JvmStatic
    fun provideChatInteractor(
        gitterClientFacade: GitterClientFacade,
        reconnectTrigger: PublishSubject<Unit>
    ): ChatInteractor {
        return ChatInteractor(
            gitterClientFacade,
            reconnectTrigger
        )
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideStreamInteractor(
        httpStreamInfoProvider: StreamInfoProvider
    ): StreamInteractor {
        return StreamInteractor(
            httpStreamInfoProvider
        )
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideApplicationPreferences(
        application: Application
    ): ApplicationPreferences {
        return PersistentApplicationPreferences(
            PreferenceManager.getDefaultSharedPreferences(application)
        )
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideReconnectTrigger(
    ): PublishSubject<Unit> {
        return PublishSubject.create()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}