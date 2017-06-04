package com.kvazars.radiot.data.gitter.rest

import com.kvazars.radiot.data.gitter.models.ChatMessage
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
            .create(GitterRestApi::class.java)

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getLastMessages(accessToken: String, roomId: String): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, null, null, null, 100, null)
    }

    fun getMessagesBefore(accessToken: String, roomId: String, messageId: String): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, messageId, null, null, 100, null)
    }

    fun getMessagesAfter(accessToken: String, roomId: String, messageId: String): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, null, messageId, null, 100, null)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}