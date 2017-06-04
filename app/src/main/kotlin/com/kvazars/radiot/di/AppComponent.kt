package com.kvazars.radiot.di

import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.domain.news.NewsInteractor
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Leo on 01.05.2017.
 */
@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun getNewsInteractor(): NewsInteractor
    fun getNewsClient(): NewsClient
    fun getGitterClient(): GitterClientFacade
}