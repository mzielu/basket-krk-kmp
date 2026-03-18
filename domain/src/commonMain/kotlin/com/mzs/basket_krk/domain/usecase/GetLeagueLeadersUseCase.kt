package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.LeagueLeader
import com.mzs.basket_krk.domain.model.LeagueStatLeaderOption
import com.mzs.basket_krk.domain.repository.LeagueRepository

interface GetLeagueLeaders :
    SuspendInOutUseCase<GetLeagueLeadersUseCase.Input, Either<Failure, List<LeagueLeader>>>

class GetLeagueLeadersUseCase(private val leagueRepository: LeagueRepository) : GetLeagueLeaders {
    override suspend fun invoke(input: Input): Either<Failure, List<LeagueLeader>> {
        return leagueRepository.getLeagueLeaders(leagueId = input.leagueId, statOption = input.statOption)
    }

    data class Input(
        val leagueId: Int,
        val statOption: LeagueStatLeaderOption
    )
}
