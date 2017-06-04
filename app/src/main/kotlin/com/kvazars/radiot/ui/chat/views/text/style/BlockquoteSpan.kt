package com.kvazars.radiot.ui.chat.views.text.style

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.TypedValue

/**
 * Created by Admin on 02.08.2017.
 */
class BlockquoteSpan(context: Context) : LeadingMarginSpan {

    private val width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics).toInt()
    private val gap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, context.resources.displayMetrics).toInt()
    private val paint = Paint()

    init {
        paint.color = 0xFFCCCCCC.toInt()
    }

    override fun drawLeadingMargin(c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int, text: CharSequence?, start: Int, end: Int, first: Boolean, layout: Layout?) {
        c?.drawRect(x.toFloat(), top.toFloat(), (x + width).toFloat(), bottom.toFloat(), paint)
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return gap + width
    }
}