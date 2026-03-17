package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.TeamResult
import com.mzs.basket_krk.domain.model.TeamResultList
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField

@Composable
fun TeamResultsTab(
    resultList: TeamResultList,
    seasons: List<Season>,
    selectedSeason: Season?,
    onSeasonSelected: (Season) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        DropdownFormField(
            label = "Season",
            options = seasons,
            selectedOption = selectedSeason,
            onOptionSelected = onSeasonSelected,
            readableValue = { it?.num?.toString() ?: "" },
            modifier = Modifier.width(100.dp)
        )
        Spacer(Modifier.height(8.dp))
        if (resultList.data.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No results available", style = BasketKrkStyles.itemAdditionalInfo)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(resultList.data.size) { index ->
                    TeamResultItem(
                        result = resultList.data[index],
                        onClick = { onMatchPress(resultList.data[index].id) }
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamResultItem(
    result: TeamResult,
    onClick: () -> Unit,
) {
    val (signColor, signText) = resolveMatchSign(result)
    val bgColor = if (result.type == MatchType.PLAYOFFS) BasketKrkColors.PlayoffsBg else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(1.dp, BasketKrkColors.BorderRoundedItem, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(60.dp)
        ) {
            Spacer(Modifier.width(8.dp))
            Text("vs", style = BasketKrkStyles.itemName)
            Spacer(Modifier.width(8.dp))
            BasketKrkImage(
                logoUrl = result.opponent.logoUrl,
                contentDescription = "${result.opponent.name} logo",
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = result.opponent.name,
                style = BasketKrkStyles.itemName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // Right section: divider + W/L badge + score/date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(BasketKrkColors.DefaultDivider)
                )
                Spacer(Modifier.width(12.dp))
                // W/L badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(signColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(signText, style = BasketKrkStyles.fixedRowText)
                }
                Spacer(Modifier.width(4.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(
                        "${result.points}-${result.opponent.points}",
                        style = BasketKrkStyles.itemName
                    )
                    Text(
                        result.date,
                        style = BasketKrkStyles.itemAdditionalInfo
                    )
                }
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

private fun resolveMatchSign(result: TeamResult): Pair<Color, String> = when (result.status) {
    MatchStatus.IN_PROGRESS -> BasketKrkColors.MatchInProgress to "IP"
    MatchStatus.NON_STARTED -> BasketKrkColors.MatchNotStarted to "?"
    else -> if (result.points > result.opponent.points)
        BasketKrkColors.MatchWin to "W"
    else
        BasketKrkColors.MatchLost to "L"
}
