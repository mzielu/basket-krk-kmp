package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.MatchesListDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.service.MatchService

class NetworkMatchService(
    private val apiService: ApiService
) : MatchService {
    override suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>> {
        return Either.catchWithError {
            apiService.get<MatchesListDto>("/round/$roundId/?page=$page").toDomain()
        }
    }
}
