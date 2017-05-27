package com.kvazars.radio_t.domain.news.usecase

import com.kvazars.radio_t.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Created by lza on 25.05.2017.
 */
class GetAllNewsUseCase(
        activeNewsIds: Observable<String>,
        newsListProvider: Single<List<NewsItem>>
) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private var newsCache: List<NewsItem>? = null

    val allNews: Observable<List<NewsItem>> = Observable
            .concat(
                    newsListProvider.toObservable(),
                    activeNewsIds.flatMap { activeNewsId ->
                        val cache = newsCache
                        if (cache != null && cache.find { it.id == activeNewsId } != null) {
                            Observable.empty()
                        } else {
                            newsListProvider.toObservable()
                        }
                    }
            )
            .doOnNext { newsCache = it }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}