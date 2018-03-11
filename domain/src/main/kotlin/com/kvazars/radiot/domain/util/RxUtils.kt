package com.kvazars.radiot.domain.util

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(disposableBag: CompositeDisposable) {
    disposableBag.add(this)
}