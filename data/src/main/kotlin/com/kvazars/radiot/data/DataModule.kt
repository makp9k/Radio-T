package com.kvazars.radiot.data

import android.os.Build
import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.news.NewsClient
import dagger.Module
import dagger.Provides
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import javax.inject.Singleton

@Module
class DataModule(httpCacheDir: File) {

    private val regularHttpClient: OkHttpClient = OkHttpClient.Builder()
            .cache(Cache(httpCacheDir, 5 * 1024 * 1024))
            .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    private val radioTHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                    .tlsVersions(TlsVersion.TLS_1_0)
                    .cipherSuites(
                            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA
                    )
                    .build()
            builder.connectionSpecs(mutableListOf(spec))
        }

        radioTHttpClient =  builder
                .cache(Cache(httpCacheDir, 5 * 1024 * 1024))
                .addInterceptor(
                        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                                .setLevel(HttpLoggingInterceptor.Level.BODY)
                )
                .build()
    }

    @Singleton
    @Provides
    fun provideGitterFacade(): GitterClientFacade {
        return GitterClientFacade(regularHttpClient)
    }

    @Singleton
    @Provides
    fun provideNewsClient(): NewsClient {
        return NewsClient(radioTHttpClient)
    }
}