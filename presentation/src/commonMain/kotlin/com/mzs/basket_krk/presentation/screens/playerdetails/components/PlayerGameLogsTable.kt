package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.table_corner_result
import com.mzs.basket_krk.domain.model.PlayerLog
import com.mzs.basket_krk.domain.model.Stat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.StatSeconds
import com.mzs.basket_krk.domain.model.getValueForGivenOption
import com.mzs.basket_krk.domain.model.toReadableStatOptionText
import com.mzs.basket_krk.presentation.base.drawTopBottomBorder
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.StatCell
import com.mzs.basket_krk.presentation.base.ui.TopRowCornerCell
import com.mzs.basket_krk.presentation.screens.matchdetails.components.StatCellMapper
import org.jetbrains.compose.resources.stringResource

private val gameLogLeftColWidth = 100.dp
private val gameLogHeaderHeight = 40.dp
private val gameLogRowHeight = 65.dp
private val gameLogCellWidth = 35.dp

@Composable
fun PlayerGameLogsTable(
    playerLogs: List<PlayerLog>,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onMatchPress: (Int) -> Unit,
    onSortByStat: (StatOption) -> Unit,
) {
    val statOptions = remember(playerLogs) {
        if (playerLogs.isNotEmpty()) StatCellMapper.getStatOptionsFromModel(playerLogs.first().stat) else emptyList()
    }

    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: Scrollable body (stat cells)
        Row(
            modifier = Modifier
                .padding(start = gameLogLeftColWidth, top = gameLogHeaderHeight)
                .fillMaxSize()
                .horizontalScroll(hScroll)
                .verticalScroll(vScroll)
        ) {
            Column {
                playerLogs.forEach { playerLog ->
                    StatLine(stat = playerLog.stat, statOptions = statOptions)
                }
            }
        }

        // Layer 2: Pinned top row (stat headers)
        Row(
            modifier = Modifier
                .padding(start = gameLogLeftColWidth)
                .height(gameLogHeaderHeight)
                .fillMaxWidth()
                .horizontalScroll(hScroll)
        ) {
            statOptions.forEachIndexed { index, statOption ->
                val isLast = index == statOptions.lastIndex
                SortableTopRowCell(
                    text = statOption.sign,
                    width = gameLogCellWidth,
                    height = gameLogHeaderHeight,
                    roundedEnd = isLast,
                    isSortActive = sortOption == statOption,
                    sortAscending = sortAscending,
                    onClick = { onSortByStat(statOption) }
                )
            }
        }

        // Layer 3: Pinned left column (game log info)
        Column(
            modifier = Modifier
                .width(gameLogLeftColWidth)
                .padding(top = gameLogHeaderHeight)
                .fillMaxHeight()
                .verticalScroll(vScroll)
        ) {
            playerLogs.forEach { playerLog ->
                GameLogLeftColumnCell(
                    playerLog = playerLog,
                    height = gameLogRowHeight,
                    onClick = { onMatchPress(playerLog.id) }
                )
            }
        }

        // Layer 4: Top-left corner
        TopRowCornerCell(
            text = stringResource(Res.string.table_corner_result),
            modifier = Modifier
                .width(gameLogLeftColWidth)
                .height(gameLogHeaderHeight)
        )
    }
}

@Composable
private fun SortableTopRowCell(
    text: String,
    width: Dp,
    height: Dp,
    roundedEnd: Boolean,
    isSortActive: Boolean,
    sortAscending: Boolean,
    onClick: () -> Unit,
) {
    val shape = if (roundedEnd) {
        RoundedCornerShape(topEnd = 10.dp)
    } else {
        RoundedCornerShape(0.dp)
    }

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(BasketKrkColors.Main)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, style = BasketKrkStyles.fixedRowText, maxLines = 1)
            if (isSortActive) {
                Icon(
                    imageVector = if (sortAscending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun GameLogLeftColumnCell(
    playerLog: PlayerLog,
    height: Dp,
    onClick: () -> Unit,
) {
    val winLossColor = when {
        playerLog.pts > playerLog.opponent.points -> BasketKrkColors.MatchWin
        playerLog.pts < playerLog.opponent.points -> BasketKrkColors.MatchLost
        else -> BasketKrkColors.MatchInProgress
    }
    val winLossText = when {
        playerLog.pts > playerLog.opponent.points -> "W"
        playerLog.pts < playerLog.opponent.points -> "L"
        else -> "IP"
    }

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .background(BasketKrkColors.DefaultBackground)
            .drawTopBottomBorder()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(playerLog.date, style = BasketKrkStyles.gameLogsDate)
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(winLossColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(winLossText, style = BasketKrkStyles.gameLogsSignResult)
                }
                Spacer(Modifier.width(4.dp))
                Text("${playerLog.pts}-${playerLog.opponent.points}", style = BasketKrkStyles.gameLogsResult)
            }
            Spacer(Modifier.height(2.dp))
            Text("vs ${playerLog.opponent.shortName}", style = BasketKrkStyles.gameLogsVsTeam)
        }
    }
}

@Composable
private fun StatLine(stat: Stat, statOptions: List<StatOption>) {
    Row {
        statOptions.forEachIndexed { colIndex, statOption ->
            val value = stat.getValueForGivenOption(statOption, StatDisplayType.SUM) ?: 0.0
            val text = value.toReadableStatOptionText(statOption)
            StatCell(
                text = text,
                isSeconds = (statOption == StatSeconds),
                bold = false,
                width = gameLogCellWidth,
                height = gameLogRowHeight,
                altBackground = ((colIndex + 1) % 2 != 0)
            )
        }
    }
}
