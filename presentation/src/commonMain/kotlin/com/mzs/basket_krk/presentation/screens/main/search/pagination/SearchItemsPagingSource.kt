package com.mzs.basket_krk.presentation.screens.main.search.pagination

import arrow.core.Either
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.usecase.GetSearchItems
import com.mzs.basket_krk.domain.usecase.GetSearchItemsUseCase
import com.mzs.basket_krk.presentation.base.BasePagingSource

class SearchItemsPagingSource(
    private val pageSize: Int,
    private val filter: String,
    private val getSearchItems: GetSearchItems
) : BasePagingSource<SearchItem>() {
    override suspend fun fetchData(page: Int): Either<Throwable, PageableData<SearchItem>> {
        return getSearchItems.invoke(
            GetSearchItemsUseCase.Input(
                filter = filter,
                page = page,
                pageSize = pageSize
            )
        )
    }
}