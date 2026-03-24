package com.mzs.basket_krk.data.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import arrow.core.Either

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.domain.service.InAppPurchaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.mp.KoinPlatform

actual fun createInAppPurchaseService(): InAppPurchaseService {
    val context = KoinPlatform.getKoin().get<Context>()
    return AndroidInAppPurchaseService(context)
}

class AndroidInAppPurchaseService(
    private val context: Context,
) : InAppPurchaseService {

    companion object {
        private const val PREMIUM_PRODUCT_ID = "premium"
        private const val MANAGE_SUBSCRIPTIONS_URL = "https://play.google.com/store/account/subscriptions"
    }

    private val _premiumActive = MutableStateFlow(true) // generous default per D-14
    override val premiumActiveFlow: StateFlow<Boolean> = _premiumActive.asStateFlow()

    private var activity: Activity? = null
    private var billingClient: BillingClient? = null
    private var cachedProductDetails: ProductDetails? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    handlePurchase(purchase)
                }
            }
        }
    }

    override fun setActivity(activity: Any?) {
        this.activity = activity as? Activity
    }

    override suspend fun initialize() {
        println("InAppPurchaseService: initializing on Android...")
        val client = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        billingClient = client

        suspendCancellableCoroutine { continuation ->
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        println("InAppPurchaseService: billing setup finished OK")
                        if (continuation.isActive) continuation.resume(Unit) {}
                    } else {
                        println("InAppPurchaseService: billing setup failed: ${result.debugMessage}")
                        if (continuation.isActive) continuation.resume(Unit) {}
                    }
                }

                override fun onBillingServiceDisconnected() {
                    println("InAppPurchaseService: billing service disconnected")
                }
            })
        }

        restorePurchases()
    }

    private suspend fun restorePurchases() {
        val client = billingClient ?: return
        try {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
            val result = client.queryPurchasesAsync(params)
            val hasPremium = result.purchasesList.any { purchase ->
                purchase.products.contains(PREMIUM_PRODUCT_ID) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            _premiumActive.value = hasPremium
            println("InAppPurchaseService: restore complete, premium=$hasPremium")

            // Acknowledge any unacknowledged purchases
            result.purchasesList
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
                .forEach { purchase -> acknowledgePurchase(purchase) }
        } catch (e: Exception) {
            println("InAppPurchaseService: restore failed: $e")
            // On failure, keep generous default (true) — matches Flutter behavior
        }
    }

    override suspend fun getProducts(): Either<Failure, List<PremiumProduct>> {
        val client = billingClient
            ?: return Either.Left(Failure.UnknownError(Throwable("BillingClient not initialized")))
        return try {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(PREMIUM_PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()
            val result = client.queryProductDetails(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val products = result.productDetailsList?.map { details ->
                    cachedProductDetails = details
                    PremiumProduct(
                        id = details.productId,
                        formattedPriceAndPeriod = details.priceAndPeriod(),
                    )
                } ?: emptyList()
                Either.Right(products)
            } else {
                Either.Left(
                    Failure.UnknownError(Throwable("Product query failed: ${result.billingResult.debugMessage}"))
                )
            }
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e))
        }
    }

    override suspend fun buySubscription(productId: String): Either<Failure, Unit> {
        val client = billingClient
            ?: return Either.Left(Failure.UnknownError(Throwable("BillingClient not initialized")))
        val currentActivity = activity
            ?: return Either.Left(Failure.UnknownError(Throwable("Activity not available")))
        val details = cachedProductDetails
            ?: return Either.Left(Failure.UnknownError(Throwable("Product details not loaded")))

        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return Either.Left(Failure.UnknownError(Throwable("No offer token available")))

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()

        val result = client.launchBillingFlow(currentActivity, flowParams)
        return if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            Either.Right(Unit)
        } else {
            Either.Left(Failure.UnknownError(Throwable("Launch billing flow failed: ${result.debugMessage}")))
        }
    }

    override fun openManageSubscriptions() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(MANAGE_SUBSCRIPTIONS_URL))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.products.contains(PREMIUM_PRODUCT_ID)) {
            println("InAppPurchaseService: premium purchased!")
            _premiumActive.value = true
            if (!purchase.isAcknowledged) {
                scope.launch { acknowledgePurchase(purchase) }
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val client = billingClient ?: return
        try {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client.acknowledgePurchase(params)
            println("InAppPurchaseService: purchase acknowledged")
        } catch (e: Exception) {
            println("InAppPurchaseService: acknowledge failed: $e")
        }
    }

    // Price/period display matching Flutter priceAndPeriod() extension per D-08
    private fun ProductDetails.priceAndPeriod(): String {
        val offer = subscriptionOfferDetails?.firstOrNull() ?: return ""
        val pricingPhase = offer.pricingPhases.pricingPhaseList.lastOrNull() ?: return ""
        val price = pricingPhase.formattedPrice
        val period = when (offer.basePlanId) {
            "premium-1-month" -> "1 month"
            "premium-3-months" -> "3 months"
            "premium-6-months" -> "6 months"
            "premium-1-year" -> "1 year"
            else -> "6 months"
        }
        return "$price / $period"
    }
}
