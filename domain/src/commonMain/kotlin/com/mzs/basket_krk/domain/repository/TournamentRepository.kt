package com.mzs.basket_krk.domain.repository

import com.mzs.basket_krk.domain.model.TournamentType

interface TournamentRepository {
    fun getCurrentTournament(): TournamentType
    fun setCurrentTournament(type: TournamentType)
}
