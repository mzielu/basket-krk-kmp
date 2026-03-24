package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendInOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.service.InAppPurchaseService

interface BuySubscription : SuspendInOutUseCase<String, Either<Failure, Unit>>

class BuySubscriptionUseCase(
    private val inAppPurchaseService: InAppPurchaseService,
) : BuySubscription {
    override suspend fun invoke(input: String): Either<Failure, Unit> =
        inAppPurchaseService.buySubscription(input)
}
