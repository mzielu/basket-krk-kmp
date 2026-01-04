package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
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
}
