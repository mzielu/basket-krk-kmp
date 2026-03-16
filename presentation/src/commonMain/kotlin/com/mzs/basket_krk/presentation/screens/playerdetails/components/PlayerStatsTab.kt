package com.mzs.basket_krk.presentation.screens.playerdetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mzs.basket_krk.domain.model.PlayerStat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.presentation.base.ui.BasketKrkColors
import com.mzs.basket_krk.presentation.base.ui.BasketKrkStyles

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

@Composable
private fun StatDisplayTypeToggle(
    statDisplayType: StatDisplayType,
    onToggle: (StatDisplayType) -> Unit,
) {
    Row(modifier = Modifier.border(1.5.dp, BasketKrkColors.TextSecondary, RoundedCornerShape(5.dp))) {
        StatDisplayType.entries.forEach { type ->
            val isSelected = statDisplayType == type
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(30.dp)
                    .background(if (isSelected) BasketKrkColors.Main.copy(alpha = 0.2f) else Color.Transparent)
                    .clickable { onToggle(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = type.name.uppercase(),
                    style = BasketKrkStyles.fixedColumnText.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) BasketKrkColors.Main else BasketKrkColors.TextSecondary
                    )
                )
            }
        }
    }
}
