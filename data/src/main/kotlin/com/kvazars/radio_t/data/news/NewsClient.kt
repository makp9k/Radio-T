package com.kvazars.radio_t.data.news

import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lza on 12.03.2017.
 */
class NewsClient(httpClient: OkHttpClient = OkHttpClient()) {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val newsApi = Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://news.radio-t.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(NewsApi::class.java)

    val news = newsApi.getNews()

    val activeNews: Single<String> = newsApi.getActiveNewsId().map { it.id }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}