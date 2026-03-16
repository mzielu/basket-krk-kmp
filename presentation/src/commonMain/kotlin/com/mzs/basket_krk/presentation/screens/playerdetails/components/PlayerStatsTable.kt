package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.model.Stat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatLeague
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.StatSeconds
import com.mzs.basket_krk.domain.model.StatSeason
import com.mzs.basket_krk.domain.model.StatTeam
import com.mzs.basket_krk.domain.model.getValueForGivenOption
import com.mzs.basket_krk.domain.model.getValueForGivenOptionWithSeasonsCount
import com.mzs.basket_krk.domain.model.toReadableStatOptionText
import com.mzs.basket_krk.presentation.base.drawTopBottomBorder
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.StatCell
import com.mzs.basket_krk.presentation.base.ui.TopRowCell
import com.mzs.basket_krk.presentation.screens.matchdetails.components.StatCellMapper

private val statSeasonColWidth = 35.dp
private val statLeagueColWidth = 40.dp
private val statTeamColWidth = 50.dp
private val statsFixedColsTotalWidth = statSeasonColWidth + statLeagueColWidth + statTeamColWidth // 125.dp
private val statsHeaderHeight = 40.dp
private val statsRowHeight = 45.dp
private val statsCellWidth = 35.dp

@Composable
fun PlayerStatsTable(
    playersStats: List<PlayerStat>,
    statDisplayType: StatDisplayType,
    onTeamPress: (Int) -> Unit,
) {
    val scrollableStatOptions = remember(playersStats) {
        if (playersStats.isNotEmpty()) StatCellMapper.getStatOptionsFromModel(playersStats.first().stat) else emptyList()
    }

    val generalStat = remember(playersStats) {
        if (playersStats.isNotEmpty()) StatCellMapper.getSumStatFromStats(playersStats.map { it.stat }) else null
    }

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Scrollable body
        Row(
            modifier = Modifier
                .padding(start = statsFixedColsTotalWidth, top = statsHeaderHeight)
                .fillMaxSize()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
        ) {
            Column {
                playersStats.forEach { playerStat ->
                    StatsStatLine(stat = playerStat.stat, statOptions = scrollableStatOptions, statDisplayType = statDisplayType)
                }
                generalStat?.let { TotalsStatLine(it, scrollableStatOptions, statDisplayType, playersStats.size) }
            }
        }

        // Layer 2: Pinned top row
        Row(
            modifier = Modifier
                .padding(start = statsFixedColsTotalWidth)
                .height(statsHeaderHeight)
                .fillMaxWidth()
                .horizontalScroll(hScroll)
        ) {
            scrollableStatOptions.forEachIndexed { index, statOption ->
                val isLast = index == scrollableStatOptions.lastIndex
                TopRowCell(
                    text = statOption.sign,
                    width = statsCellWidth,
                    height = statsHeaderHeight,
                    roundedEnd = isLast,
                    onClick = {}
                )
            }
        }

        // Layer 3: Pinned left columns
        Row(
            modifier = Modifier
                .width(statsFixedColsTotalWidth)
                .padding(top = statsHeaderHeight)
                .fillMaxHeight()
                .verticalScroll(vScroll)
        ) {
            // Season column
            Column(modifier = Modifier.width(statSeasonColWidth)) {
                playersStats.forEach { playerStat ->
                    FixedLeftCell(
                        text = playerStat.season.toString(),
                        width = statSeasonColWidth,
                        height = statsRowHeight,
                        clickable = false,
                        onClick = {}
                    )
                }
                // Totals row — empty
                generalStat?.let {
                    FixedLeftCell(text = "", width = statSeasonColWidth, height = statsRowHeight, clickable = false, onClick = {})
                }
            }

            // League column
            Column(modifier = Modifier.width(statLeagueColWidth)) {
                playersStats.forEach { playerStat ->
                    FixedLeftCell(
                        text = playerStat.league.name,
                        width = statLeagueColWidth,
                        height = statsRowHeight,
                        clickable = false,
                        onClick = {}
                    )
                }
                // Totals row — empty
                generalStat?.let {
                    FixedLeftCell(text = "", width = statLeagueColWidth, height = statsRowHeight, clickable = false, onClick = {})
                }
            }

            // Team column
            Column(modifier = Modifier.width(statTeamColWidth)) {
                playersStats.forEach { playerStat ->
                    FixedLeftCell(
                        text = playerStat.team.name,
                        width = statTeamColWidth,
                        height = statsRowHeight,
                        clickable = true,
                        onClick = { onTeamPress(playerStat.team.id) }
                    )
                }
                // Totals row — empty
                generalStat?.let {
                    FixedLeftCell(text = "", width = statTeamColWidth, height = statsRowHeight, clickable = false, onClick = {})
                }
            }
        }

        // Layer 4: Top-left corner
        Row(
            modifier = Modifier
                .width(statsFixedColsTotalWidth)
                .height(statsHeaderHeight)
        ) {
            Box(
                modifier = Modifier
                    .width(statSeasonColWidth)
                    .height(statsHeaderHeight)
                    .clip(RoundedCornerShape(topStart = 10.dp))
                    .background(BasketKrkColors.Main),
                contentAlignment = Alignment.Center
            ) {
                Text(StatSeason.sign, style = BasketKrkStyles.fixedRowText)
            }
            Box(
                modifier = Modifier
                    .width(statLeagueColWidth)
                    .height(statsHeaderHeight)
                    .background(BasketKrkColors.Main),
                contentAlignment = Alignment.Center
            ) {
                Text(StatLeague.sign, style = BasketKrkStyles.fixedRowText)
            }
            Box(
                modifier = Modifier
                    .width(statTeamColWidth)
                    .height(statsHeaderHeight)
                    .background(BasketKrkColors.Main),
                contentAlignment = Alignment.Center
            ) {
                Text(StatTeam.sign, style = BasketKrkStyles.fixedRowText)
            }
        }
    }
}

@Composable
private fun FixedLeftCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    clickable: Boolean,
    onClick: () -> Unit,
) {
    val modifier = Modifier
        .width(width)
        .height(height)
        .background(BasketKrkColors.DefaultBackground)
        .drawTopBottomBorder()
        .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = BasketKrkStyles.fixedColumnText,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatsStatLine(stat: Stat, statOptions: List<StatOption>, statDisplayType: StatDisplayType) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = stat.getValueForGivenOption(statOption, statDisplayType) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)
            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = false,
                width = statsCellWidth,
                height = statsRowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}

@Composable
private fun TotalsStatLine(generalStat: Stat, statOptions: List<StatOption>, statDisplayType: StatDisplayType, seasonCount: Int) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = generalStat.getValueForGivenOptionWithSeasonsCount(statOption, statDisplayType, seasonCount) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)
            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = true,
                width = statsCellWidth,
                height = statsRowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}
