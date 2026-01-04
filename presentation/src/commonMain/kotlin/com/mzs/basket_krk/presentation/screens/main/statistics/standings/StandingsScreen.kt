package com.mzs.basket_krk.presentation.screens.main.statistics.standings


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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.datautils.LeagueFakeData
import com.mzs.basket_krk.datautils.SeasonFakeData
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.EmptyView
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    StandingsContent(
        viewState = viewState,
        onLeagueSelected = viewModel::onLeagueSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onRefresh = viewModel::onRefresh,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun StandingsContent(
    viewState: StandingsViewState,
    onLeagueSelected: (League) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
) {
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
            when {
                viewState.fullScreenLoading -> {
                    FullScreenLoader()
                }

                viewState.error != null -> {
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
                                label = "League",
                                options = viewState.leagues,
                                selectedOption = viewState.selectedLeague,
                                onOptionSelected = onLeagueSelected,
                                readableValue = { it?.name.orEmpty() }
                            )
                        }

                        viewState.leagueDetails?.let {
                            Text(it.name)
                        } ?: EmptyView()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun StandingsContentPreview() {
    val selectedSeason = SeasonFakeData.season()
    val selectedLeague = LeagueFakeData.league(name = "League 1")

    StandingsContent(
        viewState = StandingsViewState(
            leagues = listOf(selectedLeague),
            selectedLeague = selectedLeague,
            seasons = listOf(selectedSeason),
            selectedSeason = selectedSeason,
            leagueDetails = LeagueFakeData.leagueDetails()
        ),
        onRefresh = {},
        onSeasonSelected = {},
        onLeagueSelected = {},
        onNavigateBack = {}
    )
}