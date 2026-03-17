package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.presentation.base.ui.StatDisplayTypeToggle

@Composable
fun PlayerStatsTab(
    playersStats: List<PlayerStat>,
    statDisplayType: StatDisplayType,
    onStatDisplayTypeChanged: (StatDisplayType) -> Unit,
    onTeamPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        // Toggle aligned to the right
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            StatDisplayTypeToggle(statDisplayType = statDisplayType, onToggle = onStatDisplayTypeChanged)
        }
        // Table
        if (playersStats.isNotEmpty()) {
            PlayerStatsTable(playersStats = playersStats, statDisplayType = statDisplayType, onTeamPress = onTeamPress)
        }
    }
}

