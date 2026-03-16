package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.PlayerDetailsDto
import com.mzs.basket_krk.data.dto.PlayerLogListDto
import com.mzs.basket_krk.data.dto.PlayerRecordsDto
import com.mzs.basket_krk.data.dto.PlayerStatListDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.service.PlayerService

class NetworkPlayerService(
    private val apiService: ApiService
) : PlayerService {
    override suspend fun getPlayerDetails(playerId: Int): Either<Failure, PlayerDetails> {
        return Either.catchWithError {
            apiService.get<PlayerDetailsDto>("/player/$playerId/").toDomain()
        }
    }

    override suspend fun getPlayerGameLogs(playerId: Int, seasonId: Int): Either<Failure, PlayerLogList> {
        return Either.catchWithError {
            apiService.get<PlayerLogListDto>("/player/$playerId/logs?season_id=$seasonId").toDomain()
        }
    }

    override suspend fun getPlayerStats(playerId: Int): Either<Failure, List<PlayerStat>> {
        return Either.catchWithError {
            apiService.get<PlayerStatListDto>("/player/$playerId/stats/").toDomain()
        }
    }

    override suspend fun getPlayerRecords(playerId: Int): Either<Failure, List<PlayerRecord>> {
        return Either.catchWithError {
            apiService.get<PlayerRecordsDto>("/player/$playerId/records/").toDomain()
        }
    }
}
