package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.repository.PlayerRepository

interface GetPlayerDetails :
    SuspendInOutUseCase<GetPlayerDetailsUseCase.Input, Either<Failure, PlayerDetails>>

class GetPlayerDetailsUseCase(private val playerRepository: PlayerRepository) : GetPlayerDetails {
    override suspend fun invoke(input: Input): Either<Failure, PlayerDetails> {
        return playerRepository.getPlayerDetails(input.playerId).map { details ->
            details.copy(seasons = details.seasons.sortedByDescending { it.num })
        }
    }

    data class Input(val playerId: Int)
}
