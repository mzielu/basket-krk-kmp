package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.repository.SearchRepository

interface GetSearchItems :
    SuspendInOutUseCase<GetSearchItemsUseCase.Input, Either<Failure, PageableData<SearchItem>>>

class GetSearchItemsUseCase(private val searchRepository: SearchRepository) : GetSearchItems {
    override suspend fun invoke(input: Input): Either<Failure, PageableData<SearchItem>> {
        return searchRepository.getSearchItems(filter = input.filter, page = input.page)
    }

    data class Input(
        val filter: String,
        val page: Int,
        // TODO implement page size in backend
        val pageSize: Int
    )
}
