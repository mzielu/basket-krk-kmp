package com.mzs.basket_krk.presentation.screens.matchdetails.components

import com.mzs.basket_krk.domain.model.Stat
import com.mzs.basket_krk.domain.model.StatAst
import com.mzs.basket_krk.domain.model.StatBlk
import com.mzs.basket_krk.domain.model.StatDReb
import com.mzs.basket_krk.domain.model.StatEval
import com.mzs.basket_krk.domain.model.StatFg3Pcg
import com.mzs.basket_krk.domain.model.StatFg3a
import com.mzs.basket_krk.domain.model.StatFg3m
import com.mzs.basket_krk.domain.model.StatFgPcg
import com.mzs.basket_krk.domain.model.StatFga
import com.mzs.basket_krk.domain.model.StatFgm
import com.mzs.basket_krk.domain.model.StatFtPcg
import com.mzs.basket_krk.domain.model.StatFta
import com.mzs.basket_krk.domain.model.StatFtm
import com.mzs.basket_krk.domain.model.StatMatches
import com.mzs.basket_krk.domain.model.StatMvp
import com.mzs.basket_krk.domain.model.StatOReb
import com.mzs.basket_krk.domain.model.StatOption
import com.mzs.basket_krk.domain.model.StatPFouls
import com.mzs.basket_krk.domain.model.StatPFoulsOn
import com.mzs.basket_krk.domain.model.StatPlusMinus
import com.mzs.basket_krk.domain.model.StatPts
import com.mzs.basket_krk.domain.model.StatReb
import com.mzs.basket_krk.domain.model.StatSeconds
import com.mzs.basket_krk.domain.model.StatStl
import com.mzs.basket_krk.domain.model.StatTech
import com.mzs.basket_krk.domain.model.StatTo

object StatCellMapper {

    private val NOT_NULL_STATS: List<StatOption> = listOf(
        StatPts,
        StatAst,
        StatReb,
        StatStl,
        StatBlk,
        StatFgm,
        StatFga,
        StatFgPcg,
        StatFg3m,
        StatFg3a,
        StatFg3Pcg,
        StatFtm,
        StatFta,
        StatFtPcg,
        StatTo,
        StatPFouls,
        StatTech,
        StatEval
    )

    fun getStatOptionsFromModel(stat: Stat): List<StatOption> {
        val result = mutableListOf<StatOption>()

        if (stat.m != null) result += StatMatches
        if (stat.sec != null) result += StatSeconds

        for (element in NOT_NULL_STATS) {
            when (element) {
                StatTo -> {
                    if (stat.dR != null) result += StatDReb
                    if (stat.oR != null) result += StatOReb
                    result += element
                }

                StatPFouls -> {
                    result += element
                    if (stat.flon != null) result += StatPFoulsOn
                }

                StatEval -> {
                    if (stat.pm != null) result += StatPlusMinus
                    result += element
                }

                else -> result += element
            }
        }

        if (stat.mvp != null) result += StatMvp

        return result
    }

    fun getSumStatFromStats(stats: List<Stat>): Stat {
        return Stat(
            m1 = stats.sumOf { it.m1 },
            a1 = stats.sumOf { it.a1 },
            m2 = stats.sumOf { it.m2 },
            a2 = stats.sumOf { it.a2 },
            m3 = stats.sumOf { it.m3 },
            a3 = stats.sumOf { it.a3 },
            pt = stats.sumOf { it.pt },
            a = stats.sumOf { it.a },
            r = stats.sumOf { it.r },
            b = stats.sumOf { it.b },
            s = stats.sumOf { it.s },
            t = stats.sumOf { it.t },
            fl = stats.sumOf { it.fl },
            tch = stats.sumOf { it.tch },
            eff = stats.sumOf { it.eff },

            m = stats.map { it.m }.sumNullableWithAvgNullData(),
            sec = stats.map { it.sec }.sumNullableWithAvgNullData(),
            flon = stats.map { it.flon }.sumNullableWithAvgNullData(),
            pm = stats.map { it.pm }.sumNullableWithAvgNullData(),
            mvp = stats.map { it.mvp }.sumNullableWithAvgNullData(),
            dR = stats.map { it.dR }.sumNullableWithAvgNullData(),
            oR = stats.map { it.oR }.sumNullableWithAvgNullData()
        )
    }
}

/**
 * Mirrors typical behavior used in Dart apps for stats:
 * - if ALL values are null -> return null
 * - else -> sum of non-null values (null treated as 0)
 *
 * If your Dart version does something different (like averaging when some are null),
 * paste it and I'll match it exactly.
 */
private fun List<Int?>.sumNullableWithAvgNullData(): Int? {
    val nonNulls = this.filterNotNull()
    if (nonNulls.isEmpty()) return null
    return nonNulls.sum()
}
