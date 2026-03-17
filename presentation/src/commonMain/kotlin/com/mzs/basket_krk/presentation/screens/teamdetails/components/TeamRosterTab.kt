package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption

@Composable
fun TeamRosterTab(
    roster: List<PlayerWithStat>,
    seasons: List<Season>,
    selectedSeason: Season?,
    statDisplayType: StatDisplayType,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onSeasonSelected: (Season) -> Unit,
    onStatDisplayTypeChanged: (StatDisplayType) -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onPlayerPress: (Int) -> Unit,
) {
    Text("Roster tab - ${roster.size} players")
}
