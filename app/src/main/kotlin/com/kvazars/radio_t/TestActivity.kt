package com.kvazars.radio_t

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.kvazars.radio_t.gitter.GitterClientFacade
import com.kvazars.radio_t.gitter.models.ChatMessage
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
            gitter.getMessagesBefore(messages.first().id, 4)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapIterable { it }
                    .filter { messages.add(it) }
                    .subscribe(
                            { displayMessages() },
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
