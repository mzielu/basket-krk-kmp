package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.repository.MatchRepository

interface GetMatches : SuspendInOutUseCase<Int, Either<Failure, List<Match>>>

class GetMatchesUseCase(private val matchRepository: MatchRepository) : GetMatches {
    override suspend fun invoke(input: Int): Either<Failure, List<Match>> {
        return matchRepository.getMatches(input)
    }
}
