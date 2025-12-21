package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.SeasonsInfo

interface SeasonService {
    suspend fun getSeasonsInfo(): Either<Failure, SeasonsInfo>

    suspend fun getRounds(seasonId: Int): Either<Failure, List<Round>>
}
