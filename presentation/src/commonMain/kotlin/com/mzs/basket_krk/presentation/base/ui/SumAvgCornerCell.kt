package com.mzs.basket_krk.presentation.base.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.table_corner_avg
import basket_krk.presentation.generated.resources.table_corner_tot
import com.mzs.basket_krk.domain.model.StatDisplayType
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SumAvgCornerCell(
    statDisplayType: StatDisplayType,
    height: Dp
) {
    val label = when (statDisplayType) {
        StatDisplayType.SUM -> stringResource(Res.string.table_corner_tot)
        StatDisplayType.AVG -> stringResource(Res.string.table_corner_avg)
    }

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .padding(start = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = label, style = BasketKrkStyles.fixedColumnTextBold)
    }
}

@Composable
@Preview
fun CornerBottomCellPreview() {
    SumAvgCornerCell(
        statDisplayType = StatDisplayType.AVG,
        height = 40.dp
    )
}