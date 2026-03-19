package com.mzs.basket_krk.domain.usecase

import com.mzs.basket_krk.domain.base.OutUseCase
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.domain.repository.TournamentRepository

interface GetCurrentTournament : OutUseCase<TournamentType>

class GetCurrentTournamentUseCase(
    private val tournamentRepository: TournamentRepository
) : GetCurrentTournament {
    override fun invoke(): TournamentType =
        tournamentRepository.getCurrentTournament()
}
