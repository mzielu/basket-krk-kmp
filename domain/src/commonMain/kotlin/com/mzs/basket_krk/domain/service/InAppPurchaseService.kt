package com.mzs.basket_krk.domain.service

import arrow.core.Either
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PremiumProduct
import kotlinx.coroutines.flow.StateFlow

interface InAppPurchaseService {
    val premiumActiveFlow: StateFlow<Boolean>
    suspend fun initialize()
    suspend fun getProducts(): Either<Failure, List<PremiumProduct>>
    suspend fun buySubscription(productId: String): Either<Failure, Unit>
    fun openManageSubscriptions()
    fun setActivity(activity: Any?) {} // default no-op; Android implementation casts to Activity
}
