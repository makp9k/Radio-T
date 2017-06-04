package com.kvazars.radiot.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.save(disposableBag: CompositeDisposable) {
    disposableBag.add(this)
}