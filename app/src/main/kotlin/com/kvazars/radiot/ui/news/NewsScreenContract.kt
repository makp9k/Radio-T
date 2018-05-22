package com.kvazars.radiot.ui.news

import com.kvazars.radiot.ui.shared.NewsItemView

/**
 * Created by Leo on 08.04.2017.
 */
interface NewsScreenContract {
    interface View {
        fun setNews(news: List<NewsItemView.NewsViewModel>)
        fun openNewsUrl(link: String)
    }

    interface Presenter {
        fun onNewsItemClick(item: NewsItemView.NewsViewModel)

        fun onDestroy()
    }
}