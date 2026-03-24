package com.mzs.basket_krk.domain.usecase

import arrow.core.Either
import com.mzs.basket_krk.domain.base.SuspendOutUseCase
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.domain.service.InAppPurchaseService

interface GetPremiumProducts : SuspendOutUseCase<Either<Failure, List<PremiumProduct>>>

class GetPremiumProductsUseCase(
    private val inAppPurchaseService: InAppPurchaseService,
) : GetPremiumProducts {
    override suspend fun invoke(): Either<Failure, List<PremiumProduct>> =
        inAppPurchaseService.getProducts()
}
