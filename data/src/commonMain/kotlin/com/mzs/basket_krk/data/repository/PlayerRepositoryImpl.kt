package com.mzs.basket_krk.data.repository

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.repository.PlayerRepository
import com.mzs.basket_krk.domain.service.PlayerService

class PlayerRepositoryImpl(private val playerService: PlayerService) : PlayerRepository {
    override suspend fun getPlayerDetails(playerId: Int): Either<Failure, PlayerDetails> {
        return playerService.getPlayerDetails(playerId)
    }

    override suspend fun getPlayerGameLogs(playerId: Int, seasonId: Int): Either<Failure, PlayerLogList> {
        return playerService.getPlayerGameLogs(playerId, seasonId)
    }

    override suspend fun getPlayerStats(playerId: Int): Either<Failure, List<PlayerStat>> {
        return playerService.getPlayerStats(playerId)
    }

    override suspend fun getPlayerRecords(playerId: Int): Either<Failure, List<PlayerRecord>> {
        return playerService.getPlayerRecords(playerId)
    }
}
