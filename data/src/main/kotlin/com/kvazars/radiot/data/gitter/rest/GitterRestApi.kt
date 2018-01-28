package com.kvazars.radiot.data.gitter.rest

import com.kvazars.radiot.data.gitter.models.GitterChatMessage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by lza on 11.03.2017.
 */
interface GitterRestApi {

    @GET("v1/rooms/{room}/chatMessages")
    fun getChatMessages(
            @Header("x-access-token") accessToken: String,
            @Path("room") room: String,
            @Query("skip") skip: Int?,
            @Query("beforeId") beforeId: String?,
            @Query("afterId") afterId: String?,
            @Query("aroundId") aroundId: String?,
            @Query("limit") limit: Int?,
            @Query("q") query: String?
    ): Single<List<GitterChatMessage>>

}