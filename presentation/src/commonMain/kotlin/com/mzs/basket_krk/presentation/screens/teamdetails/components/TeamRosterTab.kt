package com.mzs.basket_krk.presentation.screens.teamdetails.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import com.mzs.basket_krk.presentation.base.ui.StatDisplayTypeToggle

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
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        // Toolbar: season dropdown left, toggle right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownFormField(
                label = "Season",
                options = seasons,
                selectedOption = selectedSeason,
                onOptionSelected = onSeasonSelected,
                readableValue = { it?.num?.toString() ?: "" },
                modifier = Modifier.width(100.dp)
            )
            Spacer(Modifier.weight(1f))
            StatDisplayTypeToggle(
                statDisplayType = statDisplayType,
                onToggle = onStatDisplayTypeChanged
            )
        }
        Spacer(Modifier.height(8.dp))
        if (roster.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No roster available", style = BasketKrkStyles.itemAdditionalInfo)
            }
        } else {
            TeamRosterTable(
                roster = roster,
                statDisplayType = statDisplayType,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onPlayerPress = onPlayerPress,
                onSortByStat = onSortByStat,
            )
        }
    }
}
