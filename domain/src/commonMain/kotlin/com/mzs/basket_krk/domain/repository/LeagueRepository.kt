package com.mzs.basket_krk.domain.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueDetails
import com.mzs.basket_krk.domain.model.PageableData

interface LeagueRepository {
    suspend fun getAllTimeLeaders(
        statOption: AllTimeStatLeaderOption,
        page: Int
    ): Either<Failure, PageableData<AllTimeLeader>>

    suspend fun getSeasonLeagues(seasonId: Int): Either<Failure, List<League>>
    suspend fun getLeagueDetails(leagueId: Int): Either<Failure, LeagueDetails>
}
