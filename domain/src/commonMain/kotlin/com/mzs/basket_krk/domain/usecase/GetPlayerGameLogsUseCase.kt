package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.repository.PlayerRepository

interface GetPlayerGameLogs :
    SuspendInOutUseCase<GetPlayerGameLogsUseCase.Input, Either<Failure, PlayerLogList>>

class GetPlayerGameLogsUseCase(private val playerRepository: PlayerRepository) : GetPlayerGameLogs {
    override suspend fun invoke(input: Input): Either<Failure, PlayerLogList> {
        return playerRepository.getPlayerGameLogs(input.playerId, input.seasonId)
    }

    data class Input(val playerId: Int, val seasonId: Int)
}
