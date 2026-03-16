package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerRecord
import com.mzs.basket_krk.domain.model.getSign
import com.mzs.basket_krk.domain.model.toDescription
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles

@Composable
fun PlayerRecordsTab(
    records: List<PlayerRecord>,
    onRecordPress: (Int) -> Unit,
) {
    if (records.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No records available", style = BasketKrkStyles.itemAdditionalInfo)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            items(records.size) { index ->
                Spacer(Modifier.size(4.dp))
                RecordItem(record = records[index], onClick = { onRecordPress(records[index].matchId) })
                Spacer(Modifier.size(4.dp))
            }
        }
    }
}

@Composable
private fun RecordItem(
    record: PlayerRecord,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BasketKrkColors.BorderRoundedItem, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Left: circle with value + stat sign below
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .border(3.dp, BasketKrkColors.Main, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${record.value}", style = BasketKrkStyles.recordValue)
                }
                Text(record.recordType.getSign(), style = BasketKrkStyles.recordStatSign)
            }
            Spacer(Modifier.width(8.dp))
            // Middle: description + secondary text
            Column(modifier = Modifier.weight(1f)) {
                Text(record.recordType.toDescription(), style = BasketKrkStyles.recordDescription)
                Text(
                    buildSecondaryText(record.times, record.date),
                    style = BasketKrkStyles.itemAdditionalInfo
                )
            }
            // Right: open icon
            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = BasketKrkColors.Main)
        }
    }
}

private fun buildSecondaryText(times: Int, date: String): String {
    return if (times == 1) {
        "1 time - $date"
    } else {
        "$times times - last time $date"
    }
}
