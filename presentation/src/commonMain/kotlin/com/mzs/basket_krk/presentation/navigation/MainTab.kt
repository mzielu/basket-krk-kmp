package com.mzs.basket_krk.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.ui.graphics.vector.ImageVector
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.bottom_bar_matches
import basket_krk.presentation.generated.resources.bottom_bar_more
import basket_krk.presentation.generated.resources.bottom_bar_search
import basket_krk.presentation.generated.resources.bottom_bar_standings
import org.jetbrains.compose.resources.StringResource

enum class MainTab {
    MATCHES,
    SEARCH,
    TABLES,
    MORE,
}

fun MainTab.icon(): ImageVector =
    when (this) {
        MainTab.MATCHES -> Icons.Default.SportsBasketball
        MainTab.SEARCH -> Icons.Default.Search
        MainTab.TABLES -> Icons.Default.TableRows
        MainTab.MORE -> Icons.Default.MoreVert
    }

fun MainTab.titleRes(): StringResource =
    when (this) {
        MainTab.MATCHES -> Res.string.bottom_bar_matches
        MainTab.SEARCH -> Res.string.bottom_bar_search
        MainTab.TABLES -> Res.string.bottom_bar_standings
        MainTab.MORE -> Res.string.bottom_bar_more
    }
