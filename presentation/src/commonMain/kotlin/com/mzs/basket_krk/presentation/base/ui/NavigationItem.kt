package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ITEM_HEIGHT = 60.dp
private val ITEM_RADIUS = 12.dp

@Composable
fun NavigationItem(
    title: String,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(ITEM_RADIUS)

    Box(
        modifier = modifier
            .clip(shape)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = BasketKrkColors.BorderRoundedItem,
                shape = shape
            )
            .height(ITEM_HEIGHT)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = imageVector,
                contentDescription = imageVector.name,
                tint = BasketKrkColors.TextHighlighted,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                style = BasketKrkStyles.moreItem,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = BasketKrkColors.Main,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
@Preview
fun NavigationItemPreview() {
    NavigationItem(
        title = "Sample Item",
        onClick = {},
        imageVector = Icons.Outlined.Preview,
        modifier = Modifier.background(BasketKrkColors.DefaultBackground)
    )
}