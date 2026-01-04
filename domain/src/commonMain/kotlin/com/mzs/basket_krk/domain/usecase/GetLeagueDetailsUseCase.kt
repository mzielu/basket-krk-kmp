package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.LeagueDetails
import com.mzs.basket_krk.domain.repository.LeagueRepository

interface GetLeagueDetails :
    SuspendInOutUseCase<GetLeagueDetailsUseCase.Input, Either<Failure, LeagueDetails>>

class GetLeagueDetailsUseCase(private val repository: LeagueRepository) : GetLeagueDetails {
    override suspend fun invoke(input: Input): Either<Failure, LeagueDetails> {
        return repository.getLeagueDetails(input.leagueId)
    }

    data class Input(val leagueId: Int)
}
