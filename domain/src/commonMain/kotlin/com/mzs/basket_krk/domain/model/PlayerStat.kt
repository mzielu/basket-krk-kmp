package com.mzs.basket_krk.domain.model

data class PlayerStat(
    val season: Int,
    val team: Team,
    val league: League,
    val stat: Stat
)

fun PlayerStat.toReadableStatOptionText(statOption: StatOption, statDisplayType: StatDisplayType): String {
    return when (statOption) {
        StatTeam -> team.name
        StatLeague -> league.name
        StatSeason -> season.toString()
        else -> stat.getValueForGivenOption(statOption, statDisplayType)
            ?.toReadableStatOptionText(statOption) ?: ""
    }
}
