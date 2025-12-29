package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.repository.MatchRepository
import com.mzs.basket_krk.domain.service.MatchService

class MatchRepositoryImpl(private val matchService: MatchService) : MatchRepository {
    override suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>> {
        return matchService.getMatches(roundId, page)
    }
}
