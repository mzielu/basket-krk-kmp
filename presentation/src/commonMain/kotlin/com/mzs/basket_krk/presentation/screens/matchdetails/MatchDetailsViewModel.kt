package com.mzs.basket_krk.presentation.screens.matchdetails

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.MatchDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MatchDetailsViewModel(
    private val matchId: Int
) : ViewModel() {
    private val _viewState: MutableStateFlow<MatchDetailsViewState> =
        MutableStateFlow(MatchDetailsViewState())
    val viewState: StateFlow<MatchDetailsViewState> = _viewState.asStateFlow()

    init {
        Logger.d("XDDDDDD MatchDetailsViewModel initialized with matchId: $matchId")
    }
}

@Immutable
data class MatchDetailsViewState(
    val fullScreenLoading: Boolean = false,
    val matchDetails: MatchDetails? = null,
    val error: Failure? = null
)

sealed class MatchDetailsEffect {
    data class ShowError(val message: String) : MatchDetailsEffect()
}
