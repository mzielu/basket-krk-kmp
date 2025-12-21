package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.SeasonsInfo
import com.mzs.basket_krk.domain.repository.SeasonRepository
import com.mzs.basket_krk.domain.service.SeasonService

class SeasonRepositoryImpl(private val seasonService: SeasonService) : SeasonRepository {
    override suspend fun getSeasonsInfo(): Either<Failure, SeasonsInfo> {
        return seasonService.getSeasonsInfo()
    }

    override suspend fun getRounds(seasonId: Int): Either<Failure, List<Round>> {
        return seasonService.getRounds(seasonId = seasonId)
    }
}
