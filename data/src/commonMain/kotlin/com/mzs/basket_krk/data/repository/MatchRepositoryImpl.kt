package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.model.StatEval
import com.mzs.basket_krk.domain.repository.MatchRepository
import com.mzs.basket_krk.domain.service.MatchService
import com.mzs.basket_krk.domain.sortTeam

class MatchRepositoryImpl(private val matchService: MatchService) : MatchRepository {
    override suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>> {
        return matchService.getMatches(roundId, page)
    }

    override suspend fun getMatchDetails(matchId: Int): Either<Failure, MatchDetails> {
        return matchService.getMatchDetails(matchId).map {
            it.copy(
                t1 = it.t1.copy(stats = it.t1.sortTeam(StatEval)),
                t2 = it.t2.copy(stats = it.t2.sortTeam(StatEval)),
            )
        }
    }
}
