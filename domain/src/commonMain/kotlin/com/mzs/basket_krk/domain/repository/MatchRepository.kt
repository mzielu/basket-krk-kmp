package com.mzs.basket_krk.domain.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData

interface MatchRepository {
    suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>>
}
