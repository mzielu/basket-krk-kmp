package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.Round
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.screens.main.matches.components.MatchListItem
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesScreen(viewModel: MatchesViewModel = koinViewModel()) {
    val viewState by viewModel.viewState.collectAsState()

    MatchesContent(
        viewState,
        onRoundSelected = viewModel::onRoundSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onRefresh = viewModel::onRefresh
    )
}

@Composable
fun MatchesContent(
    viewState: MatchesViewState,
    onRoundSelected: (Round) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                viewState.fullScreenLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewState.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = onRefresh) {
                            Text("Try Again")
                        }
                    }
                }

                else -> {
                    Column {

                        Row(modifier = Modifier.padding(4.dp)) {
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


                        LazyColumn {
                            items(viewState.matches.size) { index ->
                                val match = viewState.matches[index]
                                MatchListItem(match = match, onClick = {})
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
            onSeasonSelected = {}
        )
    }
}