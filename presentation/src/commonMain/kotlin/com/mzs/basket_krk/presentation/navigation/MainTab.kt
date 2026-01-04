package com.mzs.basket_krk.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PivotTableChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.ui.graphics.vector.ImageVector
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.bottom_bar_matches
import basket_krk.presentation.generated.resources.bottom_bar_more
import basket_krk.presentation.generated.resources.bottom_bar_search
import basket_krk.presentation.generated.resources.bottom_bar_standings
import basket_krk.presentation.generated.resources.bottom_bar_stats
import org.jetbrains.compose.resources.StringResource

enum class MainTab {
    MATCHES,
    SEARCH,
    STATS,
    MORE,
}

fun MainTab.icon(): ImageVector =
    when (this) {
        MainTab.MATCHES -> Icons.Outlined.SportsBasketball
        MainTab.SEARCH -> Icons.Default.Search
        MainTab.STATS -> Icons.Outlined.QueryStats
        MainTab.MORE -> Icons.Default.MoreVert
    }

fun MainTab.titleRes(): StringResource =
    when (this) {
        MainTab.MATCHES -> Res.string.bottom_bar_matches
        MainTab.SEARCH -> Res.string.bottom_bar_search
        MainTab.STATS -> Res.string.bottom_bar_stats
        MainTab.MORE -> Res.string.bottom_bar_more
    }
