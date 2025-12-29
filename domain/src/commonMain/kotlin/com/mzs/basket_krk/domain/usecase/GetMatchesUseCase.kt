package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.repository.MatchRepository

interface GetMatches :
    SuspendInOutUseCase<GetMatchesUseCase.Input, Either<Failure, PageableData<Match>>>

class GetMatchesUseCase(private val matchRepository: MatchRepository) : GetMatches {
    override suspend fun invoke(input: Input): Either<Failure, PageableData<Match>> {
        return matchRepository.getMatches(roundId = input.roundId, page = input.page)
    }

    data class Input(
        val roundId: Int,
        val page: Int,
        //TODO implement page size in backend
        val pageSize: Int
    )
}
