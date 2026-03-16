package com.mzs.basket_krk.presentation.screens.teamdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.TeamDetails
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import com.mzs.basket_krk.presentation.screens.teamdetails.components.TeamRecordsTab
import com.mzs.basket_krk.presentation.screens.teamdetails.components.TeamResultsTab
import com.mzs.basket_krk.presentation.screens.teamdetails.components.TeamRosterTab

@Composable
fun TeamDetailsScreen(
    viewModel: TeamDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToMatch: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    TeamDetailsContent(
        viewState = viewState,
        onRetry = viewModel::retry,
        onTabSelected = viewModel::onTabSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onNavigateBack = onNavigateBack,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToMatch = onNavigateToMatch,
    )
}

@Composable
fun TeamDetailsContent(
    viewState: TeamDetailsViewState,
    onRetry: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToMatch: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = viewState.teamDetails.data?.name ?: "Team Details",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                viewState.teamDetails.isLoading -> FullScreenLoader()
                viewState.teamDetails.isError -> ErrorView(
                    error = viewState.teamDetails.error,
                    retryAction = onRetry
                )
                else -> viewState.teamDetails.data?.let { details ->
                    TeamDetailsBody(
                        details = details,
                        viewState = viewState,
                        onTabSelected = onTabSelected,
                        onSeasonSelected = onSeasonSelected,
                        onNavigateToPlayer = onNavigateToPlayer,
                        onNavigateToMatch = onNavigateToMatch,
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamDetailsBody(
    details: TeamDetails,
    viewState: TeamDetailsViewState,
    onTabSelected: (Int) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToMatch: (Int) -> Unit,
) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasketKrkImage(
                logoUrl = details.logoUrl,
                contentDescription = "${details.name} logo",
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = details.name,
                    style = BasketKrkStyles.itemName,
                )
                details.league?.let { league ->
                    Text(
                        text = league.name,
                        style = BasketKrkStyles.itemAdditionalInfo,
                    )
                }
                if (details.seasons.isNotEmpty()) {
                    Text(
                        text = details.seasons.joinToString(", ") { it.num.toString() },
                        style = BasketKrkStyles.itemAdditionalInfo,
                    )
                }
                // W-L and point differential row
                val wlText = viewState.winsLosses?.let { wl ->
                    val pm = viewState.pointDifferential ?: 0
                    "${wl.first}-${wl.second}  ${if (pm >= 0) "+" else ""}$pm"
                } ?: "-"
                Text(
                    text = wlText,
                    style = BasketKrkStyles.itemAdditionalInfo,
                )
            }
        }

        HorizontalDivider(color = BasketKrkColors.Main)

        // Tab row
        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(BasketKrkColors.TabUnselected)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = BasketKrkColors.TabUnselected,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(selectedTabIndex),
                        color = BasketKrkColors.Main,
                        height = 4.dp
                    )
                },
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = {
                        selectedTabIndex = 0
                        onTabSelected(0)
                    },
                    text = {
                        Text(
                            text = "Results",
                            color = BasketKrkColors.TabText,
                            style = if (selectedTabIndex == 0)
                                BasketKrkStyles.fixedRowText
                            else
                                BasketKrkStyles.fixedRowText.copy(fontWeight = null)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = {
                        selectedTabIndex = 1
                        onTabSelected(1)
                    },
                    text = {
                        Text(
                            text = "Roster",
                            color = BasketKrkColors.TabText,
                            style = if (selectedTabIndex == 1)
                                BasketKrkStyles.fixedRowText
                            else
                                BasketKrkStyles.fixedRowText.copy(fontWeight = null)
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = {
                        selectedTabIndex = 2
                        onTabSelected(2)
                    },
                    text = {
                        Text(
                            text = "Records",
                            color = BasketKrkColors.TabText,
                            style = if (selectedTabIndex == 2)
                                BasketKrkStyles.fixedRowText
                            else
                                BasketKrkStyles.fixedRowText.copy(fontWeight = null)
                        )
                    }
                )
            }
        }

        // Tab content
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTabIndex) {
                0 -> {
                    when {
                        viewState.results.isLoading -> FullScreenLoader()
                        viewState.results.isError -> ErrorView(
                            error = viewState.results.error,
                            retryAction = null
                        )
                        else -> viewState.results.data?.let { resultList ->
                            TeamResultsTab(
                                resultList = resultList,
                                onMatchPress = onNavigateToMatch,
                            )
                        }
                    }
                }
                1 -> {
                    when {
                        viewState.roster.isLoading -> FullScreenLoader()
                        viewState.roster.isError -> ErrorView(
                            error = viewState.roster.error,
                            retryAction = null
                        )
                        else -> viewState.roster.data?.let { roster ->
                            TeamRosterTab(
                                roster = roster,
                                onPlayerPress = onNavigateToPlayer,
                            )
                        }
                    }
                }
                else -> {
                    when {
                        viewState.records.isLoading -> FullScreenLoader()
                        viewState.records.isError -> ErrorView(
                            error = viewState.records.error,
                            retryAction = null
                        )
                        else -> viewState.records.data?.let { records ->
                            TeamRecordsTab(
                                records = records,
                                onPlayerPress = onNavigateToPlayer,
                                onMatchPress = onNavigateToMatch,
                            )
                        }
                    }
                }
            }
        }
    }
}
