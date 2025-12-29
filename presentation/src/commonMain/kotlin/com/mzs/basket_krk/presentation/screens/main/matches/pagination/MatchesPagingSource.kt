package com.mzs.basket_krk.presentation.screens.main.matches.pagination

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.usecase.GetMatches
import com.mzs.basket_krk.domain.usecase.GetMatchesUseCase
import com.mzs.basket_krk.presentation.base.BasePagingSource

class MatchesPagingSource(
    private val pageSize: Int,
    private val roundId: Int,
    private val getMatches: GetMatches
) : BasePagingSource<Match>() {
    override suspend fun fetchData(page: Int): Either<Throwable, PageableData<Match>> {
        Logger.d(
            "XDDDDDDDD fetchData called with page: $page, pageSize: $pageSize, roundId: $roundId"
        )
        return getMatches.invoke(
            GetMatchesUseCase.Input(
                roundId = roundId,
                page = page,
                pageSize = pageSize
            )
        )
    }
}