package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.AllTimeResponseDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.service.LeagueService

class NetworkLeagueService(
    private val apiService: ApiService
) : LeagueService {
    override suspend fun getAllTimeLeaders(
        statOption: AllTimeStatLeaderOption,
        page: Int
    ): Either<Failure, PageableData<AllTimeLeader>> {
        return Either.catchWithError {
            apiService.get<AllTimeResponseDto>("/stats/all_time?cat=${statOption.label.lowercase()}&page=$page")
                .toDomain()
        }
    }
}
