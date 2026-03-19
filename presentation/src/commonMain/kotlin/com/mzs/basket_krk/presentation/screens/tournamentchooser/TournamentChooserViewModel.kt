package com.mzs.basket_krk.presentation.screens.tournamentchooser

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.mzs.basket_krk.domain.model.TournamentType
import com.mzs.basket_krk.domain.usecase.GetCurrentTournament
import com.mzs.basket_krk.domain.usecase.SetCurrentTournament
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class TournamentChooserViewModel(
    private val getCurrentTournament: GetCurrentTournament,
    private val setCurrentTournament: SetCurrentTournament,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        TournamentChooserViewState(
            currentTournament = getCurrentTournament(),
            tournaments = TournamentType.entries,
        )
    )
    val viewState: StateFlow<TournamentChooserViewState> = _viewState.asStateFlow()

    private val _effect = MutableSharedFlow<TournamentChooserEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<TournamentChooserEffect> = _effect.asSharedFlow()

    fun onTournamentSelected(tournament: TournamentType) {
        if (tournament == _viewState.value.currentTournament) return
        setCurrentTournament(tournament)
        _effect.tryEmit(TournamentChooserEffect.RestartApp)
    }
}

@Immutable
data class TournamentChooserViewState(
    val currentTournament: TournamentType,
    val tournaments: List<TournamentType>,
)

sealed class TournamentChooserEffect {
    data object RestartApp : TournamentChooserEffect()
}
