package com.kvazars.radiot.domain.news.usecase

import com.kvazars.radiot.domain.news.models.NewsItem
import com.kvazars.radiot.domain.util.Optional
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

    val activeNews: Observable<Optional<NewsItem>> = Observable
            .combineLatest(
                    activeNewsIds,
                    allNews,
                    BiFunction<String, List<NewsItem>, Optional<NewsItem>> { id, news ->
                        Optional(news.find { it.id == id })
                    }
            )

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}