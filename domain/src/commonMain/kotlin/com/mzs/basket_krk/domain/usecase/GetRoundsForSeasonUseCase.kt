package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.repository.SeasonRepository

interface GetRoundsForSeason : SuspendInOutUseCase<Int, Either<Failure, List<Round>>>

class GetRoundsForSeasonUseCase(private val seasonRepository: SeasonRepository) :
    GetRoundsForSeason {
    override suspend fun invoke(input: Int): Either<Failure, List<Round>> {
        return seasonRepository.getRounds(input)
    }
}
