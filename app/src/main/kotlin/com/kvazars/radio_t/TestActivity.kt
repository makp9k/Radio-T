package com.kvazars.radio_t

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kvazars.radio_t.gitter.GitterClientFacade
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class TestActivity : AppCompatActivity() {

    val httpClient: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(
                    HttpLoggingInterceptor(HttpLoggingInterceptor.Logger(::println))
                            .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .build()

    val gitter = GitterClientFacade(httpClient)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        gitter.getMessageStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { Toast.makeText(this, it.text, Toast.LENGTH_SHORT).show() },
                        { Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show() },
                        { Toast.makeText(this, "CLOSED", Toast.LENGTH_SHORT).show() }
                )

        findViewById(R.id.reconnect_btn).setOnClickListener { gitter.reconnect() }

        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())
    }
}
