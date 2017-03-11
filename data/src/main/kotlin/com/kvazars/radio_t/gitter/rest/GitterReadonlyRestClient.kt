package com.kvazars.radio_t.gitter.rest

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
            .create(GitterRestApi::class.java)

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getLastMessages(accessToken: String, roomId: String): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, null, null, null, 20, null)
    }

    fun getMessagesBefore(accessToken: String, roomId: String, messageId: String, count:Int): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, messageId, null, null, count, null)
    }

    fun getMessagesAfter(accessToken: String, roomId: String, messageId: String, count:Int): Single<List<ChatMessage>> {
        return gitterApi.getChatMessages(accessToken, roomId, null, null, messageId, null, count, null)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}