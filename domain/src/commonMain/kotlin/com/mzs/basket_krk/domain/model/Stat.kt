package com.mzs.basket_krk.domain.model

data class Stat(
    val m1: Int,
    val a1: Int,
    val m2: Int,
    val a2: Int,
    val m3: Int,
    val a3: Int,
    val pt: Int,
    val a: Int,
    val r: Int,
    val b: Int,
    val s: Int,
    val t: Int,
    val fl: Int,
    val tch: Int,
    val eff: Int,
    val m: Int? = null,
    val sec: Int? = null,
    val flon: Int? = null,
    val pm: Int? = null,
    val mvp: Int? = null,
    val dR: Int? = null,
    val oR: Int? = null,
    val num: Int? = null
)

fun Stat.getValueForGivenOption(
    statOption: StatOption,
    statDisplayType: StatDisplayType
): Double? {
    val divisionValue = when (statDisplayType) {
        StatDisplayType.AVG -> (m ?: 1)
        StatDisplayType.SUM -> 1
    }

    // Dart: only Summable returns value; non-summable -> null
    if (statOption !is Summable) return null

    return when (statOption) {
        StatMatches -> m?.toDouble()

        StatSeconds -> sec?.let { it.toDouble() / divisionValue }

        StatFtm -> m1.toDouble() / divisionValue
        StatFta -> a1.toDouble() / divisionValue
        StatFtPcg -> percentageField(m1, a1)

        StatFgm -> (m2 + m3).toDouble() / divisionValue
        StatFga -> (a2 + a3).toDouble() / divisionValue
        StatFgPcg -> percentageField((m2 + m3), (a2 + a3))

        StatFg3m -> m3.toDouble() / divisionValue
        StatFg3a -> a3.toDouble() / divisionValue
        StatFg3Pcg -> percentageField(m3, a3)

        StatPts -> pt.toDouble() / divisionValue
        StatAst -> a.toDouble() / divisionValue
        StatReb -> r.toDouble() / divisionValue

        StatOReb -> oR?.let { it.toDouble() / divisionValue }
        StatDReb -> dR?.let { it.toDouble() / divisionValue }

        StatStl -> s.toDouble() / divisionValue
        StatBlk -> b.toDouble() / divisionValue
        StatTo -> t.toDouble() / divisionValue

        StatTech -> tch.toDouble() / divisionValue
        StatPFouls -> fl.toDouble() / divisionValue

        StatPFoulsOn -> flon?.let { it.toDouble() / divisionValue }
        StatMvp -> mvp?.let { it.toDouble() / divisionValue }
        StatPlusMinus -> pm?.let { it.toDouble() / divisionValue }

        StatEval -> eff.toDouble() / divisionValue
    }
}

fun Double.toReadableStatOptionText(statOption: StatOption): String {
    return if (statOption === StatSeconds) {
        val secondsEven = this.toInt()
        val minutes = secondsEven / 60
        val seconds = secondsEven % 60
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        if (isIntegerLike(this)) {
            this.toInt().toString()
        } else {
            formatOneDecimal(this)
        }
    }
}

private fun formatOneDecimal(value: Double): String {
    val rounded = kotlin.math.round(value * 10.0) / 10.0
    val intPart = rounded.toInt()
    val frac = kotlin.math.abs((rounded - intPart) * 10).toInt()
    return "$intPart.$frac"
}

private fun percentageField(made: Int, attempted: Int): Double {
    return if (attempted == 0) {
        0.0
    } else {
        (100.0 * made) / attempted
    }
}

private fun isIntegerLike(value: Double): Boolean {
    return value % 1.0 == 0.0
}