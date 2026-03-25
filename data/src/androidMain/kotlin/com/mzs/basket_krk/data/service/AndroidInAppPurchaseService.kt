package com.mzs.basket_krk.data.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import arrow.core.Either
import co.touchlab.kermit.Logger

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.mzs.basket_krk.domain.model.Failure
import com.mzs.basket_krk.domain.model.PremiumProduct
import com.mzs.basket_krk.domain.service.InAppPurchaseService
import kotlinx.coroutines.CompletableDeferred
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
    private val initialized = CompletableDeferred<Unit>()

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
        Logger.d("InAppPurchaseService: initializing on Android...")
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .enablePrepaidPlans()
            .build()
        val client = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()
        billingClient = client

        suspendCancellableCoroutine { continuation ->
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        Logger.d("InAppPurchaseService: billing setup finished OK")
                        if (continuation.isActive) continuation.resume(Unit, null)
                    } else {
                        Logger.e("InAppPurchaseService: billing setup failed: ${result.debugMessage}")
                        if (continuation.isActive) continuation.resume(Unit, null)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Logger.d("InAppPurchaseService: billing service disconnected")
                }
            })
        }

        restorePurchases()
        initialized.complete(Unit)
    }

    private suspend fun restorePurchases() {
        val client = billingClient ?: return
        try {
            // Check both SUBS and INAPP — product type depends on Play Console config
            val allPurchases = mutableListOf<Purchase>()
            for (type in listOf(BillingClient.ProductType.SUBS, BillingClient.ProductType.INAPP)) {
                val params = QueryPurchasesParams.newBuilder()
                    .setProductType(type)
                    .build()
                val result = client.queryPurchasesAsync(params)
                allPurchases.addAll(result.purchasesList)
            }
            val hasPremium = allPurchases.any { purchase ->
                purchase.products.contains(PREMIUM_PRODUCT_ID) &&
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED
            }
            _premiumActive.value = hasPremium
            Logger.d("InAppPurchaseService: restore complete, premium=$hasPremium")

            // Acknowledge any unacknowledged purchases
            allPurchases
                .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged }
                .forEach { purchase -> acknowledgePurchase(purchase) }
        } catch (e: Exception) {
            Logger.e("InAppPurchaseService: restore failed: $e")
            // On failure, keep generous default (true) — matches Flutter behavior
        }
    }

    override suspend fun getProducts(): Either<Failure, List<PremiumProduct>> {
        initialized.await()
        val client = billingClient
            ?: return Either.Left(Failure.UnknownError(Throwable("BillingClient not initialized")))
        return try {
            // Try SUBS first, then INAPP — Flutter's in_app_purchase queries both types
            val subsResult = queryProductsByType(client, BillingClient.ProductType.SUBS)
            val products = if (subsResult.isNotEmpty()) {
                subsResult
            } else {
                queryProductsByType(client, BillingClient.ProductType.INAPP)
            }
            Either.Right(products)
        } catch (e: Exception) {
            Either.Left(Failure.UnknownError(e))
        }
    }

    private suspend fun queryProductsByType(
        client: BillingClient,
        productType: String,
    ): List<PremiumProduct> {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PREMIUM_PRODUCT_ID)
                .setProductType(productType)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()
        val result = client.queryProductDetails(params)
        Logger.d("InAppPurchaseService: query $productType result: ${result.billingResult.responseCode}, products: ${result.productDetailsList?.size}")
        return if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            result.productDetailsList?.map { details ->
                cachedProductDetails = details
                PremiumProduct(
                    id = details.productId,
                    formattedPriceAndPeriod = details.priceAndPeriod(),
                )
            } ?: emptyList()
        } else {
            emptyList()
        }
    }

    override suspend fun buySubscription(productId: String): Either<Failure, Unit> {
        initialized.await()
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
            Logger.d("InAppPurchaseService: premium purchased!")
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
            Logger.d("InAppPurchaseService: purchase acknowledged")
        } catch (e: Exception) {
            Logger.e("InAppPurchaseService: acknowledge failed: $e")
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
