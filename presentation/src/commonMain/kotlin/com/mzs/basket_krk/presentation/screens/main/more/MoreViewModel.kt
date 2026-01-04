package com.mzs.basket_krk.presentation.screens.main.more

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.mzs.basket_krk.domain.model.Platform
import com.mzs.basket_krk.domain.usecase.GetPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MoreViewModel(
    private val getPlatform: GetPlatform
) : ViewModel() {
    private val _viewState: MutableStateFlow<MoreViewState> = MutableStateFlow(
        MoreViewState(platform = getPlatform.invoke())
    )
    val viewState: StateFlow<MoreViewState> = _viewState.asStateFlow()
}

@Immutable
data class MoreViewState(
    val platform: Platform
)
