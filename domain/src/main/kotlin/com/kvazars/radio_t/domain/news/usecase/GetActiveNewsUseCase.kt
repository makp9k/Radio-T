package com.kvazars.radio_t.domain.news.usecase

import com.kvazars.radio_t.domain.news.models.NewsItem
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * Created by lza on 25.05.2017.
 */
class GetActiveNewsUseCase(
        activeNewsIds: Observable<String>,
        allNews: Observable<List<NewsItem>>
) {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val emptyNewsItem = NewsItem("", "", "", null, null)

    val activeNews: Observable<NewsItem> = Observable
            .combineLatest(
                    activeNewsIds,
                    allNews,
                    BiFunction<String, List<NewsItem>, NewsItem> { id, news ->
                        news.find { it.id == id } ?: emptyNewsItem
                    }
            )
            .filter { it != emptyNewsItem }

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}