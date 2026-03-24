package com.mzs.basket_krk.domain.usecase

import com.mzs.basket_krk.domain.base.OutUseCase
import com.mzs.basket_krk.domain.service.InAppPurchaseService
import kotlinx.coroutines.flow.StateFlow

interface ObservePremiumActive : OutUseCase<StateFlow<Boolean>>

class ObservePremiumActiveUseCase(
    private val inAppPurchaseService: InAppPurchaseService,
) : ObservePremiumActive {
    override fun invoke(): StateFlow<Boolean> =
        inAppPurchaseService.premiumActiveFlow
}
