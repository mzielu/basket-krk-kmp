package com.mzs.basket_krk.presentation.screens.premium

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.mzs.basket_krk.domain.base.onSuspendGeneralError
import com.mzs.basket_krk.domain.base.onSuspendSuccess
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.Platform
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.domain.usecase.BuySubscription
import com.mzs.basket_krk.domain.usecase.GetPlatform
import com.mzs.basket_krk.domain.usecase.GetPremiumProducts
import com.mzs.basket_krk.domain.usecase.ObservePremiumActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PremiumViewModel(
    private val getProducts: GetPremiumProducts,
    private val buySubscription: BuySubscription,
    private val observePremiumActive: ObservePremiumActive,
    private val getPlatform: GetPlatform,
) : ViewModel() {

    private val _viewState = MutableStateFlow(PremiumViewState())
    val viewState: StateFlow<PremiumViewState> = _viewState.asStateFlow()

    init {
        _viewState.update { it.copy(platform = getPlatform()) }
        loadProducts()
        observePremium()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            getProducts()
                .onSuspendSuccess { products ->
                    _viewState.update {
                        it.copy(
                            product = products.firstOrNull(),
                            isLoading = false,
                        )
                    }
                }.onSuspendGeneralError { error ->
                    Logger.e("Error fetching premium products", error)
                    _viewState.update {
                        it.copy(
                            error = error as? Failure,
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun observePremium() {
        viewModelScope.launch {
            observePremiumActive().collect { isPremium ->
                _viewState.update { it.copy(isPremiumActive = isPremium) }
            }
        }
    }

    fun onBuyClick() {
        val productId = _viewState.value.product?.id ?: return
        viewModelScope.launch {
            buySubscription(productId)
        }
    }

    fun onRetry() {
        loadProducts()
    }
}

@Immutable
data class PremiumViewState(
    val product: PremiumProduct? = null,
    val isPremiumActive: Boolean = true, // generous default per D-14
    val isLoading: Boolean = true,
    val error: Failure? = null,
    val platform: Platform = Platform.ANDROID,
) {
    val subscriptionManagementUrl: String
        get() = when (platform) {
            Platform.ANDROID -> "https://play.google.com/store/account/subscriptions"
            Platform.IOS -> "https://apps.apple.com/account/subscriptions"
        }
}
