package com.mzs.basket_krk.presentation.screens.main.matches.pagination

import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.usecase.GetMatches
import com.mzs.basket_krk.presentation.base.BasePagingSource

class MatchesPagingSourceFactory(
    private val getMatches: GetMatches
) : BaseMatchesPagingSourceFactory {
    override fun create(pageSize: Int, roundId: Int): BasePagingSource<Match> {
        return MatchesPagingSource(
            pageSize = pageSize,
            roundId = roundId,
            getMatches = getMatches
        )
    }
}

interface BaseMatchesPagingSourceFactory {
    fun create(pageSize: Int, roundId: Int): BasePagingSource<Match>
}
