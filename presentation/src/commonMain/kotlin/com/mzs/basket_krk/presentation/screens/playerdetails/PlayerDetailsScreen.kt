package com.mzs.basket_krk.presentation.screens.playerdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerDetails
import com.mzs.basket_krk.domain.model.PlayerLogByTeam
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import com.mzs.basket_krk.presentation.base.ui.FullScreenLoader
import com.mzs.basket_krk.presentation.screens.playerdetails.components.PlayerGameLogsTab
import com.mzs.basket_krk.presentation.screens.playerdetails.components.PlayerRecordsTab
import com.mzs.basket_krk.presentation.screens.playerdetails.components.PlayerStatsTab

@Composable
fun PlayerDetailsScreen(
    viewModel: PlayerDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMatch: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()

    PlayerDetailsContent(
        viewState = viewState,
        onRetry = viewModel::retry,
        onTabSelected = viewModel::onTabSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onTeamSelected = viewModel::onTeamSelected,
        onSortByStat = viewModel::onSortByStat,
        onStatDisplayTypeChanged = viewModel::onStatDisplayTypeChanged,
        onNavigateBack = onNavigateBack,
        onNavigateToMatch = onNavigateToMatch,
        onNavigateToTeam = onNavigateToTeam,
    )
}

@Composable
fun PlayerDetailsContent(
    viewState: PlayerDetailsViewState,
    onRetry: () -> Unit,
    onTabSelected: (Int) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onTeamSelected: (PlayerLogByTeam) -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onStatDisplayTypeChanged: (StatDisplayType) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMatch: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = viewState.playerDetails.data?.let {
                    "${it.firstName} ${it.lastName}"
                } ?: "Player Details",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                viewState.playerDetails.isLoading -> FullScreenLoader()
                viewState.playerDetails.isError -> ErrorView(
                    error = viewState.playerDetails.error,
                    retryAction = onRetry
                )
                else -> viewState.playerDetails.data?.let { details ->
                    PlayerDetailsBody(
                        details = details,
                        viewState = viewState,
                        onTabSelected = onTabSelected,
                        onSeasonSelected = onSeasonSelected,
                        onTeamSelected = onTeamSelected,
                        onSortByStat = onSortByStat,
                        onStatDisplayTypeChanged = onStatDisplayTypeChanged,
                        onNavigateToMatch = onNavigateToMatch,
                        onNavigateToTeam = onNavigateToTeam,
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerDetailsBody(
    details: PlayerDetails,
    viewState: PlayerDetailsViewState,
    onTabSelected: (Int) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onTeamSelected: (PlayerLogByTeam) -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onStatDisplayTypeChanged: (StatDisplayType) -> Unit,
    onNavigateToMatch: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
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
            details.team?.let { team ->
                BasketKrkImage(
                    logoUrl = team.logoUrl,
                    contentDescription = "${team.name} logo",
                    modifier = Modifier
                        .size(64.dp)
                        .clickable { onNavigateToTeam(team.id) }
                )
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text(
                        text = "${details.firstName} ${details.lastName}",
                        style = BasketKrkStyles.itemName,
                    )
                    Text(
                        text = team.name,
                        style = BasketKrkStyles.itemAdditionalInfo,
                        modifier = Modifier.clickable { onNavigateToTeam(team.id) }
                    )
                    if (details.seasons.isNotEmpty()) {
                        Text(
                            text = details.seasons.joinToString(", ") { it.num.toString() },
                            style = BasketKrkStyles.itemAdditionalInfo,
                        )
                    }
                }
            } ?: run {
                Column {
                    Text(
                        text = "${details.firstName} ${details.lastName}",
                        style = BasketKrkStyles.itemName,
                    )
                    if (details.seasons.isNotEmpty()) {
                        Text(
                            text = details.seasons.joinToString(", ") { it.num.toString() },
                            style = BasketKrkStyles.itemAdditionalInfo,
                        )
                    }
                }
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
                            text = "Game Logs",
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
                            text = "Stats",
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
                        viewState.gameLogs.isLoading -> FullScreenLoader()
                        viewState.gameLogs.isError -> ErrorView(
                            error = viewState.gameLogs.error,
                            retryAction = null
                        )
                        else -> viewState.gameLogs.data?.let { playerLogList ->
                            PlayerGameLogsTab(
                                playerLogList = playerLogList,
                                selectedTeam = viewState.selectedTeam,
                                selectedSeason = viewState.selectedSeason,
                                seasons = details.seasons,
                                sortOption = viewState.sortOption,
                                sortAscending = viewState.sortAscending,
                                onSeasonSelected = onSeasonSelected,
                                onTeamSelected = onTeamSelected,
                                onSortByStat = onSortByStat,
                                onMatchPress = onNavigateToMatch,
                            )
                        }
                    }
                }
                1 -> {
                    when {
                        viewState.stats.isLoading -> FullScreenLoader()
                        viewState.stats.isError -> ErrorView(
                            error = viewState.stats.error,
                            retryAction = null
                        )
                        else -> viewState.stats.data?.let { statList ->
                            PlayerStatsTab(
                                playersStats = statList,
                                statDisplayType = viewState.statDisplayType,
                                onStatDisplayTypeChanged = onStatDisplayTypeChanged,
                                onTeamPress = onNavigateToTeam,
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
                        else -> viewState.records.data?.let { recordList ->
                            PlayerRecordsTab(
                                records = recordList,
                                onRecordPress = onNavigateToMatch,
                            )
                        }
                    }
                }
            }
        }
    }
}
