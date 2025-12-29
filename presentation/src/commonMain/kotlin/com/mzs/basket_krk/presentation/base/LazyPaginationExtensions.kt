package com.mzs.basket_krk.presentation.base

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

val LazyPagingItems<*>.isLoading
    get() = loadState.refresh is LoadState.Loading || loadState.append is LoadState.Loading

val LazyPagingItems<*>.isInitialLoading
    get() = isLoading && isEmpty

val LazyPagingItems<*>.isEmpty
    get() = itemCount == 0

val LazyPagingItems<*>.isError
    get() = loadState.refresh is LoadState.Error || loadState.append is LoadState.Error
