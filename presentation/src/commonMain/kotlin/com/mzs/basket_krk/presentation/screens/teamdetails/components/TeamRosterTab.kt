package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mzs.basket_krk.domain.model.PlayerWithStat

@Composable
fun TeamRosterTab(
    roster: List<PlayerWithStat>,
    onPlayerPress: (Int) -> Unit,
) {
    Text("Roster tab - ${roster.size} players")
}
