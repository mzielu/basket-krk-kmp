package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.Stat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.StatSeconds
import com.mzs.basket_krk.domain.model.getValueForGivenOption
import com.mzs.basket_krk.domain.model.toReadableStatOptionText
import com.mzs.basket_krk.presentation.base.drawTopBottomBorder
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.LeftColumnPlayerCell
import com.mzs.basket_krk.presentation.base.ui.SortableTopRowCell
import com.mzs.basket_krk.presentation.base.ui.StatCell
import com.mzs.basket_krk.presentation.base.ui.TopRowCornerCell
import com.mzs.basket_krk.presentation.screens.matchdetails.components.StatCellMapper

private val rosterPlayerColWidth = 120.dp
private val rosterHeaderHeight = 40.dp
private val rosterRowHeight = 45.dp
private val rosterStatCellWidth = 35.dp

@Composable
fun TeamRosterTable(
    roster: List<PlayerWithStat>,
    statDisplayType: StatDisplayType,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onPlayerPress: (Int) -> Unit,
    onSortByStat: (StatOption) -> Unit,
) {
    val scrollableStatOptions = remember(roster) {
        if (roster.isNotEmpty()) StatCellMapper.getStatOptionsFromModel(roster.first().stat) else emptyList()
    }

    val totalsStat = remember(roster) {
        if (roster.isNotEmpty()) StatCellMapper.getSumStatFromStats(roster.map { it.stat }) else null
    }

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Scrollable body (stat cells)
        Row(
            modifier = Modifier
                .padding(start = rosterPlayerColWidth, top = rosterHeaderHeight)
                .fillMaxSize()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
        ) {
            Column {
                roster.forEach { playerWithStat ->
                    RosterStatLine(
                        stat = playerWithStat.stat,
                        statOptions = scrollableStatOptions,
                        statDisplayType = statDisplayType
                    )
                }
                // Totals row
                totalsStat?.let {
                    RosterTotalsLine(
                        totalsStat = it,
                        statOptions = scrollableStatOptions,
                        statDisplayType = statDisplayType
                    )
                }
            }
        }

        // Layer 2: Pinned top row (sortable stat headers)
        Row(
            modifier = Modifier
                .padding(start = rosterPlayerColWidth)
                .height(rosterHeaderHeight)
                .fillMaxWidth()
                .horizontalScroll(hScroll)
        ) {
            scrollableStatOptions.forEachIndexed { index, statOption ->
                val isLast = index == scrollableStatOptions.lastIndex
                SortableTopRowCell(
                    text = statOption.sign,
                    width = rosterStatCellWidth,
                    height = rosterHeaderHeight,
                    roundedEnd = isLast,
                    isSortActive = sortOption == statOption,
                    sortAscending = sortAscending,
                    onClick = { onSortByStat(statOption) }
                )
            }
        }

        // Layer 3: Pinned left column (player names)
        Column(
            modifier = Modifier
                .width(rosterPlayerColWidth)
                .padding(top = rosterHeaderHeight)
                .fillMaxHeight()
                .verticalScroll(vScroll)
        ) {
            roster.forEach { playerWithStat ->
                LeftColumnPlayerCell(
                    playerWithStat = playerWithStat,
                    height = rosterRowHeight,
                    onClick = { onPlayerPress(playerWithStat.player.id) }
                )
            }
            // Totals row — "Total" label cell
            totalsStat?.let {
                TotalsLeftCell(height = rosterRowHeight)
            }
        }

        // Layer 4: Top-left corner
        TopRowCornerCell(
            text = "Player",
            modifier = Modifier
                .width(rosterPlayerColWidth)
                .height(rosterHeaderHeight)
        )
    }
}

@Composable
private fun RosterStatLine(
    stat: Stat,
    statOptions: List<StatOption>,
    statDisplayType: StatDisplayType,
) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = stat.getValueForGivenOption(statOption, statDisplayType) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)
            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = false,
                width = rosterStatCellWidth,
                height = rosterRowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}

@Composable
private fun RosterTotalsLine(
    totalsStat: Stat,
    statOptions: List<StatOption>,
    statDisplayType: StatDisplayType,
) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = totalsStat.getValueForGivenOption(statOption, statDisplayType) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)
            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = true,
                width = rosterStatCellWidth,
                height = rosterRowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}

@Composable
private fun TotalsLeftCell(height: Dp) {
    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .background(BasketKrkColors.DefaultBackground)
            .drawTopBottomBorder(),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = "Total",
            style = BasketKrkStyles.fixedColumnText.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
