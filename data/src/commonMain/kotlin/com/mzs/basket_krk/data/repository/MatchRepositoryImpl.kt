package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.repository.MatchRepository
import com.mzs.basket_krk.domain.service.MatchService

class MatchRepositoryImpl(private val matchService: MatchService) : MatchRepository {
    override suspend fun getMatches(roundId: Int): Either<Failure, List<Match>> {
        return matchService.getMatches(roundId)
    }
}
