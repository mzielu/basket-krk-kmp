package com.mzs.basket_krk.presentation.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.model.PageableData

abstract class BasePagingSource<T : Any>() : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        Logger.d("XDDDDDDDDDDD Load called with params: $params")
        val nextPage = params.key ?: 1
        return fetchData(page = nextPage).fold(
            ifLeft = { error ->
                Logger.e("Error fetching page $nextPage: $error", error)
                LoadResult.Error(error)
            },
            ifRight = { response ->
                val prevKey = if (nextPage == 1) null else nextPage - 1
                val nextKey = if (response.next.isNullOrEmpty()) null else nextPage + 1
                LoadResult.Page(
                    data = response.data,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        Logger.d("XDDDDDDDDDDD Load called with params: $state")

        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    abstract suspend fun fetchData(page: Int): Either<Throwable, PageableData<T>>
}