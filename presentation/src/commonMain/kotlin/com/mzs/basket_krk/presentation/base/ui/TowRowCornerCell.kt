package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopRowCornerCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 10.dp))
            .background(BasketKrkColors.Main)
            .padding(start = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AutoSizeText(
            text = text,
            style = BasketKrkStyles.fixedRowText
        )
    }
}

@Composable
@Preview
fun TopRowCornerCellPreview() {
    TopRowCornerCell(text = "Player")
}