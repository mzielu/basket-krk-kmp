package com.mzs.basket_krk.presentation.screens.main.matches.components

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType

@Composable
fun MatchListItem(
    match: Match,
    onClick: (matchId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = resolveItemBg(match)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(match.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, Color.Green),
    ) {
        Column {
            TopItemView(match = match)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: teams
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    MatchItemTeamRow(
                        matchTeam = match.team1,
                        matchStatus = match.status,
                        highlight = match.status == MatchStatus.IN_PROGRESS || (match.team1.points > match.team2.points),
                    )
                    MatchItemTeamRow(
                        matchTeam = match.team2,
                        matchStatus = match.status,
                        highlight = match.status == MatchStatus.IN_PROGRESS || (match.team2.points > match.team1.points),
                    )
                }

                // Right arrow
                Icon(
                    imageVector = Icons.Default.KeyboardDoubleArrowRight,
                    contentDescription = null,
                    tint = Color.Cyan,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
                )
            }

            HorizontalDivider(
                color = Color.DarkGray,
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            // Bottom row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = match.date.toString(),
                    )
                }

                match.arena?.let { arena ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stadium, // if not available, use another icon
                            contentDescription = null,
                            tint = Color.Magenta,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = arena,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopItemView(match: Match) {
    when (match.type) {
        MatchType.REGULAR_SEASON -> {
            Surface(color = Color.Green) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "dasdas",
                    )
                    match.league?.let { league ->
                        Text(
                            text = " - ${league.name} dsadsadas",
                        )
                    }
                }
            }
        }

        MatchType.PLAYOFFS -> {
            val league = match.league
            val finalText = when {
                !match.description.isNullOrBlank() -> match.description
                league != null -> league.name
                else -> ""
            }.orEmpty()

            Surface(color = Color.Magenta) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "pdsapdas",
                    )
                    if (finalText.isNotEmpty()) {
                        Text(
                            text = " - $finalText",
                        )
                    }
                }
            }
        }
    }
}

private fun resolveItemBg(match: Match): Color =
    when (match.type) {
        MatchType.REGULAR_SEASON -> Color.Transparent
        MatchType.PLAYOFFS -> Color.Cyan
    }
