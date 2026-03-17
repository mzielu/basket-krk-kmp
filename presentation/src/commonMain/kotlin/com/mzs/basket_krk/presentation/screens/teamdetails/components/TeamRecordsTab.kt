package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamRecordRange
import com.mzs.basket_krk.domain.model.TeamRecordStatOption
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField

@Composable
fun TeamRecordsTab(
    records: List<TeamRecord>,
    selectedStatOption: TeamRecordStatOption,
    selectedRange: TeamRecordRange,
    onFilterChanged: (TeamRecordStatOption, TeamRecordRange) -> Unit,
    onPlayerPress: (Int) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        // Two dropdowns in a row
        Row(modifier = Modifier.fillMaxWidth()) {
            DropdownFormField(
                label = "Range",
                options = TeamRecordRange.entries.toList(),
                selectedOption = selectedRange,
                onOptionSelected = { onFilterChanged(selectedStatOption, it) },
                readableValue = { it?.displayName ?: "" },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            DropdownFormField(
                label = "Category",
                options = TeamRecordStatOption.entries.toList(),
                selectedOption = selectedStatOption,
                onOptionSelected = { onFilterChanged(it, selectedRange) },
                readableValue = { it?.displayName ?: "" },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        if (records.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No records available", style = BasketKrkStyles.itemAdditionalInfo)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(records.size) { index ->
                    TeamRecordItem(
                        record = records[index],
                        onClick = {
                            val record = records[index]
                            val matchId = record.matchId
                            if (matchId != null) onMatchPress(matchId)
                            else onPlayerPress(record.player.id)
                        }
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamRecordItem(
    record: TeamRecord,
    onClick: () -> Unit,
) {
    val playerName = buildString {
        append(record.player.name)
        record.sNum?.let { append(" (S$it)") }
    }
    val suffix = buildRecordSuffix(record)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BasketKrkColors.BorderRoundedItem, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Position
            Text(
                text = "${record.position}.",
                style = BasketKrkStyles.itemName,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.Center
            )
            // Player name with optional season suffix
            Text(
                text = playerName,
                style = BasketKrkStyles.itemName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // Value
            Text(
                text = "${record.value}",
                style = BasketKrkStyles.itemName,
                modifier = Modifier.width(35.dp),
                textAlign = TextAlign.Center
            )
            // Suffix
            if (suffix.isNotEmpty()) {
                Text(
                    text = suffix,
                    style = BasketKrkStyles.itemAdditionalInfo,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End
                )
            }
            // Chevron if navigable to match
            if (record.matchId != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BasketKrkColors.TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    val rounded = (value * 10).toInt() / 10.0
    return rounded.toString().let {
        if ('.' !in it) "$it.0" else it
    }
}

private fun buildRecordSuffix(record: TeamRecord): String {
    val ats = record.ats
    return when {
        ats != null && ats > 0 -> "(${formatOneDecimal(record.value.toDouble() / ats * 100)}%)"
        record.games > 0 -> "(${formatOneDecimal(record.value.toDouble() / record.games)} PG)"
        record.matchId == null -> "${record.games}M"
        else -> ""
    }
}
