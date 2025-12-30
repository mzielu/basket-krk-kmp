package com.mzs.basket_krk.presentation.screens.matchdetails.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.table_corner_player
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.Stat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.StatSeconds
import com.mzs.basket_krk.domain.model.getValueForGivenOption
import com.mzs.basket_krk.domain.model.toReadableStatOptionText
import com.mzs.basket_krk.presentation.base.ui.LeftColumnPlayerCell
import com.mzs.basket_krk.presentation.base.ui.StatCell
import com.mzs.basket_krk.presentation.base.ui.SumAvgCornerCell
import com.mzs.basket_krk.presentation.base.ui.TopRowCell
import com.mzs.basket_krk.presentation.base.ui.TopRowCornerCell
import org.jetbrains.compose.resources.stringResource

val leftColWidth = 120.dp
val headerHeight = 40.dp
val rowHeight = 45.dp
val cellWidth = 35.dp

@Composable
fun MatchDetailsTeamTable(
    playersWithStats: List<PlayerWithStat>,
    statDisplayType: StatDisplayType,
    onPlayerPress: (Int) -> Unit,
    onStatOptionPress: (StatOption) -> Unit
) {

    val statOptions = remember(playersWithStats) {
        if (playersWithStats.isNotEmpty()) StatCellMapper.getStatOptionsFromModel(playersWithStats.first().stat) else emptyList()
    }

    val generalStat = remember(playersWithStats) {
        if (playersWithStats.isNotEmpty()) StatCellMapper.getSumStatFromStats(playersWithStats.map { it.stat }) else null
    }

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .padding(start = leftColWidth, top = headerHeight)
                .fillMaxSize()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
        ) {
            Column {
                // rows for players
                playersWithStats.forEachIndexed { _, pws ->
                    StatLine(pws.stat, statOptions, statDisplayType)
                }

                //general stat row
                generalStat?.let { StatLine(it, statOptions, statDisplayType) }
            }
        }

        // --- PINNED TOP ROW (header cells for stat options) ---
        Row(
            modifier = Modifier
                .padding(start = leftColWidth)
                .height(headerHeight)
                .fillMaxWidth()
                .horizontalScroll(hScroll)
        ) {
            statOptions.forEachIndexed { index, statOption ->
                val isLast = index == statOptions.lastIndex
                TopRowCell(
                    text = statOption.sign,
                    width = cellWidth,
                    height = headerHeight,
                    roundedEnd = isLast,
                    onClick = { onStatOptionPress(statOption) }
                )
            }
        }

        // --- PINNED LEFT COLUMN (player names + bottom-left corner cell) ---
        Column(
            modifier = Modifier
                .width(leftColWidth)
                .padding(top = headerHeight)
                .fillMaxHeight()
                .verticalScroll(vScroll)
        ) {
            playersWithStats.forEach { pws ->
                LeftColumnPlayerCell(
                    playerWithStat = pws,
                    height = rowHeight,
                    onClick = { onPlayerPress(pws.player.id) }
                )
            }

            // bottom-left corner cell (TOT/AVG)
            SumAvgCornerCell(
                statDisplayType = statDisplayType,
                height = rowHeight
            )
        }

        // --- TOP-LEFT CORNER (PLAYER) ---ż
        TopRowCornerCell(
            text = stringResource(Res.string.table_corner_player),
            modifier = Modifier
                .width(leftColWidth)
                .height(headerHeight)
        )
    }
}

@Composable
private fun StatLine(
    stat: Stat,
    statOptions: List<StatOption>,
    statDisplayType: StatDisplayType,
    bold: Boolean = false
) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = stat.getValueForGivenOption(statOption, statDisplayType) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)

            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = bold,
                width = cellWidth,
                height = rowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}