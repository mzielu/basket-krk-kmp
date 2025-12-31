package com.mzs.basket_krk.presentation.base

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors

fun Modifier.drawTopBottomBorder(
    strokeWidth: Dp = 0.5.dp,
    borderColor: Color = BasketKrkColors.MainLight
): Modifier = this.drawBehind {
    // top border
    drawLine(
        color = borderColor,
        start = Offset(0f, 0f),
        end = Offset(size.width, 0f),
        strokeWidth = strokeWidth.toPx()
    )

    // bottom border
    drawLine(
        color = borderColor,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = strokeWidth.toPx()
    )
}