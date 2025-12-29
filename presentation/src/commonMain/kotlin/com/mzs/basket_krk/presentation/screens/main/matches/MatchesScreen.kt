package com.mzs.basket_krk.presentation.screens.main.matches

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchTeam
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.presentation.base.isEmpty
import com.mzs.basket_krk.presentation.base.isError
import com.mzs.basket_krk.presentation.base.isLoading
import com.mzs.basket_krk.presentation.base.ui.BasketKrkPullToRefresh
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.PaginationErrorItem
import com.mzs.basket_krk.presentation.base.ui.PaginationLoadingIndicator
import com.mzs.basket_krk.presentation.screens.main.matches.components.MatchListItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDate
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
    var wasRefreshFiredByUser by remember { mutableStateOf(false) }
    val showRefresh =
        wasRefreshFiredByUser && matchesPagingItems.loadState.refresh == LoadState.Loading

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                viewState.fullScreenLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewState.error != null || matchesPagingItems.isError -> {
                    ErrorView(error = viewState.error, retryAction = { onRefresh() })
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
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

                        if (matchesPagingItems.isEmpty) {
                            if (matchesPagingItems.isLoading) {
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

    @Composable
    @Preview
    fun MatchesScreenPreview() {

        val match = Match(
            id = 1,
            date = LocalDate(2025, 1, 1),
            time = "18:00",
            team1 = MatchTeam(
                id = 1,
                name = "Home Team",
                shortName = "HT",
                logoUrl = "path/to/logo1.png",
                points = 75
            ),
            team2 = MatchTeam(
                id = 2,
                name = "Away Team",
                shortName = "AT",
                logoUrl = "path/to/logo2.png",
                points = 68
            ),
            status = MatchStatus.FINISHED,
            type = MatchType.REGULAR_SEASON,
            arena = "Main Arena"
        )

        val pagingData = PagingData.from(emptyList<Match>())
        val lazyPagingItems = MutableStateFlow(pagingData).collectAsLazyPagingItems()

        val selectedSeason = Season(id = 1, num = 23)
        val selectedRound = Round(id = 1, name = "Round 1", date = LocalDate(2025, 1, 1))
        MatchesContent(
            viewState = MatchesViewState(
                rounds = listOf(
                    selectedRound,
                    Round(id = 2, name = "Round 2", date = LocalDate(2025, 1, 15))
                ),
                selectedRound = selectedRound,
                seasons = listOf(
                    selectedSeason
                ),
                selectedSeason = selectedSeason
            ),
            onRefresh = {},
            onRoundSelected = {},
            onSeasonSelected = {},
            onMatchDetailsClick = {},
            matchesPagingItems = lazyPagingItems
        )
    }
}