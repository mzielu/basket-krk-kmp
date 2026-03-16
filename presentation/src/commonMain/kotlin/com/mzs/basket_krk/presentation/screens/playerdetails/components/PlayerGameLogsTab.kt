package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.season_input_hint
import basket_krk.presentation.generated.resources.team_input_hint
import com.mzs.basket_krk.domain.model.PlayerLogByTeam
import com.mzs.basket_krk.domain.model.PlayerLogList
import com.mzs.basket_krk.domain.model.Season
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.presentation.base.ui.DropdownFormField
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerGameLogsTab(
    playerLogList: PlayerLogList,
    selectedTeam: PlayerLogByTeam?,
    selectedSeason: Season?,
    seasons: List<Season>,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onSeasonSelected: (Season) -> Unit,
    onTeamSelected: (PlayerLogByTeam) -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        // Filter row
        Row {
            DropdownFormField(
                label = stringResource(Res.string.season_input_hint),
                options = seasons,
                selectedOption = selectedSeason,
                onOptionSelected = onSeasonSelected,
                readableValue = { it?.num?.toString() ?: "" },
                modifier = Modifier.width(100.dp)
            )
            Spacer(Modifier.width(8.dp))
            DropdownFormField(
                label = stringResource(Res.string.team_input_hint),
                options = playerLogList.data,
                selectedOption = selectedTeam,
                onOptionSelected = onTeamSelected,
                readableValue = { it?.team?.name ?: "" },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(8.dp))
        // Table
        if (selectedTeam != null && selectedTeam.logs.isNotEmpty()) {
            PlayerGameLogsTable(
                playerLogs = selectedTeam.logs,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onMatchPress = onMatchPress,
                onSortByStat = onSortByStat,
            )
        }
    }
}
