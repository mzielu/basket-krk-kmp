package com.mzs.basket_krk.domain

import com.mzs.basket_krk.domain.model.MatchDetailsTeam
import com.mzs.basket_krk.domain.model.PlayerWithStat
import com.mzs.basket_krk.domain.model.StatDisplayType
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.getValueForGivenOption

fun MatchDetailsTeam.sortTeam(
    statOption: StatOption,
    statDisplayType: StatDisplayType = StatDisplayType.SUM
): List<PlayerWithStat>? {
    return this.stats?.sortedByDescending { stat ->
        stat.stat.getValueForGivenOption(statOption, statDisplayType)
    }
}