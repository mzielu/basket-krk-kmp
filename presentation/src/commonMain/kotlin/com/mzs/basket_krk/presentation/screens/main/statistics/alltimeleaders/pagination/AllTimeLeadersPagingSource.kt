package com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.pagination

import arrow.core.Either
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.PageableData
import com.mzs.basket_krk.domain.usecase.GetAllTimeLeaders
import com.mzs.basket_krk.domain.usecase.GetAllTimeLeadersUseCase
import com.mzs.basket_krk.presentation.base.BasePagingSource

class AllTimeLeadersPagingSource(
    private val pageSize: Int,
    private val statOption: AllTimeStatLeaderOption,
    private val getAllTimeLeaders: GetAllTimeLeaders
) : BasePagingSource<AllTimeLeader>() {
    override suspend fun fetchData(page: Int): Either<Throwable, PageableData<AllTimeLeader>> {
        return getAllTimeLeaders.invoke(
            GetAllTimeLeadersUseCase.Input(
                statOption = statOption,
                page = page,
                pageSize = pageSize
            )
        )
    }
}