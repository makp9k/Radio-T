package com.kvazars.radiot.ui.shared

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.graphics.Palette
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.kvazars.radiot.R
import com.kvazars.radiot.di.GlideApp
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.view_news_item.view.*

class NewsItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    data class NewsViewModel(
        val title: String,
        val footer: String,
        val details: String,
        val link: String,
        val pictureUrl: String?
    )

    companion object {
        val cachedSwatches: MutableMap<String, Palette.Swatch> = mutableMapOf()
    }

    private var model: NewsViewModel? = null
    private var paletteDisposable: Disposable? = null
    private var glideRequest: ViewTarget<ImageView, Bitmap>? = null

    init {
        View.inflate(context, R.layout.view_news_item, this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            description.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun bindWithModel(model: NewsViewModel) {
        this.model = model

        header.text = model.title
        description.text = model.details
        footer.text = model.footer

        loadImage(model.pictureUrl)
    }

    private fun loadImage(url: String?) {
        image.setImageBitmap(null)
        handleEmptyImage()
        if (URLUtil.isValidUrl(url)) {
            context.let {
                glideRequest = GlideApp.with(it)
                    .asBitmap()
                    .listener(ImageResourceListener())
                    .load(url)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into(image)
            }
        }
    }

    private fun handleEmptyImage() {
        applySwatch(
            Palette.Swatch(0xFFFFFFFF.toInt(), 1)
        )
        image.setImageDrawable(null)

        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.connect(R.id.header, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(R.id.header, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
        constraintSet.applyTo(this)
    }

    private fun handleImageBitmap(bitmap: Bitmap) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(this)
        constraintSet.connect(R.id.header, ConstraintSet.START, R.id.gradient_overlay, ConstraintSet.START)
        constraintSet.connect(R.id.header, ConstraintSet.END, R.id.gradient_overlay, ConstraintSet.END)
        constraintSet.applyTo(this)

        val pictureUrl = model?.pictureUrl ?: return
        val swatch: Palette.Swatch? = cachedSwatches[pictureUrl]

        if (swatch != null) {
            applySwatch(swatch)
        } else {
            paletteDisposable?.let {
                if (!it.isDisposed) {
                    it.dispose()
                }
            }

            paletteDisposable = Single.defer<Palette> { Single.just(Palette.Builder(bitmap).generate()) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result, _ ->
                    if (result != null) {
                        val s = result.dominantSwatch
                        if (s != null) {
                            cachedSwatches[pictureUrl] = s
                            applySwatch(s)
                        }
                    }
                }
        }
    }

    private fun applySwatch(swatch: Palette.Swatch) {
        gradient_overlay.background?.colorFilter =
                PorterDuffColorFilter(
                    swatch.rgb,
                    PorterDuff.Mode.MULTIPLY
                )
        val color = swatch.bodyTextColor or (0xFF shl 24)
        header.setTextColor(color)
        description.setTextColor(color)
        footer.setTextColor(color)
    }

    fun unbind() {
        paletteDisposable?.dispose()
        try {
            GlideApp.with(context).clear(glideRequest)
        } catch (ignored: Exception) {
        }
    }

    inner class ImageResourceListener : RequestListener<Bitmap> {

        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
            handleEmptyImage()
            return false
        }

        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            resource?.let { handleImageBitmap(resource) } ?: handleEmptyImage()
            return false
        }
    }
}