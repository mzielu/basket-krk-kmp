package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.repository.SearchRepository
import com.mzs.basket_krk.domain.service.SearchService

class SearchRepositoryImpl(private val searchService: SearchService) : SearchRepository {
    override suspend fun getSearchItems(
        filter: String,
        page: Int
    ): Either<Failure, PageableData<SearchItem>> {
        return searchService.getSearchItems(filter, page)
    }
}
