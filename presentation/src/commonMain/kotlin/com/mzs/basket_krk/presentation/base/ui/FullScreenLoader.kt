package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FullScreenLoader() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BasketKrkColors.DefaultBackground),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = BasketKrkColors.Main)
    }
}

@Composable
@Preview
fun FullScreenLoaderPreview() {
    FullScreenLoader()
}
