package com.mzs.basket_krk.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.navigation.Screen
import com.mzs.basket_krk.presentation.screens.main.MainScreen
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.AllTimeLeadersScreen
import com.mzs.basket_krk.presentation.screens.main.statistics.alltimeleaders.AllTimeLeadersViewModel
import com.mzs.basket_krk.presentation.screens.main.statistics.standings.StandingsScreen
import com.mzs.basket_krk.presentation.screens.main.statistics.standings.StandingsViewModel
import com.mzs.basket_krk.presentation.screens.matchdetails.MatchDetailsScreen
import com.mzs.basket_krk.presentation.screens.matchdetails.MatchDetailsViewModel
import com.mzs.basket_krk.presentation.screens.playerdetails.PlayerDetailsScreen
import com.mzs.basket_krk.presentation.screens.playerdetails.PlayerDetailsViewModel
import com.mzs.basket_krk.presentation.screens.teamdetails.TeamDetailsScreen
import com.mzs.basket_krk.presentation.screens.teamdetails.TeamDetailsViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@Composable
fun App() {
    MaterialTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(BasketKrkColors.Main),
            contentAlignment = Alignment.Center,
        ) {
            val navController: NavHostController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Main,
            ) {
                // Onboarding Graph
                composable<Screen.Main> {
                    MainScreen(
                        openMatchDetails = {
                            navController.navigate(Screen.MatchDetails(matchId = it))
                        },
                        openPlayerDetails = {
                            navController.navigate(Screen.PlayerDetails(playerId = it))
                        },
                        openTeamDetails = {
                            navController.navigate(Screen.TeamDetails(teamId = it))
                        },
                        openAllTimeLeaders = {
                            navController.navigate(Screen.AllTimeLeaders)
                        },
                        openTables = {
                            navController.navigate(Screen.Standings)
                        }
                    )
                }

                composable<Screen.MatchDetails> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.MatchDetails>()
                    val viewModel: MatchDetailsViewModel = koinInject(
                        parameters = { parametersOf(args.matchId) }
                    )

                    MatchDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable<Screen.PlayerDetails> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.PlayerDetails>()
                    val viewModel: PlayerDetailsViewModel = koinInject(
                        parameters = { parametersOf(args.playerId) }
                    )
                    PlayerDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToMatch = { navController.navigate(Screen.MatchDetails(matchId = it)) },
                        onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) },
                    )
                }

                composable<Screen.TeamDetails> { backStackEntry ->
                    val args = backStackEntry.toRoute<Screen.TeamDetails>()
                    val viewModel: TeamDetailsViewModel = koinInject(
                        parameters = { parametersOf(args.teamId) }
                    )
                    TeamDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) },
                        onNavigateToMatch = { navController.navigate(Screen.MatchDetails(matchId = it)) },
                    )
                }

                composable<Screen.AllTimeLeaders> {
                    AllTimeLeadersScreen(
                        viewModel = koinViewModel<AllTimeLeadersViewModel>(),
                        openPlayerDetails = {
                            navController.navigate(Screen.PlayerDetails(playerId = it))
                        },
                        onNavigateBack = { navController.popBackStack() },
                    )
                }

                composable<Screen.Standings> {
                    StandingsScreen(
                        viewModel = koinViewModel<StandingsViewModel>(),
                        onNavigateBack = { navController.popBackStack() },
                    )
                }
            }
        }
    }
}