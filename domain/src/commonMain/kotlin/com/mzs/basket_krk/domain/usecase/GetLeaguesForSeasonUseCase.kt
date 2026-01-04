package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.repository.LeagueRepository

interface GetLeaguesForSeason :
    SuspendInOutUseCase<GetLeaguesForSeasonUseCase.Input, Either<Failure, List<League>>>

class GetLeaguesForSeasonUseCase(private val repository: LeagueRepository) : GetLeaguesForSeason {
    override suspend fun invoke(input: Input): Either<Failure, List<League>> {
        return repository.getSeasonLeagues(input.seasonId)
    }

    data class Input(val seasonId: Int)
}
