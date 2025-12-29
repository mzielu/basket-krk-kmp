package com.mzs.basket_krk.datautils

import com.mzs.basket_krk.datautils.LeagueFakeData.league
import com.mzs.basket_krk.domain.model.League
import com.mzs.basket_krk.domain.model.Match
import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.MatchDetailsTeam
import com.mzs.basket_krk.domain.model.MatchStatus
import com.mzs.basket_krk.domain.model.MatchTeam
import com.mzs.basket_krk.domain.model.MatchType
import com.mzs.basket_krk.domain.model.TournamentType
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

    fun matchDetailsTeam(
        id: Int = 1,
        name: String = "Team Name",
        shortName: String = "TN",
        logo: String = "http://example.com/logo.png",
        points: Int = 0
    ) = MatchDetailsTeam(
        id = id,
        name = name,
        shortName = shortName,
        logo = logo,
        points = points
    )

    fun matchDetails(
        id: Int = 1,
        idTmnt: Int = 1,
        tournament: TournamentType = TournamentType.MBA,
        type: MatchType = MatchType.REGULAR_SEASON,
        status: MatchStatus = MatchStatus.FINISHED,
        date: LocalDate = LocalDate(2024, 6, 14),
        time: String = "18:00",
        team1: MatchDetailsTeam = matchDetailsTeam(name = "Home Team", points = 85),
        team2: MatchDetailsTeam = matchDetailsTeam(name = "Away Team", points = 78),
        qtrs: List<String> = listOf("12:20", "25:18", "22:22", "26:18"),
        desc: String? = "FINAL (ALL-STAR)",
        arena: String? = "Main Arena",
        league: League? = league()
    ) = MatchDetails(
        id = id,
        idTmnt = idTmnt,
        tournament = tournament,
        type = type,
        status = status,
        date = date,
        time = time,
        t1 = team1,
        t2 = team2,
        qtrs = qtrs,
        description = desc,
        arena = arena,
        league = league
    )
}
