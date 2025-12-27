package com.mzs.basket_krk.presentation.base

import androidx.paging.PagingSource
import androidx.paging.PagingState
import arrow.core.Either
import co.touchlab.kermit.Logger

abstract class BasePagingSource<T : Any>() : PagingSource<Int, T>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val nextPage = params.key ?: 1
        return fetchData(page = nextPage).fold(
            ifLeft = { error ->
                Logger.e("Error fetching page $nextPage: $error", error)
                LoadResult.Error(error)
            },
            ifRight = { response ->
                val prevKey = if (nextPage == 1) null else nextPage - 1
                LoadResult.Page(
                    data = response,
                    prevKey = prevKey,
                    nextKey = null
                )
            }
        )
    }

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    abstract suspend fun fetchData(page: Int): Either<Throwable, List<T>>
}