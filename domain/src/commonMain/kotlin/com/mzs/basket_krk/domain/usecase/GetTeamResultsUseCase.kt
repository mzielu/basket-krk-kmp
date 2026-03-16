package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.TeamResultList
import com.mzs.basket_krk.domain.repository.TeamRepository

interface GetTeamResults :
    SuspendInOutUseCase<GetTeamResultsUseCase.Input, Either<Failure, TeamResultList>>

class GetTeamResultsUseCase(private val teamRepository: TeamRepository) : GetTeamResults {
    override suspend fun invoke(input: Input): Either<Failure, TeamResultList> {
        return teamRepository.getTeamResults(input.teamId, input.seasonId)
    }

    data class Input(val teamId: Int, val seasonId: Int)
}
