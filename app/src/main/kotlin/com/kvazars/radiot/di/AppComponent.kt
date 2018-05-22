package com.kvazars.radiot.di

import android.app.Application
import com.kvazars.radiot.data.DataModule
import com.kvazars.radiot.domain.chat.ChatInteractor
import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import com.kvazars.radiot.domain.preferences.ApplicationPreferences
import com.kvazars.radiot.domain.stream.StreamInteractor
import dagger.BindsInstance
import dagger.Component
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton


/**
 * Created by Leo on 01.05.2017.
 */
@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun newsInteractor(): NewsInteractor
    fun chatInteractor(): ChatInteractor
    fun streamInteractor(): StreamInteractor
    fun streamPlayer(): PodcastStreamPlayer
    fun appPreferences(): ApplicationPreferences
    fun reconnectTrigger(): PublishSubject<Unit>

    @Component.Builder
    interface Builder {
        fun dataModule(dataModule: DataModule): Builder
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}