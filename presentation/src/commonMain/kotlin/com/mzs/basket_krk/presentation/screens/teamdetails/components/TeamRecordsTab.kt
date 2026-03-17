package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mzs.basket_krk.domain.model.TeamRecord
import com.mzs.basket_krk.domain.model.TeamRecordRange
import com.mzs.basket_krk.domain.model.TeamRecordStatOption

@Composable
fun TeamRecordsTab(
    records: List<TeamRecord>,
    selectedStatOption: TeamRecordStatOption,
    selectedRange: TeamRecordRange,
    onFilterChanged: (TeamRecordStatOption, TeamRecordRange) -> Unit,
    onPlayerPress: (Int) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Text("Records tab - ${records.size} records")
}
