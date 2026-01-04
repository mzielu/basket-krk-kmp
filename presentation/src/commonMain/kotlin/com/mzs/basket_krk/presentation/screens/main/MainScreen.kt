package com.mzs.basket_krk.presentation.screens.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.navigation.MainTab
import com.mzs.basket_krk.presentation.navigation.icon
import com.mzs.basket_krk.presentation.navigation.titleRes
import com.mzs.basket_krk.presentation.screens.main.matches.MatchesScreen
import com.mzs.basket_krk.presentation.screens.main.more.MoreScreen
import com.mzs.basket_krk.presentation.screens.main.search.SearchScreen
import com.mzs.basket_krk.presentation.screens.main.statistics.StatisticsScreen
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    openMatchDetails: (Int) -> Unit,
    openTeamDetails: (Int) -> Unit,
    openPlayerDetails: (Int) -> Unit,
    openAllTimeLeaders: () -> Unit,
) {
    val selectedTab by viewModel.selectedTab.collectAsState()

    MainContent(
        selectedTab = selectedTab,
        onTabSelected = { viewModel.selectTab(it) },
        contentFactory = { tab ->
            when (tab) {
                MainTab.MATCHES -> MatchesScreen(openMatchDetails = openMatchDetails)
                MainTab.SEARCH -> SearchScreen(
                    openTeamDetails = openTeamDetails,
                    openPlayerDetails = openPlayerDetails
                )

                MainTab.STATS -> StatisticsScreen(
                    openTables = {},
                    openLeagueLeaders = {},
                    openAllTimeLeaders = openAllTimeLeaders
                )

                MainTab.MORE -> MoreScreen(
                    onOpenPayments = {},
                    onOpenTournamentChooser = {}
                )
            }
        },
    )
}

@Composable
fun MainContent(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    contentFactory: @Composable (MainTab) -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = stringResource(selectedTab.titleRes()),
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Outlined.Settings,
                            tint = BasketKrkColors.AppBarText,
                            contentDescription = "Settings",
                        )
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar(containerColor = BasketKrkColors.DefaultBackground) {
                MainTab.entries.forEach { tab ->
                    val title = stringResource(tab.titleRes())
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(imageVector = tab.icon(), contentDescription = title)
                        },
                        label = {
                            Text(title, textAlign = TextAlign.Center)
                        },
                    )
                }
            }
        },
    ) { padding ->
        Crossfade(targetState = selectedTab, modifier = Modifier.padding(padding)) { tab ->
            contentFactory(tab)
        }
    }
}

@Composable
@Preview
fun MainContentPreview() {
    MainContent(
        selectedTab = MainTab.MATCHES,
        onTabSelected = {},
        contentFactory = { tab ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Fake content for: ${tab.name}")
            }
        },
    )
}
