package com.mzs.basket_krk.data.service

import arrow.core.Either
import co.touchlab.kermit.Logger

import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.domain.service.InAppPurchaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSSet
import platform.Foundation.NSURL
import platform.StoreKit.SKPayment
import platform.StoreKit.SKPaymentQueue
import platform.StoreKit.SKPaymentTransaction
import platform.StoreKit.SKPaymentTransactionObserverProtocol
import platform.StoreKit.SKPaymentTransactionStateFailed
import platform.StoreKit.SKPaymentTransactionStatePurchased
import platform.StoreKit.SKPaymentTransactionStateRestored
import platform.StoreKit.SKProduct
import platform.StoreKit.SKProductPeriodUnitDay
import platform.StoreKit.SKProductPeriodUnitMonth
import platform.StoreKit.SKProductPeriodUnitWeek
import platform.StoreKit.SKProductPeriodUnitYear
import platform.StoreKit.SKProductsRequest
import platform.StoreKit.SKProductsRequestDelegateProtocol
import platform.StoreKit.SKProductsResponse
import platform.StoreKit.SKRequest
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual fun createInAppPurchaseService(): InAppPurchaseService = IosInAppPurchaseService()

class IosInAppPurchaseService : InAppPurchaseService {

    companion object {
        private const val PREMIUM_PRODUCT_ID = "premium"
        private const val MANAGE_SUBSCRIPTIONS_URL = "https://apps.apple.com/account/subscriptions"
    }

    private val _premiumActive = MutableStateFlow(true) // generous default per D-14
    override val premiumActiveFlow: StateFlow<Boolean> = _premiumActive.asStateFlow()

    private var cachedProduct: SKProduct? = null

    @Suppress("unused")
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Payment observer for purchase updates
    private val paymentObserver = object : NSObject(), SKPaymentTransactionObserverProtocol {
        override fun paymentQueue(queue: SKPaymentQueue, updatedTransactions: List<*>) {
            updatedTransactions.filterIsInstance<SKPaymentTransaction>().forEach { transaction ->
                when (transaction.transactionState) {
                    SKPaymentTransactionStatePurchased -> {
                        Logger.d("IosInAppPurchaseService: purchased!")
                        _premiumActive.value = true
                        queue.finishTransaction(transaction)
                    }
                    SKPaymentTransactionStateRestored -> {
                        Logger.d("IosInAppPurchaseService: restored!")
                        _premiumActive.value = true
                        queue.finishTransaction(transaction)
                    }
                    SKPaymentTransactionStateFailed -> {
                        Logger.e("IosInAppPurchaseService: purchase failed: ${transaction.error?.localizedDescription}")
                        queue.finishTransaction(transaction)
                    }
                    else -> { /* pending or deferred */ }
                }
            }
        }
    }

    override suspend fun initialize() {
        Logger.d("IosInAppPurchaseService: initializing...")
        SKPaymentQueue.defaultQueue().addTransactionObserver(paymentObserver)
        restorePurchases()
    }

    private suspend fun restorePurchases() {
        try {
            // Restore purchases to check for existing premium
            SKPaymentQueue.defaultQueue().restoreCompletedTransactions()
            // Note: The result comes via paymentObserver.
            // Give a brief window for the observer to receive restored transactions.
            delay(2000)
            // If observer set it to true during delay, keep it. Otherwise mark as not premium.
            Logger.d("IosInAppPurchaseService: restore window complete, premium=${_premiumActive.value}")
        } catch (e: Exception) {
            Logger.e("IosInAppPurchaseService: restore failed: $e")
        }
    }

    override suspend fun getProducts(): Either<Failure, List<PremiumProduct>> {
        return try {
            val products = fetchProducts()
            val mapped = products.map { skProduct ->
                cachedProduct = skProduct
                PremiumProduct(
                    id = skProduct.productIdentifier,
                    formattedPriceAndPeriod = skProduct.priceAndPeriod(),
                )
            }
            Either.Right(mapped)
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e))
        }
    }

    private suspend fun fetchProducts(): List<SKProduct> = suspendCancellableCoroutine { continuation ->
        val productIds = NSSet.setWithObject(PREMIUM_PRODUCT_ID)
        val request = SKProductsRequest(productIdentifiers = productIds)
        val delegate = object : NSObject(), SKProductsRequestDelegateProtocol {
            override fun productsRequest(request: SKProductsRequest, didReceiveResponse: SKProductsResponse) {
                @Suppress("UNCHECKED_CAST")
                val products = didReceiveResponse.products() as? List<SKProduct> ?: emptyList()
                if (continuation.isActive) continuation.resume(products)
            }

            override fun request(request: SKRequest, didFailWithError: NSError?) {
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        Throwable("Product fetch failed: ${didFailWithError?.localizedDescription}")
                    )
                }
            }
        }
        request.delegate = delegate
        request.start()
    }

    override suspend fun buySubscription(productId: String): Either<Failure, Unit> {
        val product = cachedProduct
            ?: return Either.Left(Failure.UnknownError(Throwable("Product not loaded")))

        return try {
            val payment = SKPayment.paymentWithProduct(product)
            SKPaymentQueue.defaultQueue().addPayment(payment)
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e))
        }
    }

    override fun openManageSubscriptions() {
        val url = NSURL(string = MANAGE_SUBSCRIPTIONS_URL)
        UIApplication.sharedApplication.openURL(url!!)
    }

    // Price/period display for iOS per D-08
    private fun SKProduct.priceAndPeriod(): String {
        val priceStr = "${price()}"
        val period = subscriptionPeriod() ?: return "$priceStr / 6 months"
        val periodStr = when (period.unit) {
            SKProductPeriodUnitDay -> "${period.numberOfUnits()} days"
            SKProductPeriodUnitWeek -> "${period.numberOfUnits()} weeks"
            SKProductPeriodUnitMonth -> when (period.numberOfUnits().toInt()) {
                1 -> "1 month"
                3 -> "3 months"
                6 -> "6 months"
                else -> "6 months"
            }
            SKProductPeriodUnitYear -> "1 year"
            else -> "6 months"
        }
        return "$priceStr / $periodStr"
    }
}
