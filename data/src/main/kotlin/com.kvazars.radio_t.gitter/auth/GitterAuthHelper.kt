package com.kvazars.radio_t.gitter.auth

import com.kvazars.radio_t.gitter.auth.models.GitterChatAccessData
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.regex.Pattern

/**
 * Created by lza on 28.02.2017.
 */
class GitterAuthHelper(httpClient: OkHttpClient) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val gitterApi = Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://gitter.im/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GitterApi::class.java)

    private val accessTokenPattern = Pattern.compile("\"accessToken\":\"([^\"]+)\"")
    private val roomIdPattern = Pattern.compile("\"troupe\":\\{\"id\":\"([^\"]+)\"")

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getAccessData(roomName: String): Single<GitterChatAccessData> {
        return gitterApi.getChatPage(roomName)
                .map { it.string() }
                .map {
                    val accessToken = accessTokenPattern.matcher(it)
                    val matcherRoomId = roomIdPattern.matcher(it)
                    if (accessToken.find() && matcherRoomId.find()) {
                        GitterChatAccessData(accessToken.group(1), matcherRoomId.group(1))
                    } else {
                        null
                    }
                }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}