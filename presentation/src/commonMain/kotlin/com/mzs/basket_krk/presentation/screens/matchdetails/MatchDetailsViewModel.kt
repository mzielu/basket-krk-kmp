package com.mzs.basket_krk.presentation.screens.matchdetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.MatchDetails
import com.mzs.basket_krk.domain.usecase.GetMatchDetails
import com.mzs.basket_krk.domain.usecase.GetMatchDetailsUseCase
import com.mzs.basket_krk.presentation.base.ViewStateData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MatchDetailsViewModel(
    private val matchId: Int,
    private val getMatchDetails: GetMatchDetails
) : ViewModel() {
    private val _viewState: MutableStateFlow<MatchDetailsViewState> =
        MutableStateFlow(MatchDetailsViewState())
    val viewState: StateFlow<MatchDetailsViewState> = _viewState.asStateFlow()

    init {
        fetchMatchDetails()
    }

    private fun fetchMatchDetails() {
        viewModelScope.launch {
            _viewState.update { it.copy(matchDetails = it.matchDetails.loading()) }
            getMatchDetails(input = GetMatchDetailsUseCase.Input(matchId = matchId))
                .onSuspendSuccess { details ->
                    _viewState.update { it.copy(matchDetails = it.matchDetails.data(details)) }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error when fetching match details", error)
                    _viewState.update { it.copy(matchDetails = it.matchDetails.error(error)) }
                }
        }
    }

    fun retry() {
        fetchMatchDetails()
    }
}

@Immutable
data class MatchDetailsViewState(
    val matchDetails: ViewStateData<MatchDetails?> = ViewStateData(null),
)

sealed class MatchDetailsEffect {
    data class ShowError(val message: String) : MatchDetailsEffect()
}
