package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun BasketKrkImage(
    logoUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    return AsyncImage(
        model = ImageRequest
            .Builder(LocalPlatformContext.current)
            .data("https://www.basketkrk.pl/$logoUrl")
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
    )
}