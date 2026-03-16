package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.repository.TeamRepository

interface GetTeamRoster :
    SuspendInOutUseCase<GetTeamRosterUseCase.Input, Either<Failure, List<PlayerWithStat>>>

class GetTeamRosterUseCase(private val teamRepository: TeamRepository) : GetTeamRoster {
    override suspend fun invoke(input: Input): Either<Failure, List<PlayerWithStat>> {
        return teamRepository.getTeamRoster(input.teamId, input.seasonId)
    }

    data class Input(val teamId: Int, val seasonId: Int)
}
