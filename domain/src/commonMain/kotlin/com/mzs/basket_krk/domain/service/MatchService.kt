package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match

interface MatchService {
    suspend fun getMatches(roundId: Int): Either<Failure, List<Match>>
}
