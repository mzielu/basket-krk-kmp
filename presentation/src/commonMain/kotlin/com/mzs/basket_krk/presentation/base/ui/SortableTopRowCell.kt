package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SortableTopRowCell(
    text: String,
    width: Dp,
    height: Dp,
    roundedEnd: Boolean,
    isSortActive: Boolean,
    sortAscending: Boolean,
    onClick: () -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, style = BasketKrkStyles.fixedRowText, maxLines = 1)
            if (isSortActive) {
                Icon(
                    imageVector = if (sortAscending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
