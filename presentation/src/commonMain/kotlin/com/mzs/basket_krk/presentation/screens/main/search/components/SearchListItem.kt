package com.mzs.basket_krk.presentation.screens.main.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.datautils.SearchFakeData
import com.mzs.basket_krk.domain.model.SearchItem
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ITEM_HEIGHT = 60.dp
private val ICON_SIZE = 40.dp
private val ITEM_RADIUS = 12.dp

@Composable
fun SearchListItem(
    searchItem: SearchItem,
    onPlayerClick: (playerId: Int) -> Unit,
    onTeamClick: (playerId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(ITEM_HEIGHT)
            .clip(RoundedCornerShape(ITEM_RADIUS))
            .clickable {
                when (searchItem) {
                    is SearchItem.Player -> onPlayerClick(searchItem.id)
                    is SearchItem.Team -> onTeamClick(searchItem.id)
                }
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(ITEM_RADIUS),
        border = BorderStroke(1.dp, BasketKrkColors.BorderRoundedItem),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when (searchItem) {
                is SearchItem.Team -> {
                    searchItem.logoPath?.let {
                        Spacer(modifier = Modifier.width(16.dp))
                        BasketKrkImage(
                            logoUrl = it,
                            contentDescription = "${searchItem.name} logo",
                            modifier = Modifier.size(ICON_SIZE)
                        )
                    }
                }

                else -> {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Player icon",
                        tint = BasketKrkColors.EditTextIcon,
                        modifier = Modifier.size(ICON_SIZE)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                val text = when (searchItem) {
                    is SearchItem.Player -> "${searchItem.lastName} ${searchItem.firstName}"
                    is SearchItem.Team -> searchItem.name
                }

                Text(
                    text = text,
                    style = BasketKrkStyles.listItemMainTextBold,
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = BasketKrkColors.Main,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
@Preview
fun SearchListItemPlayerPreview() {
    SearchListItem(
        searchItem = SearchFakeData.searchItemPlayer(),
        onPlayerClick = {},
        onTeamClick = {}
    )
}

@Composable
@Preview
fun SearchListItemTeamPreview() {
    SearchListItem(
        searchItem = SearchFakeData.searchItemTeam(logo = "dsa"),
        onPlayerClick = {},
        onTeamClick = {}
    )
}