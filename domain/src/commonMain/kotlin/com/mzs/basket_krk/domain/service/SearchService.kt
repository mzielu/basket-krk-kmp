package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem

interface SearchService {
    suspend fun getSearchItems(filter: String, page: Int): Either<Failure, PageableData<SearchItem>>
}
