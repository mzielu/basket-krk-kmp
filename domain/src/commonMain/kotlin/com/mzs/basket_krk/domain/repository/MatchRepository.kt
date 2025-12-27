package com.mzs.basket_krk.domain.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match

interface MatchRepository {
    suspend fun getMatches(roundId: Int): Either<Failure, List<Match>>
}
