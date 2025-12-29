package com.mzs.basket_krk.presentation.screens.matchdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import com.mzs.basket_krk.presentation.base.ui.ErrorView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchDetailsScreen(
    viewModel: MatchDetailsViewModel,
    onNavigateBack: () -> Unit,
) {

    val viewState by viewModel.viewState.collectAsState()

    MatchDetailsContent(
        viewState = viewState,
        onRetry = viewModel::retry,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun MatchDetailsContent(
    viewState: MatchDetailsViewState,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            ActionBar(
                titleText = "Match Details",
                showBackButton = true,
                onBackButtonClick = onNavigateBack,
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {


            if (viewState.matchDetails.isLoading) {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewState.matchDetails.isError) {

                ErrorView(error = viewState.matchDetails.error, retryAction = onRetry)
            } else {
                viewState.matchDetails.data?.let { matchDetails ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Match ID: ${matchDetails.id}")
                        Text(text = "Date: ${matchDetails.date}")

                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MatchDetailsContentPreview() {
    MatchDetailsContent(
        viewState = MatchDetailsViewState(),
        onRetry = {},
        onNavigateBack = {},
    )
}
