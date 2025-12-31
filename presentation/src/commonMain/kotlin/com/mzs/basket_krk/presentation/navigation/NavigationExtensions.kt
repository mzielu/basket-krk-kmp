package com.mzs.basket_krk.presentation.navigation

import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.model.TournamentType


private const val DEFAULT_BASE_MBA_MATCH_URL =
    "https://fibalivestats.dcd.shared.geniussports.com/u/UNBA/XXXXXX/"
private const val DEFAULT_BASE_KNBA_MATCH_URL = "https://knba.krakow.pl/mecz/XXXXXX"

fun MatchDetails.getWebLink(): String {
    val base = when (tournament) {
        TournamentType.MBA, TournamentType.WMBA -> DEFAULT_BASE_MBA_MATCH_URL
        TournamentType.KNBA -> DEFAULT_BASE_KNBA_MATCH_URL
    }

    return base.replace("XXXXXX", idTmnt.toString())
}