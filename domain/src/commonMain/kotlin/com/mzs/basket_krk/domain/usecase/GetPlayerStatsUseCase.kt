package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.repository.PlayerRepository

interface GetPlayerStats :
    SuspendInOutUseCase<GetPlayerStatsUseCase.Input, Either<Failure, List<PlayerStat>>>

class GetPlayerStatsUseCase(private val playerRepository: PlayerRepository) : GetPlayerStats {
    override suspend fun invoke(input: Input): Either<Failure, List<PlayerStat>> {
        return playerRepository.getPlayerStats(input.playerId)
    }

    data class Input(val playerId: Int)
}
