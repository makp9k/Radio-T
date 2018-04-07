package com.kvazars.radiot.ui.news.views

import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.kvazars.radiot.ui.shared.NewsItemView

@EpoxyModelClass
abstract class EpoxyNewsModel : EpoxyModelWithHolder<EpoxyNewsModel.Holder>() {
    @EpoxyAttribute
    var newsModel: NewsItemView.NewsViewModel? = null
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var clickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) {
        newsModel?.let {
            holder.itemView.bindWithModel(it)
        }
        holder.itemView.setOnClickListener(clickListener)
    }

    override fun unbind(holder: Holder?) {
        holder?.let {
            holder.itemView.unbind()
        }
    }

    override fun getDefaultLayout() = 0

    override fun buildView(parent: ViewGroup?): View {
        val context = parent!!.context
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240F, context.resources.displayMetrics).toInt()
        )
        return NewsItemView(context).apply {
            layoutParams = lp
        }
    }

    class Holder : EpoxyHolder() {
        lateinit var itemView: NewsItemView

        override fun bindView(itemView: View) {
            this.itemView = itemView as NewsItemView
        }
    }
}