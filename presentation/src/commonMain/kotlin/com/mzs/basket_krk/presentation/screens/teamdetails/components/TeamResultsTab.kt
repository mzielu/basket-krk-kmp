package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.TeamResultList

@Composable
fun TeamResultsTab(
    resultList: TeamResultList,
    seasons: List<Season>,
    selectedSeason: Season?,
    onSeasonSelected: (Season) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Text("Results tab - ${resultList.data.size} results")
}
