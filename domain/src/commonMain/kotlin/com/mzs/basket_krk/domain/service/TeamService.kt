package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamResultList

interface TeamService {
    suspend fun getTeamDetails(teamId: Int): Either<Failure, TeamDetails>
    suspend fun getTeamResults(teamId: Int, seasonId: Int): Either<Failure, TeamResultList>
    suspend fun getTeamRoster(teamId: Int, seasonId: Int): Either<Failure, List<PlayerWithStat>>
    suspend fun getTeamRecords(teamId: Int, category: String): Either<Failure, List<TeamRecord>>
}
