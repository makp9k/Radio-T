package com.kvazars.radio_t

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.kvazars.radio_t.data.gitter.GitterClientFacade
import com.kvazars.radio_t.data.gitter.models.ChatMessage
import com.kvazars.radio_t.data.news.NewsClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins
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
    val news = NewsClient(httpClient)

    val messages = TreeSet<ChatMessage>(Comparator { o1, o2 -> o1.timestamp.compareTo(o2.timestamp) })

    lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        text = findViewById(R.id.text) as TextView

        gitter.getMessageStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { messages.add(it) }
                .subscribe(
                        { displayMessages() },
                        { text.append(it.message + "\n") },
                        { text.append("CLOSED\n") }
                )

        findViewById(R.id.reconnect_btn).setOnClickListener {
            news.getNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { println(it) },
                            { text.append(it.message + "\n") }
                    )
        }

        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())
    }

    private fun displayMessages() {
        text.text = ""

        println(messages)
        messages.forEach { text.append(it.text + "\n") }
    }
}
