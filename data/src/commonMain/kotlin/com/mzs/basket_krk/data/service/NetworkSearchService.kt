package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.SearchResponseDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.domain.service.SearchService

class NetworkSearchService(
    private val apiService: ApiService
) : SearchService {
    override suspend fun getSearchItems(filter: String, page: Int): Either<Failure, PageableData<SearchItem>> {
        return Either.catchWithError {
            apiService.get<SearchResponseDto>("/search/?filter=$filter&page=$page").toDomain()
        }
    }
}
