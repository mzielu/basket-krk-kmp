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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Stadium
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
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.label_playoffs
import basket_krk.presentation.generated.resources.label_reg_season
import com.mzs.basket_krk.datautils.MatchFakeData
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.presentation.base.getMatchDateTime
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MatchListItem(
    match: Match,
    onClick: (matchId: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = when (match.type) {
        MatchType.REGULAR_SEASON -> BasketKrkColors.DefaultBackground
        MatchType.PLAYOFFS -> BasketKrkColors.PlayoffsBg
    }

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(match.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, BasketKrkColors.Main),
    ) {
        Column {
            Surface(color = BasketKrkColors.Main) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val topText = when (match.type) {
                        MatchType.REGULAR_SEASON -> {
                            match.league?.let {
                                "${stringResource(Res.string.label_reg_season)} - ${it.name}"
                            } ?: stringResource(Res.string.label_reg_season)
                        }

                        MatchType.PLAYOFFS -> {
                            match.description ?: match.league?.let {
                                "${stringResource(Res.string.label_playoffs)} - ${it.name}"
                            } ?: stringResource(Res.string.label_playoffs)
                        }
                    }

                    Text(
                        text = topText,
                        style = BasketKrkStyles.listItemTopText,
                        modifier = Modifier.padding(2.dp)
                    )

                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
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
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BasketKrkColors.Main,
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp)
                )
            }

            HorizontalDivider(
                color = BasketKrkColors.DefaultDivider,
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
                        tint = BasketKrkColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = getMatchDateTime(
                            status = match.status,
                            date = match.date,
                            time = match.time
                        ),
                        style = BasketKrkStyles.listItemSecondaryText
                    )
                }

                match.arena?.let { arena ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Stadium,
                            contentDescription = null,
                            tint = BasketKrkColors.TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = arena,
                            style = BasketKrkStyles.listItemSecondaryText
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun MatchListItemRegularSeasonPreview() {
    MatchListItem(match = MatchFakeData.match(), onClick = {})
}

@Composable
@Preview
fun MatchListItemPlayoffsPreview() {
    MatchListItem(match = MatchFakeData.match(type = MatchType.PLAYOFFS), onClick = {})
}