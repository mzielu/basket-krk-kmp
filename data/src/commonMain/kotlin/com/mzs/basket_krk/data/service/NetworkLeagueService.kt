package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.AllTimeResponseDto
import com.mzs.basket_krk.data.dto.LeagueDetailsDto
import com.mzs.basket_krk.data.dto.LeagueListDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueDetails
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

    override suspend fun getSeasonLeagues(seasonId: Int): Either<Failure, List<League>> {
        return Either.catchWithError {
            apiService.get<LeagueListDto>("/season/$seasonId/leagues/").toDomain()
        }
    }

    override suspend fun getLeagueDetails(leagueId: Int): Either<Failure, LeagueDetails> {
        return Either.catchWithError {
            apiService.get<LeagueDetailsDto>("/league/$leagueId/").toDomain()
        }
    }
}
