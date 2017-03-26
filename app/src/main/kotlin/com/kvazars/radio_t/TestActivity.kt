package com.kvazars.radio_t

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.kvazars.radio_t.data.gitter.GitterClientFacade
import com.kvazars.radio_t.data.gitter.models.ChatMessage
import com.kvazars.radio_t.data.news.NewsClient
import com.kvazars.radio_t.domain.news.ChatMessageNotification
import com.kvazars.radio_t.domain.news.NewsInteractor
import com.kvazars.radio_t.domain.news.NewsItem
import com.kvazars.radio_t.domain.news.NewsProvider
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*


class TestActivity : AppCompatActivity() {

    val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    val gitter = GitterClientFacade(httpClient)
    val messageStream: Observable<ChatMessage> = gitter.getMessageStream()
            .subscribeOn(Schedulers.io())

    val newsClient = NewsClient(httpClient)
    val news = NewsInteractor(
            messageStream.map { ChatMessageNotification(it.user.username, it.text) },
            object : NewsProvider {
                override fun getActiveNewsId(): Single<String> {
                    return Single.just("58d5c66f159623ba5b888621")
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

    val messages = TreeSet<ChatMessage>(Comparator { o1, o2 -> o1.timestamp.compareTo(o2.timestamp) })

    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        text = findViewById(R.id.text) as TextView

        messageStream
                .observeOn(AndroidSchedulers.mainThread())
                .filter { messages.add(it) }
                .subscribe(
                        { displayMessages() },
                        { text.append(it.message + "\n") },
                        { text.append("CLOSED\n") }
                )

        val activeNewsId = news.activeNewsIds.share()
        val allNews = news.allNews.share()

        activeNewsId
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Toast.makeText(this, "Active news id: $it", Toast.LENGTH_SHORT).show() },
                        { text.append(it.message + "\n") }
                )

        allNews
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Toast.makeText(this, "Total news: ${it.count()}", Toast.LENGTH_SHORT).show() },
                        { text.append(it.message + "\n") }
                )

        Observable.combineLatest(
                activeNewsId,
                allNews,
                BiFunction { id: String, news: List<NewsItem> -> news.find { it.id == id } }
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Toast.makeText(this, "Active news title: ${it?.title}", Toast.LENGTH_SHORT).show() },
                        { text.append(it.message + "\n") }
                )

        findViewById(R.id.reconnect_btn).setOnClickListener {
            news.allNews
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { Toast.makeText(this, "Total news: ${it.count()}", Toast.LENGTH_SHORT).show() },
                            { text.append(it.message + "\n") }
                    )
        }

//        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())
    }

    private fun displayMessages() {
        text.text = ""

        println(messages)
        messages.forEach { text.append(it.text + "\n") }
    }
}