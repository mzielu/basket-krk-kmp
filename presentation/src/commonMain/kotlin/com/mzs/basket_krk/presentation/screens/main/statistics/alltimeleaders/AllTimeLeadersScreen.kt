package com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.all_time_category_assists
import basket_krk.presentation.generated.resources.all_time_category_blocks
import basket_krk.presentation.generated.resources.all_time_category_dd
import basket_krk.presentation.generated.resources.all_time_category_fg3m
import basket_krk.presentation.generated.resources.all_time_category_fgm
import basket_krk.presentation.generated.resources.all_time_category_ftm
import basket_krk.presentation.generated.resources.all_time_category_games
import basket_krk.presentation.generated.resources.all_time_category_points
import basket_krk.presentation.generated.resources.all_time_category_qd
import basket_krk.presentation.generated.resources.all_time_category_rebounds
import basket_krk.presentation.generated.resources.all_time_category_steals
import basket_krk.presentation.generated.resources.all_time_category_td
import com.mzs.basket_krk.datautils.LeagueFakeData
import com.mzs.basket_krk.domain.model.AllTimeLeader
import com.mzs.basket_krk.domain.model.AllTimeStatLeaderOption
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.presentation.base.isEmpty
import com.mzs.basket_krk.presentation.base.isError
import com.mzs.basket_krk.presentation.base.isLoading
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkPullToRefresh
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.EmptyView
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import com.mzs.basket_krk.presentation.base.ui.PaginationErrorItem
import com.mzs.basket_krk.presentation.base.ui.PaginationLoadingIndicator
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.components.LeaderItem
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AllTimeLeadersScreen(
    viewModel: AllTimeLeadersViewModel = koinViewModel(),
    openPlayerDetails: (Int) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val leadersPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    AllTimeLeadersContent(
        viewState = viewState,
        leadersPagingItems = leadersPagingItems,
        onPlayerClick = {
            openPlayerDetails.invoke(it.id)
        },
        onRefresh = viewModel::onRefresh,
        onStatOptionChanged = viewModel::onStatOptionChanged,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun AllTimeLeadersContent(
    viewState: AllTimeLeadersViewState,
    leadersPagingItems: LazyPagingItems<AllTimeLeader>,
    onPlayerClick: (SearchItem.Player) -> Unit,
    onNavigateBack: () -> Unit,
    onRefresh: () -> Unit,
    onStatOptionChanged: (AllTimeStatLeaderOption) -> Unit,
) {
    var wasRefreshFiredByUser by remember { mutableStateOf(false) }
    val showRefresh =
        wasRefreshFiredByUser && leadersPagingItems.loadState.refresh == LoadState.Loading

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = "All Time Leaders",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BasketKrkColors.DefaultBackground)
                .padding(innerPadding)
        ) {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Expanded title
                    Box(
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = stringResource(viewState.selectedStatOption.titleRes()),
                            style = BasketKrkStyles.categoryTitle,
                            softWrap = true
                        )
                    }

                    DropdownFormField(
                        modifier = Modifier.width(100.dp),
                        label = "Round",
                        options = AllTimeStatLeaderOption.entries,
                        selectedOption = viewState.selectedStatOption,
                        onOptionSelected = onStatOptionChanged,
                        readableValue = { it?.label.orEmpty() }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    when {
                        leadersPagingItems.isError -> {
                            ErrorView(retryAction = { onRefresh() })
                        }

                        else -> {
                            Column(modifier = Modifier.fillMaxSize()) {
                                if (leadersPagingItems.isEmpty) {
                                    if (leadersPagingItems.isLoading) {
                                        FullScreenLoader()
                                    } else {
                                        EmptyView()
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
                                            items(leadersPagingItems.itemCount) { index ->
                                                leadersPagingItems[index]?.let { item ->
                                                    LeaderItem(
                                                        leader = item,
                                                        onOpenPlayerDetails = onPlayerClick,
                                                        modifier = Modifier.padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        )
                                                    )
                                                }
                                            }

                                            with(leadersPagingItems) {
                                                when {
                                                    loadState.refresh is LoadState.Error -> item {
                                                        PaginationErrorItem(
                                                            onRetryClick = leadersPagingItems::retry
                                                        )
                                                    }

                                                    loadState.append is LoadState.Error -> item {
                                                        PaginationErrorItem(
                                                            onRetryClick = leadersPagingItems::retry
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
    }
}

fun AllTimeStatLeaderOption.titleRes(): StringResource =
    when (this) {
        AllTimeStatLeaderOption.PTS -> Res.string.all_time_category_points
        AllTimeStatLeaderOption.AST -> Res.string.all_time_category_assists
        AllTimeStatLeaderOption.REB -> Res.string.all_time_category_rebounds
        AllTimeStatLeaderOption.STL -> Res.string.all_time_category_steals
        AllTimeStatLeaderOption.BLK -> Res.string.all_time_category_blocks
        AllTimeStatLeaderOption.FT -> Res.string.all_time_category_ftm
        AllTimeStatLeaderOption.FG -> Res.string.all_time_category_fgm
        AllTimeStatLeaderOption.FG3 -> Res.string.all_time_category_fg3m
        AllTimeStatLeaderOption.GMS -> Res.string.all_time_category_games
        AllTimeStatLeaderOption.DD -> Res.string.all_time_category_dd
        AllTimeStatLeaderOption.TD -> Res.string.all_time_category_td
        AllTimeStatLeaderOption.QD -> Res.string.all_time_category_qd
    }


@Composable
@Preview
fun AllTimeLeadersContentPreview() {
    val pagingData = PagingData.from(listOf(LeagueFakeData.allTimeLeader()))
    val lazyPagingItems = MutableStateFlow(pagingData).collectAsLazyPagingItems()

    AllTimeLeadersContent(
        viewState = AllTimeLeadersViewState(),
        onRefresh = {},
        onPlayerClick = {},
        leadersPagingItems = lazyPagingItems,
        onNavigateBack = {},
        onStatOptionChanged = {},
    )
}