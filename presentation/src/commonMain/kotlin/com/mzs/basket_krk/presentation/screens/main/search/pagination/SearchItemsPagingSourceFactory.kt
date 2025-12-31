package com.mzs.basket_krk.presentation.screens.main.search.pagination

import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.usecase.GetSearchItems
import com.mzs.basket_krk.presentation.base.BasePagingSource

class SearchItemsPagingSourceFactory(
    private val getSearchItems: GetSearchItems
) : BaseSearchItemsPagingSourceFactory {

    override fun create(
        pageSize: Int,
        filter: String
    ): BasePagingSource<SearchItem> {
        return SearchItemsPagingSource(
            pageSize = pageSize,
            filter = filter,
            getSearchItems = getSearchItems
        )
    }
}

interface BaseSearchItemsPagingSourceFactory {
    fun create(pageSize: Int, filter: String): BasePagingSource<SearchItem>
}
