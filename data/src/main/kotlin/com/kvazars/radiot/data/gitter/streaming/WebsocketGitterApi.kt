package com.kvazars.radiot.data.gitter.streaming

import com.kvazars.radiot.data.gitter.streaming.models.HandshakeResponse
import io.reactivex.Single
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by lza on 24.02.2017.
 */
interface WebsocketGitterApi {

    @POST("bayeux")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    fun handshake(@Body payload: RequestBody): Single<List<HandshakeResponse>>

}
