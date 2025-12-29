package com.mzs.basket_krk.presentation.base

import androidx.compose.runtime.Composable
import basket_krk.presentation.generated.resources.Res
import basket_krk.presentation.generated.resources.live_label
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import org.jetbrains.compose.resources.stringResource

@Composable
fun Match.getMatchDateTime(): String {
    return if (status == MatchStatus.IN_PROGRESS) {
        stringResource(Res.string.live_label)
    } else {
        "$time $date";
    }
}