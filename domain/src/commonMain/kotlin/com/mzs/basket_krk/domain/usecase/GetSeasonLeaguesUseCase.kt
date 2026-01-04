package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.repository.LeagueRepository

interface GetSeasonLeagues :
    SuspendInOutUseCase<GetSeasonLeaguesUseCase.Input, Either<Failure, List<League>>>

class GetSeasonLeaguesUseCase(private val repository: LeagueRepository) : GetSeasonLeagues {
    override suspend fun invoke(input: Input): Either<Failure, List<League>> {
        return repository.getSeasonLeagues(input.seasonId)
    }

    data class Input(val seasonId: Int)
}
