package com.kvazars.radiot.di

import com.kvazars.radiot.data.DataModule
import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.domain.news.NewsInteractor
import dagger.Component
import javax.inject.Singleton


/**
 * Created by Leo on 01.05.2017.
 */
@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {
    fun getNewsInteractor(): NewsInteractor
    fun getNewsClient(): NewsClient
    fun getGitterClient(): GitterClientFacade

    @Component.Builder
    interface Builder {
        fun dataModule(dataModule: DataModule): Builder

        fun build(): AppComponent
    }
}