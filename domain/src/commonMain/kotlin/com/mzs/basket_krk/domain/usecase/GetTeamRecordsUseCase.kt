package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.repository.TeamRepository

interface GetTeamRecords :
    SuspendInOutUseCase<GetTeamRecordsUseCase.Input, Either<Failure, List<TeamRecord>>>

class GetTeamRecordsUseCase(private val teamRepository: TeamRepository) : GetTeamRecords {
    override suspend fun invoke(input: Input): Either<Failure, List<TeamRecord>> {
        return teamRepository.getTeamRecords(input.teamId, input.category)
    }

    data class Input(val teamId: Int, val category: String)
}
