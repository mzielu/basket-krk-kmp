package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.domain.repository.TeamRepository

interface GetTeamDetails :
    SuspendInOutUseCase<GetTeamDetailsUseCase.Input, Either<Failure, TeamDetails>>

class GetTeamDetailsUseCase(private val teamRepository: TeamRepository) : GetTeamDetails {
    override suspend fun invoke(input: Input): Either<Failure, TeamDetails> {
        return teamRepository.getTeamDetails(input.teamId).map { details ->
            details.copy(seasons = details.seasons.sortedByDescending { it.num })
        }
    }

    data class Input(val teamId: Int)
}
