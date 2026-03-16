package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.repository.PlayerRepository

interface GetPlayerRecords :
    SuspendInOutUseCase<GetPlayerRecordsUseCase.Input, Either<Failure, List<PlayerRecord>>>

class GetPlayerRecordsUseCase(private val playerRepository: PlayerRepository) : GetPlayerRecords {
    override suspend fun invoke(input: Input): Either<Failure, List<PlayerRecord>> {
        return playerRepository.getPlayerRecords(input.playerId)
    }

    data class Input(val playerId: Int)
}
