package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.LeaguesInfoDto
import com.mzs.basket_krk.data.dto.RoundsListDto
import com.mzs.basket_krk.data.dto.SeasonsInfoDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.LeaguesInfo
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.SeasonsInfo
import com.mzs.basket_krk.domain.service.SeasonService


class NetworkSeasonService(
    private val apiService: ApiService
) : SeasonService {
    override suspend fun getSeasonsInfo(): Either<Failure, SeasonsInfo> {
        return Either.catchWithError {
            apiService.get<SeasonsInfoDto>("/season/").toDomain()
        }
    }

    override suspend fun getLeaguesInfo(): Either<Failure, LeaguesInfo> {
        return Either.catchWithError {
            apiService.get<LeaguesInfoDto>("/league/").toDomain()
        }
    }

    override suspend fun getRounds(seasonId: Int): Either<Failure, List<Round>> {
        return Either.catchWithError {
            apiService.get<RoundsListDto>("/season/$seasonId/rounds/").toDomain()
        }
    }
}
