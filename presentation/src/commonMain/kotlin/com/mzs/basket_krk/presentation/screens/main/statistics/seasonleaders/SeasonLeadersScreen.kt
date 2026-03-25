package com.mzs.basket_krk.presentation.screens.main.statistics.seasonleaders

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
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.LeagueStatLeaderOption
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.EmptyView
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import com.mzs.basket_krk.presentation.screens.main.statistics.seasonleaders.components.SeasonLeaderItem
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SeasonLeadersScreen(
    viewModel: SeasonLeadersViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    SeasonLeadersContent(
        viewState = viewState,
        onSeasonSelected = viewModel::onSeasonSelected,
        onLeagueSelected = viewModel::onLeagueSelected,
        onStatOptionChanged = viewModel::onStatOptionChanged,
        onRefresh = viewModel::onRefresh,
        onNavigateBack = onNavigateBack,
        onNavigateToPlayer = onNavigateToPlayer,
    )
}

@Composable
fun SeasonLeadersContent(
    viewState: SeasonLeadersViewState,
    onSeasonSelected: (Season) -> Unit,
    onLeagueSelected: (League) -> Unit,
    onStatOptionChanged: (LeagueStatLeaderOption) -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = "Season Leaders",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
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
                    modifier = Modifier.weight(1.3f),
                    label = "Season",
                    options = viewState.seasons,
                    selectedOption = viewState.selectedSeason,
                    onOptionSelected = onSeasonSelected,
                    readableValue = { it?.num.toString() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownFormField(
                    modifier = Modifier.weight(2.7f),
                    label = "League",
                    options = viewState.leagues,
                    selectedOption = viewState.selectedLeague,
                    onOptionSelected = onLeagueSelected,
                    readableValue = { it?.name.orEmpty() }
                )

                Spacer(modifier = Modifier.width(8.dp))

                DropdownFormField(
                    modifier = Modifier.weight(1.5f),
                    label = "Category",
                    options = LeagueStatLeaderOption.entries,
                    selectedOption = viewState.selectedStatOption,
                    onOptionSelected = onStatOptionChanged,
                    readableValue = { it?.label.orEmpty() }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                when {
                    viewState.fullScreenLoading -> {
                        FullScreenLoader()
                    }

                    viewState.error != null -> {
                        ErrorView(error = viewState.error, retryAction = { onRefresh() })
                    }

                    viewState.leaders.isEmpty() -> {
                        EmptyView()
                    }

                    else -> {
                        LazyColumn {
                            items(viewState.leaders.size) { index ->
                                SeasonLeaderItem(
                                    leader = viewState.leaders[index],
                                    onOpenPlayerDetails = { player -> onNavigateToPlayer(player.id) },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
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
fun SeasonLeadersContentPreview() {
    SeasonLeadersContent(
        viewState = SeasonLeadersViewState(),
        onSeasonSelected = {},
        onLeagueSelected = {},
        onStatOptionChanged = {},
        onRefresh = {},
        onNavigateBack = {},
        onNavigateToPlayer = {},
    )
}
