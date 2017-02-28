package com.kvazars.radio_t.gitter.rest

import com.kvazars.radio_t.gitter.auth.GitterApi
import com.kvazars.radio_t.gitter.models.ChatMessage
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by lza on 28.02.2017.
 */
class GitterReadonlyRestClient(httpClient: OkHttpClient) {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val gitterApi = Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://api.gitter.im/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GitterApi::class.java)

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getLastMessages(count: Int): Single<List<ChatMessage>> {
        return Single.just(emptyList())
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}