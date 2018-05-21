package com.kvazars.radiot.ui.stream.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import com.kvazars.radiot.R

class RadiotPatternBackgroundDrawable(context: Context) : Drawable() {

    private val backgroundPaint = Paint().apply {
        shader = BitmapShader(
            BitmapFactory.decodeResource(context.resources, R.drawable.radio_t_pattern_transparent),
            Shader.TileMode.REPEAT,
            Shader.TileMode.REPEAT
        )
    }

    private val foregroundPaint = Paint()

    override fun onBoundsChange(bounds: Rect) {
        foregroundPaint.shader = RadialGradient(
            bounds.exactCenterX(),
            bounds.exactCenterY(),
            Math.max(bounds.width(), bounds.height()).toFloat(),
            intArrayOf(0xFFFFFFFF.toInt(), 0x88FFFFFF.toInt(), 0x004f7383),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun draw(canvas: Canvas) {
        canvas.drawRect(bounds, backgroundPaint)
        canvas.drawRect(bounds, foregroundPaint)
    }
}