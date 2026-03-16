package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.TeamDetailsDto
import com.mzs.basket_krk.data.dto.TeamRecordListDto
import com.mzs.basket_krk.data.dto.TeamResultListDto
import com.mzs.basket_krk.data.dto.TeamRosterDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamResultList
import com.mzs.basket_krk.domain.service.TeamService

class NetworkTeamService(
    private val apiService: ApiService
) : TeamService {
    override suspend fun getTeamDetails(teamId: Int): Either<Failure, TeamDetails> {
        return Either.catchWithError {
            apiService.get<TeamDetailsDto>("/team/$teamId/").toDomain()
        }
    }

    override suspend fun getTeamResults(teamId: Int, seasonId: Int): Either<Failure, TeamResultList> {
        return Either.catchWithError {
            apiService.get<TeamResultListDto>("/team/$teamId/results?season_id=$seasonId").toDomain()
        }
    }

    override suspend fun getTeamRoster(teamId: Int, seasonId: Int): Either<Failure, List<PlayerWithStat>> {
        return Either.catchWithError {
            apiService.get<TeamRosterDto>("/team/$teamId/players?season_id=$seasonId").toDomain()
        }
    }

    override suspend fun getTeamRecords(teamId: Int, category: String): Either<Failure, List<TeamRecord>> {
        return Either.catchWithError {
            apiService.get<TeamRecordListDto>("/team/$teamId/records?cat=$category").toDomain()
        }
    }
}
