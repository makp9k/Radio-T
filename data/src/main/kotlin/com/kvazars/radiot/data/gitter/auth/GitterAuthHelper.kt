package com.kvazars.radiot.data.gitter.auth

import com.kvazars.radiot.data.gitter.auth.models.GitterChatAccessData
import io.reactivex.Maybe
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
            .create(GitterAuthApi::class.java)

    private val accessTokenPattern = Pattern.compile("\"accessToken\":\"([^\"]+)\"")
    private val roomIdPattern = Pattern.compile("\"troupe\":\\{\"id\":\"([^\"]+)\"")

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    fun getAccessData(roomName: String): Maybe<GitterChatAccessData> {
        return gitterApi.getChatPage(roomName)
                .map { it.string() }
                .flatMapMaybe {
                    val accessToken = accessTokenPattern.matcher(it)
                    val matcherRoomId = roomIdPattern.matcher(it)
                    if (accessToken.find() && matcherRoomId.find()) {
                        Maybe.just(GitterChatAccessData(accessToken.group(1), matcherRoomId.group(1)))
                    } else {
                        Maybe.empty()
                    }
                }
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}