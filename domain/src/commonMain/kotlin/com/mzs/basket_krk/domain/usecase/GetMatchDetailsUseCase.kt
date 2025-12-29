package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.repository.MatchRepository

interface GetMatchDetails :
    SuspendInOutUseCase<GetMatchDetailsUseCase.Input, Either<Failure, MatchDetails>>

class GetMatchDetailsUseCase(private val matchRepository: MatchRepository) : GetMatchDetails {
    override suspend fun invoke(input: Input): Either<Failure, MatchDetails> {
        return matchRepository.getMatchDetails(input.matchId)
    }

    data class Input(val matchId: Int)
}
