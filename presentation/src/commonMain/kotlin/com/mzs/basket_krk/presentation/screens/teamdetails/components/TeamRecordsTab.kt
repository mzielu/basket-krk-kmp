package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mzs.basket_krk.domain.model.TeamRecord

@Composable
fun TeamRecordsTab(
    records: List<TeamRecord>,
    onPlayerPress: (Int) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Text("Records tab - ${records.size} records")
}
