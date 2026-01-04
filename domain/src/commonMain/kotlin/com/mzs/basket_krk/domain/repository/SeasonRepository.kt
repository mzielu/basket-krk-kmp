package com.mzs.basket_krk.domain.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.LeaguesInfo
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.SeasonsInfo

interface SeasonRepository {
    suspend fun getSeasonsInfo(): Either<Failure, SeasonsInfo>

    suspend fun getLeaguesInfo(): Either<Failure, LeaguesInfo>

    suspend fun getRounds(seasonId: Int): Either<Failure, List<Round>>
}
