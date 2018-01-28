package com.kvazars.radiot.data.news

import com.kvazars.radiot.domain.news.NewsProvider
import com.kvazars.radiot.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatterBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * Created by lza on 12.03.2017.
 */
class NewsClient(httpClient: OkHttpClient = OkHttpClient()) : NewsProvider {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val dateFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .appendPattern("[.SSS]")
        .appendOffsetId()
        .toFormatter(Locale.ENGLISH)

    private val newsApi = Retrofit.Builder()
            .client(httpClient)
            .baseUrl("https://news.radio-t.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(NewsApi::class.java)

    val news = newsApi.getNews()

    val activeNews: Single<String> = newsApi.getActiveNewsId().map { it.id }.onErrorReturn { "" }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun getActiveNewsId(): Single<String> {
//                        return Single.just("5a6789720107ccc2a6b3b6ff")
        return activeNews
    }

    override fun getNewsList(): Single<List<NewsItem>> {
        return news.flatMap {
            Observable.fromIterable(it).map(::mapNewsItem).toList()
        }
    }

    private fun mapNewsItem(newsItem: com.kvazars.radiot.data.news.models.NewsItem): NewsItem {
        return NewsItem(
            newsItem.id,
            newsItem.title,
            newsItem.snippet,
            newsItem.link,
            newsItem.pictureUrl,
            newsItem.active,
            parseDate(newsItem.ts)
        )
    }

    private fun parseDate(timestamp: String) = ZonedDateTime.parse(timestamp, dateFormatter)

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}