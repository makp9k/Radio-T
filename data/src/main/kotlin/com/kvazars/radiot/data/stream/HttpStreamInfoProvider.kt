package com.kvazars.radiot.data.stream

import com.kvazars.radiot.domain.stream.StreamInfoProvider
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpStreamInfoProvider(private val httpClient: OkHttpClient) : StreamInfoProvider {
    override fun isOnAir(): Single<Boolean> {
        return Single.defer {
            val request = Request.Builder().url("http://stream.radio-t.com/").head().build()
            val call = httpClient.newCall(request)
            val response = call.execute()
            Single.just(response.isSuccessful)
        }.onErrorReturn { false }
    }
}