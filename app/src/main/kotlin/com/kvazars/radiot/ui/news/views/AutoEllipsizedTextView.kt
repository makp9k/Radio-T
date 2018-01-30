package com.kvazars.radiot.ui.news.views

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.text.TextUtils
import android.util.AttributeSet

class AutoEllipsizedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        post{
            val lines = height / lineHeight

            maxLines = lines
            ellipsize = TextUtils.TruncateAt.END
        }
    }

}