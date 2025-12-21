package com.mzs.basket_krk.presentation.screens.main.matches

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MatchesScreen(viewModel: MatchesViewModel = koinViewModel()) {
    MatchesContent()
}

@Composable
fun MatchesContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Matches screen")
    }
}
