package com.mzs.basket_krk.presentation.screens.main.statistics


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.all_time_leaders
import basket_krk.presentation.generated.resources.league_tables
import basket_krk.presentation.generated.resources.season_leaders
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.NavigationItem
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatisticsScreen(
    openLeagueLeaders: () -> Unit,
    openAllTimeLeaders: () -> Unit,
    openTables: () -> Unit,
) {
    val itemPadding = 8.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BasketKrkColors.DefaultBackground)
            .verticalScroll(rememberScrollState())
            .padding(itemPadding)
    ) {
        NavigationItem(
            title = stringResource(Res.string.season_leaders),
            imageVector = Icons.Outlined.Leaderboard,
            onClick = openLeagueLeaders
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.all_time_leaders),
            imageVector = Icons.Outlined.StarOutline,
            onClick = openAllTimeLeaders
        )

        Spacer(modifier = Modifier.height(itemPadding))

        NavigationItem(
            title = stringResource(Res.string.league_tables),
            imageVector = Icons.Outlined.TableRows,
            onClick = openTables
        )
    }
}

@Composable
@Preview
fun StatisticsScreenPreview() {
    StatisticsScreen(
        openTables = {},
        openAllTimeLeaders = {},
        openLeagueLeaders = {}
    )
}