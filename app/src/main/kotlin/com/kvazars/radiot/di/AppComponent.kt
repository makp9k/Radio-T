package com.kvazars.radiot.di

import com.kvazars.radiot.data.DataModule
import com.kvazars.radiot.domain.chat.ChatInteractor
import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.player.PodcastStreamPlayer
import dagger.Component
import javax.inject.Singleton


/**
 * Created by Leo on 01.05.2017.
 */
@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun getNewsInteractor(): NewsInteractor
    fun getChatInteractor(): ChatInteractor
    fun streamPlayer(): PodcastStreamPlayer

    @Component.Builder
    interface Builder {
        fun dataModule(dataModule: DataModule): Builder

        fun build(): AppComponent
    }
}