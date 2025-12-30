package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TopRowCell(
    text: String,
    width: Dp,
    height: Dp,
    roundedEnd: Boolean,
    onClick: () -> Unit
) {
    val shape = if (roundedEnd) {
        RoundedCornerShape(topEnd = 10.dp)
    } else {
        RoundedCornerShape(0.dp)
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(BasketKrkColors.Main)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = BasketKrkStyles.fixedRowText, maxLines = 1)
    }
}

@Composable
@Preview
fun HeaderCellPreview() {
    TopRowCell(
        text = "Points",
        width = 80.dp,
        height = 40.dp,
        roundedEnd = true,
        onClick = {}
    )
}