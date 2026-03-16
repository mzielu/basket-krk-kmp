package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.PlayerStat

interface PlayerService {
    suspend fun getPlayerDetails(playerId: Int): Either<Failure, PlayerDetails>
    suspend fun getPlayerGameLogs(playerId: Int, seasonId: Int): Either<Failure, PlayerLogList>
    suspend fun getPlayerStats(playerId: Int): Either<Failure, List<PlayerStat>>
    suspend fun getPlayerRecords(playerId: Int): Either<Failure, List<PlayerRecord>>
}
