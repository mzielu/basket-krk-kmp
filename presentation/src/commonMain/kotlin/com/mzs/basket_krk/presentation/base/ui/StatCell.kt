package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.presentation.base.drawTopBottomBorder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatCell(
    text: String,
    isSeconds: Boolean,
    width: Dp,
    height: Dp,
    altBackground: Boolean,
    bold: Boolean = false
) {
    val bg =
        if (altBackground) BasketKrkColors.DefaultAreaHighlighter else BasketKrkColors.DefaultBackground
    val style = when {
        bold && isSeconds -> BasketKrkStyles.numericTableTextSmallBold
        bold && !isSeconds -> BasketKrkStyles.numericTableTextBold
        !bold && isSeconds -> BasketKrkStyles.numericTableTextSmall
        else -> BasketKrkStyles.numericTableText
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(bg)
            .drawTopBottomBorder(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = style,
            maxLines = 1
        )
    }
}

@Composable
@Preview
fun StatCellPreview() {
    StatCell(
        text = "12.23",
        isSeconds = false,
        width = 50.dp,
        height = 40.dp,
        altBackground = false,
        bold = true
    )
}