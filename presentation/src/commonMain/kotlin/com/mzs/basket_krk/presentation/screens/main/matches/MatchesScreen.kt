package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mzs.basket_krk.datautils.MatchFakeData
import com.mzs.basket_krk.datautils.SeasonFakeData
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.presentation.base.isEmpty
import com.mzs.basket_krk.presentation.base.isError
import com.mzs.basket_krk.presentation.base.isLoading
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkPullToRefresh
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.EmptyView
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.PaginationErrorItem
import com.mzs.basket_krk.presentation.base.ui.PaginationLoadingIndicator
import com.mzs.basket_krk.presentation.screens.main.matches.components.MatchListItem
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel = koinViewModel(),
    openMatchDetails: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val matchesPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    MatchesContent(
        viewState = viewState,
        matchesPagingItems = matchesPagingItems,
        onRoundSelected = viewModel::onRoundSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onMatchDetailsClick = openMatchDetails,
        onRefresh = viewModel::onRefresh
    )
}

@Composable
fun MatchesContent(
    viewState: MatchesViewState,
    matchesPagingItems: LazyPagingItems<Match>,
    onRoundSelected: (Round) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onMatchDetailsClick: (Int) -> Unit,
    onRefresh: () -> Unit,
) {
    val showRefresh =
        (viewState.fullScreenLoading) || matchesPagingItems.loadState.refresh == LoadState.Loading

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BasketKrkColors.DefaultBackground)
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DropdownFormField(
                    modifier = Modifier.weight(1f),
                    label = "Season",
                    options = viewState.seasons,
                    selectedOption = viewState.selectedSeason,
                    onOptionSelected = onSeasonSelected,
                    readableValue = { it?.num.toString() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownFormField(
                    modifier = Modifier.weight(3f),
                    label = "Round",
                    options = viewState.rounds,
                    selectedOption = viewState.selectedRound,
                    onOptionSelected = onRoundSelected,
                    readableValue = { it?.name.orEmpty() }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    viewState.error != null || matchesPagingItems.isError -> {
                        ErrorView(error = viewState.error, retryAction = { onRefresh() })
                    }

                    !viewState.fullScreenLoading && matchesPagingItems.isEmpty && !matchesPagingItems.isLoading -> {
                        EmptyView()
                    }

                    else -> {
                        BasketKrkPullToRefresh(
                            isRefreshing = showRefresh,
                            onRefresh = {
                                onRefresh()
                            }
                        ) {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(matchesPagingItems.itemCount) { index ->
                                    matchesPagingItems[index]?.let { match ->
                                        MatchListItem(
                                            match = match,
                                            onClick = onMatchDetailsClick,
                                            modifier = Modifier.padding(
                                                vertical = 4.dp,
                                                horizontal = 8.dp
                                            )
                                        )
                                    }
                                }

                                with(matchesPagingItems) {
                                    when {
                                        loadState.refresh is LoadState.Error -> item {
                                            PaginationErrorItem(
                                                onRetryClick = matchesPagingItems::retry
                                            )
                                        }

                                        loadState.append is LoadState.Error -> item {
                                            PaginationErrorItem(
                                                onRetryClick = matchesPagingItems::retry
                                            )
                                        }

                                        isLoading && !isEmpty -> item { PaginationLoadingIndicator() }
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

@Composable
@Preview
fun MatchesScreenPreview() {
    val pagingData = PagingData.from(listOf(MatchFakeData.match()))
    val lazyPagingItems = MutableStateFlow(pagingData).collectAsLazyPagingItems()

    val selectedSeason = SeasonFakeData.season()
    val selectedRound = SeasonFakeData.round(name = "Round 1")
    MatchesContent(
        viewState = MatchesViewState(
            rounds = listOf(selectedRound),
            selectedRound = selectedRound,
            seasons = listOf(selectedSeason),
            selectedSeason = selectedSeason
        ),
        onRefresh = {},
        onRoundSelected = {},
        onSeasonSelected = {},
        onMatchDetailsClick = {},
        matchesPagingItems = lazyPagingItems
    )
}