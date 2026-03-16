package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamResultList
import com.mzs.basket_krk.domain.repository.TeamRepository
import com.mzs.basket_krk.domain.service.TeamService

class TeamRepositoryImpl(private val teamService: TeamService) : TeamRepository {
    override suspend fun getTeamDetails(teamId: Int): Either<Failure, TeamDetails> {
        return teamService.getTeamDetails(teamId)
    }

    override suspend fun getTeamResults(teamId: Int, seasonId: Int): Either<Failure, TeamResultList> {
        return teamService.getTeamResults(teamId, seasonId)
    }

    override suspend fun getTeamRoster(teamId: Int, seasonId: Int): Either<Failure, List<PlayerWithStat>> {
        return teamService.getTeamRoster(teamId, seasonId)
    }

    override suspend fun getTeamRecords(teamId: Int, category: String): Either<Failure, List<TeamRecord>> {
        return teamService.getTeamRecords(teamId, category)
    }
}
