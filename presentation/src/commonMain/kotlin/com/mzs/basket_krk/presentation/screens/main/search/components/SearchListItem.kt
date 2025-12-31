package com.mzs.basket_krk.presentation.screens.main.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SearchListItem(
    onPlayerClick: (playerId: Int) -> Unit,
    onTeamClick: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                //TODO add click action
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BasketKrkColors.Main),
        border = BorderStroke(1.dp, BasketKrkColors.Main),
    ) {
        Text(
            text = "sample text",
            style = BasketKrkStyles.listItemTopText,
            modifier = Modifier.padding(2.dp)
        )
    }
}

@Composable
@Preview
fun SearchListItemPreview() {
    SearchListItem(
        onPlayerClick = {},
        onTeamClick = {}
    )
}