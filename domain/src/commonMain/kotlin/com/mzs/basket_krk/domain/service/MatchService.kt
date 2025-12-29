package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData

interface MatchService {
    suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>>
}
