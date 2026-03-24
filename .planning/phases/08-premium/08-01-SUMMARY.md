---
phase: 08-premium
plan: "01"
subsystem: premium-iap-data-layer
tags: [iap, billing, storekit, domain, di, koin]
dependency_graph:
  requires: []
  provides: [InAppPurchaseService, PremiumProduct, GetPremiumProducts, BuySubscription, ObservePremiumActive]
  affects: [AllTimeLeadersPagingSource, PremiumScreen, DataModule, PresentationModule]
tech_stack:
  added: [billing-ktx:7.1.1, StoreKit (iOS cinterop)]
  patterns: [expect/actual, Koin createdAtStart, MutableStateFlow generous default, KoinPlatform.getKoin() for Android context]
key_files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PremiumProduct.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/service/InAppPurchaseService.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPremiumProductsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/BuySubscriptionUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/ObservePremiumActiveUseCase.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/InAppPurchaseServiceFactory.kt
    - data/src/androidMain/kotlin/com/mzs/basket_krk/data/service/AndroidInAppPurchaseService.kt
    - data/src/iosMain/kotlin/com/mzs/basket_krk/data/service/IosInAppPurchaseService.kt
  modified:
    - data/build.gradle.kts
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt
    - composeApp/src/androidMain/kotlin/com/mzs/basket_krk/MainActivity.kt
decisions:
  - "setActivity(Any?) default no-op on InAppPurchaseService interface avoids composeApp depending on data module directly"
  - "BuySubscription uses SuspendInOutUseCase<String, Either<Failure,Unit>> (not SuspendInUseCase) to preserve failure result"
  - "IosInAppPurchaseService uses StoreKit 1 (SKPaymentQueue/SKProductsRequest) per plan override — StoreKit 2 async/await cinterop too complex"
  - "restorePurchases on iOS uses 2s delay window to allow paymentObserver to fire before marking premium=false"
metrics:
  duration: "3 min"
  completed: "2026-03-24"
  tasks_completed: 2
  files_created: 8
  files_modified: 4
---

# Phase 08 Plan 01: Premium IAP Data Layer Summary

**One-liner:** Native IAP data layer with BillingClient (Android) and StoreKit 1 (iOS) behind InAppPurchaseService interface, using generous-default MutableStateFlow and eager Koin initialization.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | Domain contracts — InAppPurchaseService, PremiumProduct, use cases | be4a584 | 5 files created |
| 2 | Platform implementations, billing dep, DI wiring, Activity injection | 82072d0 | 3 files created, 4 modified |

## What Was Built

### Domain Layer (Task 1)

**`PremiumProduct`** — simple data class with `id` and `formattedPriceAndPeriod` matching Flutter's product model.

**`InAppPurchaseService`** interface with:
- `premiumActiveFlow: StateFlow<Boolean>` for reactive premium state observation
- `suspend fun initialize()` — called eagerly at app startup
- `suspend fun getProducts(): Either<Failure, List<PremiumProduct>>` — product catalog query
- `suspend fun buySubscription(productId: String): Either<Failure, Unit>` — purchase initiation
- `fun openManageSubscriptions()` — platform-specific subscription management deeplink
- `fun setActivity(activity: Any?) {}` — default no-op for Activity binding (Android override casts to Activity)

**Three use cases** following existing interface+class pattern:
- `GetPremiumProductsUseCase` — `SuspendOutUseCase<Either<Failure, List<PremiumProduct>>>`
- `BuySubscriptionUseCase` — `SuspendInOutUseCase<String, Either<Failure, Unit>>`
- `ObservePremiumActiveUseCase` — `OutUseCase<StateFlow<Boolean>>`

### Data Layer (Task 2)

**`InAppPurchaseServiceFactory.kt`** — `expect fun createInAppPurchaseService(): InAppPurchaseService` following the same pattern as `platformEngine()` in `HttpClientFactory.kt`.

**`AndroidInAppPurchaseService`** using BillingClient 7.1.1:
- `BillingClient` connected via `suspendCancellableCoroutine` in `initialize()`
- `restorePurchases()` queries `BillingClient.ProductType.SUBS` and updates `_premiumActive`; on failure keeps generous default `true`
- `getProducts()` queries `SUBS` product details and caches `ProductDetails` for billing flow
- `buySubscription()` launches billing flow using cached `ProductDetails` and first offer token
- `priceAndPeriod()` extension maps `basePlanId` strings to human-readable period strings
- `setActivity(Any?)` override casts to `Activity` for `launchBillingFlow`

**`IosInAppPurchaseService`** using StoreKit 1 (SKPaymentQueue + SKProductsRequest):
- `initialize()` registers `SKPaymentTransactionObserver` and calls `restoreCompletedTransactions()`
- `restorePurchases()` uses 2-second delay window for observer to fire restored transactions
- `fetchProducts()` bridges `SKProductsRequest` delegate callbacks to coroutine via `suspendCancellableCoroutine`
- `buySubscription()` queues `SKPayment` via `SKPaymentQueue.defaultQueue()`
- `priceAndPeriod()` extension parses `SKProductPeriod` unit and count to human-readable string

**DI wiring:**
- `DataModule.kt`: `single<InAppPurchaseService>(createdAtStart = true)` with background coroutine `initialize()` launch
- `PresentationModule.kt`: `GetPremiumProducts`, `BuySubscription`, `ObservePremiumActive` registered as singletons
- `MainActivity.kt`: `setActivity(this)` in `onCreate`, `setActivity(null)` in `onDestroy` for lifecycle-safe Activity binding

## Deviations from Plan

### Auto-fixed Issues

None — plan executed as written with two clarifications applied per plan instructions:

1. `setActivity(Any?)` default no-op added to `InAppPurchaseService` interface (plan listed this as the correct approach when `composeApp` lacks `projects.data` dependency — confirmed and applied).

2. `BuySubscription` uses `SuspendInOutUseCase<String, Either<Failure, Unit>>` (plan explicitly prescribed this over the non-existent `SuspendInUseCase`).

## Known Stubs

None — this plan creates infrastructure only (domain contracts + platform implementations + DI). No UI rendering. Downstream plans (08-02 PremiumScreen, 08-03 AllTimeLeaders gating) will consume these services.

## Self-Check: PASSED
