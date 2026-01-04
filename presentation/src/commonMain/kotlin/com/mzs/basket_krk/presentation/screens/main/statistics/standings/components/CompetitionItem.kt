package com.mzs.basket_krk.presentation.screens.main.statistics.standings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.datautils.LeagueFakeData
import com.mzs.basket_krk.domain.model.Competition
import com.mzs.basket_krk.domain.model.CompetitionStanding
import com.mzs.basket_krk.presentation.base.ui.AutoSizeText
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkImage
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import org.jetbrains.compose.ui.tooling.preview.Preview

private val ITEM_HEIGHT = 40.dp
private val ITEM_HEIGHT_2 = 50.dp
private val ITEM_RADIUS = 12.dp

@Composable
fun CompetitionItem(
    competition: Competition,
    onOpenTeamDetails: (teamId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = competition.name,
            style = BasketKrkStyles.competitionTitle,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(Modifier.height(8.dp))

        CompetitionHeaderRow()

        Spacer(Modifier.height(4.dp))

        Column {
            competition.standings.forEach { standing ->
                StandingItem(
                    competitionStanding = standing,
                    onClick = { onOpenTeamDetails(standing.team.id) }
                )
            }
        }
    }
}

@Composable
private fun CompetitionHeaderRow() {
    val shape = RoundedCornerShape(ITEM_RADIUS)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(BasketKrkColors.Main)
            .height(ITEM_HEIGHT),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(50.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("Pos", style = BasketKrkStyles.competitionTitleRow)
        }

        Spacer(Modifier.width(30.dp))

        Box(
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text("Team", style = BasketKrkStyles.competitionTitleRow)
        }

        Box(
            modifier = Modifier.width(60.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("W-L", style = BasketKrkStyles.competitionTitleRow)
        }

        Box(
            modifier = Modifier.width(60.dp).fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text("+/-", style = BasketKrkStyles.competitionTitleRow)
        }
    }
}

@Composable
private fun StandingItem(
    competitionStanding: CompetitionStanding,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(ITEM_RADIUS)

    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, BasketKrkColors.BorderRoundedItem, shape)
            .height(ITEM_HEIGHT_2)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Position
            Box(
                modifier = Modifier.width(50.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = competitionStanding.pos.toString(),
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            competitionStanding.team.logoPath?.let {
                BasketKrkImage(
                    logoUrl = it,
                    contentDescription = "${competitionStanding.team.name} logo",
                    modifier = Modifier.size(30.dp)
                )
            }

            // Team name
            Box(
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = competitionStanding.team.name,
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            // W-L
            Box(
                modifier = Modifier.width(60.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${competitionStanding.w}-${competitionStanding.l}",
                    style = BasketKrkStyles.competitionItemRow
                )
            }

            Column(
                modifier = Modifier.width(60.dp).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val pointDifference = competitionStanding.p - competitionStanding.m
                val text = if (pointDifference >= 0) {
                    "+$pointDifference"
                } else {
                    "$pointDifference"
                }

                Text(
                    text = text,
                    style = BasketKrkStyles.competitionItemRow
                )

                AutoSizeText(
                    text = "${competitionStanding.p}/${competitionStanding.m}",
                    style = BasketKrkStyles.competitionItemRowLight
                )
            }
        }
    }
}

@Composable
@Preview
fun CompetitionItemPreview() {
    CompetitionItem(
        competition = LeagueFakeData.competition(),
        onOpenTeamDetails = {}
    )
}