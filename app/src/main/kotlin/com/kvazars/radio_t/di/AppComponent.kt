package com.kvazars.radio_t.di

import com.kvazars.radio_t.data.news.NewsClient
import com.kvazars.radio_t.domain.news.NewsInteractor
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
}