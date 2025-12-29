package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.datautils.LeagueFakeData.league
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchTeam
import com.mzs.basket_krk.domain.model.MatchType
import kotlinx.datetime.LocalDate

object MatchFakeData {
    fun match(
        id: Int = 1,
        team1: MatchTeam = matchTeam(name = "Home Team", points = 42),
        team2: MatchTeam = matchTeam(name = "Away Team", points = 69),
        date: LocalDate = LocalDate(2024, 6, 14),
        time: String = "18:00",
        type: MatchType = MatchType.REGULAR_SEASON,
        status: MatchStatus = MatchStatus.FINISHED,
        description: String? = "Test descrpiption",
        arena: String? = "Arena",
        league: League? = league()
    ) = Match(
        id = id,
        team1 = team1,
        team2 = team2,
        date = date,
        time = time,
        arena = arena,
        type = type,
        status = status,
        description = description,
        league = league
    )

    fun matchTeam(
        id: Int = 1,
        name: String = "Team Name",
        shortName: String = "TN",
        logoUrl: String = "http://example.com/logo.png",
        points: Int = 0
    ) = MatchTeam(
        id = id,
        name = name,
        shortName = shortName,
        logoUrl = logoUrl,
        points = points
    )
}
