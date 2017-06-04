package com.kvazars.radiot.data.gitter.auth

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by lza on 24.02.2017.
 */
interface GitterAuthApi {

    @GET("{room}/~chat")
    fun getChatPage(@Path("room") room: String): Single<ResponseBody>

}
