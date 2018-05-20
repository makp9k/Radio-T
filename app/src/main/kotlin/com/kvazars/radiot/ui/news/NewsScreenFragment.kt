package com.kvazars.radiot.ui.news

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import com.kvazars.radiot.ui.news.views.EpoxyNewsModel_
import com.kvazars.radiot.ui.shared.NewsItemView


/**
 * Created by Leo on 12.04.2017.
 */
class NewsScreenFragment : Fragment(), NewsScreenContract.View, EpoxyRecyclerView.ModelBuilderCallback {

    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var presenter: NewsScreenPresenter
    private lateinit var epoxyRecyclerView: EpoxyRecyclerView
    private var news: List<NewsItemView.NewsViewModel>? = null

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = NewsScreenPresenter(this, RadioTApplication.getAppComponent(context!!).getNewsInteractor())

        epoxyRecyclerView = view.findViewById(R.id.recycler_view)
        (epoxyRecyclerView.layoutManager as LinearLayoutManager).initialPrefetchItemCount = 4
        epoxyRecyclerView.buildModelsWith(this)
        epoxyRecyclerView.requestModelBuild()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun buildModels(controller: EpoxyController) {
        context?.let { _ ->
            news?.forEachIndexed { _, it ->
                EpoxyNewsModel_()
                        .id(it.hashCode())
                        .newsModel(it)
                        .clickListener { model, _, _, _ ->
                            model.newsModel()?.let {
                                presenter.onNewsItemClick(it)
                            }
                        }!!
                        .addTo(controller)
            }
        }
    }

    override fun openNewsUrl(url: String) {
        context?.let {
            CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(it, R.color.primary))
                    .setShowTitle(true)
                    .build()
                    .launchUrl(context, Uri.parse(url))
        }
    }

    override fun setNews(news: List<NewsItemView.NewsViewModel>) {
        this.news = news
        epoxyRecyclerView.requestModelBuild()
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}