package com.mzs.basket_krk.presentation.screens.main.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.presentation.base.isEmpty
import com.mzs.basket_krk.presentation.base.isError
import com.mzs.basket_krk.presentation.base.isLoading
import com.mzs.basket_krk.presentation.base.ui.BasketKrkPullToRefresh
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.PaginationErrorItem
import com.mzs.basket_krk.presentation.base.ui.PaginationLoadingIndicator
import com.mzs.basket_krk.presentation.screens.main.search.components.SearchListItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    openTeamDetails: (Int) -> Unit,
    openPlayerDetails: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val searchPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    SearchContent(
        viewState = viewState,
        searchPagingItems = searchPagingItems,
        openTeamDetails = openTeamDetails,
        openPlayerDetails = openPlayerDetails,
        onRefresh = viewModel::onRefresh
    )
}

@Composable
fun SearchContent(
    viewState: SearchViewState,
    searchPagingItems: LazyPagingItems<SearchItem>,
    openTeamDetails: (Int) -> Unit,
    openPlayerDetails: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    var wasRefreshFiredByUser by remember { mutableStateOf(false) }
    val showRefresh =
        wasRefreshFiredByUser && searchPagingItems.loadState.refresh == LoadState.Loading

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                searchPagingItems.isError -> {
                    ErrorView(retryAction = { onRefresh() })
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        if (searchPagingItems.isEmpty) {
                            if (searchPagingItems.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text("No matches found")
                            }
                        } else {
                            BasketKrkPullToRefresh(
                                isRefreshing = showRefresh,
                                onRefresh = {
                                    wasRefreshFiredByUser = true
                                    onRefresh()
                                }
                            ) {
                                LazyColumn(Modifier.fillMaxSize()) {
                                    items(searchPagingItems.itemCount) { index ->
                                        searchPagingItems[index]?.let { item ->
                                            SearchListItem(
                                                searchItem = item,
                                                onTeamClick = openTeamDetails,
                                                onPlayerClick = openPlayerDetails,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                )
                                            )
                                        }
                                    }

                                    with(searchPagingItems) {
                                        when {
                                            loadState.refresh is LoadState.Error -> item {
                                                PaginationErrorItem(
                                                    onRetryClick = searchPagingItems::retry
                                                )
                                            }

                                            loadState.append is LoadState.Error -> item {
                                                PaginationErrorItem(
                                                    onRetryClick = searchPagingItems::retry
                                                )
                                            }

                                            isLoading -> item { PaginationLoadingIndicator() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}