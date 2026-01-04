package com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.pagination

import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.usecase.GetAllTimeLeaders
import com.mzs.basket_krk.presentation.base.BasePagingSource

class AllTimeLeadersPagingSourceFactory(
    private val getAllTimeLeaders: GetAllTimeLeaders
) : BaseAllTimeLeadersPagingSourceFactory {
    override fun create(
        pageSize: Int,
        statOption: AllTimeStatLeaderOption
    ): BasePagingSource<AllTimeLeader> {
        return AllTimeLeadersPagingSource(
            pageSize = pageSize,
            statOption = statOption,
            getAllTimeLeaders = getAllTimeLeaders
        )
    }
}

interface BaseAllTimeLeadersPagingSourceFactory {
    fun create(pageSize: Int, statOption: AllTimeStatLeaderOption): BasePagingSource<AllTimeLeader>
}
