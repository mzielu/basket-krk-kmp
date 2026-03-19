package com.mzs.basket_krk.domain.usecase

import com.mzs.basket_krk.domain.base.InUseCase
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.domain.repository.TournamentRepository

interface SetCurrentTournament : InUseCase<TournamentType>

class SetCurrentTournamentUseCase(
    private val tournamentRepository: TournamentRepository
) : SetCurrentTournament {
    override fun invoke(input: TournamentType) {
        tournamentRepository.setCurrentTournament(input)
    }
}
