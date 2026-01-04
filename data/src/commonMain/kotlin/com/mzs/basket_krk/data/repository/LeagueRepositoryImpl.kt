package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueDetails
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.repository.LeagueRepository
import com.mzs.basket_krk.domain.service.LeagueService

class LeagueRepositoryImpl(private val leagueService: LeagueService) : LeagueRepository {
    override suspend fun getAllTimeLeaders(
        statOption: AllTimeStatLeaderOption,
        page: Int
    ): Either<Failure, PageableData<AllTimeLeader>> {
        return leagueService.getAllTimeLeaders(statOption, page)
    }

    override suspend fun getSeasonLeagues(seasonId: Int): Either<Failure, List<League>> {
        return leagueService.getSeasonLeagues(seasonId)
    }


    override suspend fun getLeagueDetails(leagueId: Int): Either<Failure, LeagueDetails> {
        return leagueService.getLeagueDetails(leagueId)
    }
}
