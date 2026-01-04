package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.repository.LeagueRepository

interface GetAllTimeLeaders :
    SuspendInOutUseCase<GetAllTimeLeadersUseCase.Input, Either<Failure, PageableData<AllTimeLeader>>>

class GetAllTimeLeadersUseCase(private val leagueRepository: LeagueRepository) : GetAllTimeLeaders {
    override suspend fun invoke(input: Input): Either<Failure, PageableData<AllTimeLeader>> {
        return leagueRepository.getAllTimeLeaders(statOption = input.statOption, page = input.page)
    }

    data class Input(
        val statOption: AllTimeStatLeaderOption,
        val page: Int,
        // TODO implement page size in backend
        val pageSize: Int
    )
}
