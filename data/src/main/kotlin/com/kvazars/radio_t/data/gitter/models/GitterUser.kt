package com.kvazars.radio_t.data.gitter.models

import com.google.gson.annotations.SerializedName

/**
 * Created by lza on 28.02.2017.
 */
data class GitterUser(
        val username: String,
        val displayName: String,
        @SerializedName("avatarUrlSmall")
        val avatarUrl: String
)