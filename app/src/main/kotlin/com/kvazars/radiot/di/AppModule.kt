package com.kvazars.radiot.di

import android.app.Application
import android.os.Build
import com.kvazars.radiot.data.gitter.GitterClientFacade
import com.kvazars.radiot.data.gitter.models.ChatMessageAdd
import com.kvazars.radiot.data.news.NewsClient
import com.kvazars.radiot.domain.news.NewsInteractor
import com.kvazars.radiot.domain.news.NewsProvider
import com.kvazars.radiot.domain.news.models.ChatMessageNotification
import com.kvazars.radiot.domain.news.models.NewsItem
import dagger.Module
import dagger.Provides
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import javax.inject.Named
import javax.inject.Singleton


/**
 * Created by Leo on 02.05.2017.
 */
@Module
class AppModule(private val app: Application) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    @Singleton
    @Provides
    @Named("regular")
    fun provideRegularHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(File(app.cacheDir, "http-cache"), 5 * 1024 * 1024))
            .addInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }

    @Singleton
    @Provides
    @Named("radio-t")
    fun provideRadioTHttpClient(): OkHttpClient {
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

        return builder
            .cache(Cache(File(app.cacheDir, "http-cache"), 5 * 1024 * 1024))
            .addInterceptor(
                HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideGitterFacade(@Named("regular") httpClient: OkHttpClient): GitterClientFacade {
        return GitterClientFacade(httpClient)
    }

    @Singleton
    @Provides
    fun provideNewsClient(@Named("radio-t") httpClient: OkHttpClient): NewsClient {
        return NewsClient(httpClient)
    }

    @Singleton
    @Provides
    fun provideNewsInteractor(
        gitterClientFacade: GitterClientFacade,
        newsClient: NewsClient
    ): NewsInteractor {
        return NewsInteractor(
            gitterClientFacade
                .stream
                .subscribeOn(Schedulers.io())
                .filter { it is ChatMessageAdd }
                .map { (it as ChatMessageAdd).chatMessage }
                .map { ChatMessageNotification(it.user.username, it.text) },
            object : NewsProvider {
                override fun getActiveNewsId(): Single<String> {
//                        return Single.just("5925d5c6b3ee44f9817c5978")
                    return newsClient.activeNews
                }

                override fun getNewsList(): Single<List<NewsItem>> {
                    return newsClient.news.flatMap {
                        Observable.fromIterable(it).map {
                            NewsItem(it.id, it.title, it.snippet, it.link, it.pictureUrl)
                        }.toList()
                    }
                }
            }
        )
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}