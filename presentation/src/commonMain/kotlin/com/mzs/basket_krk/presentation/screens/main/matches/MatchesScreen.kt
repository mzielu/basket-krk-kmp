package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesScreen(viewModel: MatchesViewModel = koinViewModel()) {
    val viewState by viewModel.viewState.collectAsState()

    MatchesContent(viewState, onRefresh = viewModel::onRefresh)
}

@Composable
fun MatchesContent(
    viewState: MatchesViewState,
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
                    LazyColumn {
                        items(viewState.rounds, key = { it.id }) { round ->
                            Text(text = "Round: ${round.name}")
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
        MatchesContent(
            viewState = MatchesViewState(),
            onRefresh = {},
        )
}