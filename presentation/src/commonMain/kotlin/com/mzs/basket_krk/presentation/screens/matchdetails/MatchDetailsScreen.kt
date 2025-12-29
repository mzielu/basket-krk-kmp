package com.mzs.basket_krk.presentation.screens.matchdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mzs.basket_krk.presentation.base.ui.ActionBar
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchDetailsScreen(
    viewModel: MatchDetailsViewModel,
    onNavigateBack: () -> Unit,
) {

    val viewState by viewModel.viewState.collectAsState()

    MatchDetailsContent(
        viewState = viewState,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun MatchDetailsContent(
    viewState: MatchDetailsViewState,
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
    ) { padding ->
        Box {
            Text("Match Details Content", modifier = Modifier.padding(padding))
        }
    }
}

@Composable
@Preview
fun MatchDetailsContentPreview() {
    MatchDetailsContent(
        viewState = MatchDetailsViewState(),
        onNavigateBack = {},
    )
}
