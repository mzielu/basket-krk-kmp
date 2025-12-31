package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mzs.basket_krk.datautils.PlayerFakeData
import com.mzs.basket_krk.datautils.StatFakeData
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.presentation.base.drawTopBottomBorder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LeftColumnPlayerCell(
    playerWithStat: PlayerWithStat,
    height: Dp,
    onClick: () -> Unit
) {
    val number = playerWithStat.stat.num
    val name =
        if (number != null) "${playerWithStat.player.name} ($number)" else playerWithStat.player.name

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .background(BasketKrkColors.DefaultBackground)
            .drawTopBottomBorder()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        AutoSizeText(
            text = name,
            style = BasketKrkStyles.fixedColumnText,
            maxLines = 1,
        )
    }
}

@Composable
@Preview
fun LeftColumnPlayerCellPreview() {
    LeftColumnPlayerCell(
        playerWithStat = PlayerWithStat(
            player = PlayerFakeData.playerShort(),
            stat = StatFakeData.stat()
        ),
        height = 40.dp,
        onClick = {}
    )
}

@Composable
@Preview
fun LeftColumnPlayerCellLongNamePreview() {
    LeftColumnPlayerCell(
        playerWithStat = PlayerWithStat(
            player = PlayerFakeData.playerShort(
                name = "Jonathan Alexander Maximilian Doe-Smith the Third even longer that that"
            ),
            stat = StatFakeData.stat()
        ),
        height = 40.dp,
        onClick = {}
    )
}