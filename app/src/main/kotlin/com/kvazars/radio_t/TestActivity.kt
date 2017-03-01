package com.kvazars.radio_t

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.kvazars.radio_t.gitter.GitterClientFacade
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TestActivity : AppCompatActivity() {

    val gitter = GitterClientFacade()

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
    }
}
