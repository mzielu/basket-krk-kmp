package com.mzs.basket_krk.data.repository

import com.mzs.basket_krk.data.tournament.TournamentProvider
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.domain.repository.TournamentRepository

class TournamentRepositoryImpl(
    private val tournamentProvider: TournamentProvider
) : TournamentRepository {

    override fun getCurrentTournament(): TournamentType =
        TournamentType.fromKey(tournamentProvider.getCurrentKey())

    override fun setCurrentTournament(type: TournamentType) {
        tournamentProvider.setCurrentKey(type.key)
    }
}
