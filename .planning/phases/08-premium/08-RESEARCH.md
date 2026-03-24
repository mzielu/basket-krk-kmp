# Phase 8: Premium - Research

**Researched:** 2026-03-24
**Domain:** Kotlin Multiplatform in-app purchase subscriptions (BillingClient / StoreKit 2) + Compose UI + Paging3 gating
**Confidence:** HIGH

---

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

- **D-01:** Use expect/actual with native APIs — BillingClient on Android, StoreKit 2 on iOS. No third-party wrapper or RevenueCat.
- **D-02:** Domain layer defines InAppPurchaseService interface. Data layer provides expect/actual platform implementations (androidMain/iosMain). Matches existing architecture (e.g., HttpClientFactory pattern).
- **D-03:** Single product ID `'premium'` (non-consumable subscription), same as Flutter.
- **D-04:** Local-only receipt validation — trust BillingClient/StoreKit purchase status directly. No server-side validation.
- **D-05:** Subscription type (recurring), matching Flutter's buyNonConsumable + subscription period display.
- **D-06:** StoreKit 2 for iOS (modern async/await API, requires iOS 15+).
- **D-07:** 1:1 Flutter match — subscription card with two states (subscribe vs active), price/period display, platform-specific legal text at bottom.
- **D-08:** Price/period display using platform-specific extraction — Android: basePlanId mapping, iOS: subscriptionPeriod parsing. Matches Flutter's priceAndPeriod() extension.
- **D-09:** All string resources already exist in strings.xml (EN + PL) — no additions needed.
- **D-10:** Premium upsell dialog (premium_dialog_* strings) is DEFERRED — not part of this phase.
- **D-11:** PagingSource intercept — AllTimeLeadersPagingSource checks premium status before loading page 3+. If not premium, returns endOfPaginationReached=true and screen appends a premium indicator item.
- **D-12:** Premium indicator card matches Flutter's CustomBuyPremiumIndicator — lock icon, "Want to see TOP 100?", "Upgrade to Premium" button.
- **D-13:** Tapping "Upgrade to Premium" navigates to Screen.Premium (same as MoreScreen's "Buy Premium" item).
- **D-14:** Generous default — premium=true until restorePurchases completes (matches Flutter). Avoids flash of gated content on cold start.
- **D-15:** StateFlow<Boolean> for premium state observation. MutableStateFlow in InAppPurchaseService, exposed as StateFlow. ViewModels collect as needed.
- **D-16:** Eager initialization — InAppPurchaseService initializes at app startup via Koin singleton. Restores purchases immediately so premium state is ready before AllTimeLeaders is opened.

### Claude's Discretion

- Exact expect/actual class structure for BillingClient/StoreKit integration
- Koin DI wiring for InAppPurchaseService and use cases
- PremiumViewModel state management details
- How to pass premium state into AllTimeLeadersPagingSource (constructor injection vs flow collection)
- Error handling UI for failed product queries or purchase errors
- Platform-specific legal text rendering (RichText with clickable link)

### Deferred Ideas (OUT OF SCOPE)

- **Premium upsell dialog** — premium_dialog_* strings exist but dialog implementation deferred
- **Debug/release AdMob APPLICATION_ID variant** — belongs in ads/monetization phase
- **Ad banner integration** — out of scope per PROJECT.md
</user_constraints>

---

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|------------------|
| PREM-01 | User can view the Premium screen showing subscription options with price and duration | PremiumScreen replaces placeholder; product query via InAppPurchaseService; price/period extraction per platform |
| PREM-02 | User can purchase a subscription via platform-specific in-app purchase (Google Play / App Store) | BillingClient.launchBillingFlow (Android); StoreKit 2 Product.purchase (iOS); purchase state update via MutableStateFlow |
| PREM-03 | User can see active premium status with green confirmation when subscribed | PremiumViewModel observes StateFlow<Boolean>; UI shows active card state with green check icon |
| PREM-04 | User can tap "Manage subscription" to open platform subscription management | LocalUriHandler.openUri() with platform-specific URL (Play Store subscriptions / App Store subscriptions) |
| PREM-05 | Premium status is checked and observed across the app (premium active stream) | InAppPurchaseService as Koin singleton; StateFlow<Boolean> premiumActive; eager init at app start |
| PREM-06 | All-Time Leaders pagination is gated at page 3+ for non-premium users (shows premium indicator) | AllTimeLeadersPagingSource receives isPremium: Boolean; returns endOfPaginationReached=true on page 3 if not premium; AllTimeLeadersScreen appends PremiumIndicatorItem |
</phase_requirements>

---

## Summary

This phase integrates platform-specific in-app purchase subscriptions using the project's established expect/actual pattern. The architecture mirrors HttpClientFactory: a `commonMain` interface (`InAppPurchaseService`) is backed by `androidMain` (BillingClient 7.x) and `iosMain` (StoreKit 2 via cinterop) implementations. All three plans are independent work streams that converge at a Koin singleton.

The most technically complex part is the Android BillingClient integration: it requires an `Activity` reference to launch the billing flow (not just `Context`), and `launchBillingFlow` must be called from the main thread. This means the Android implementation needs a mechanism to receive an `Activity` reference — the established pattern is passing it via a `setActivity()` setter called from `MainActivity.onResume()`. On iOS, StoreKit 2's `Product.purchase()` can be called from any context in KMP iosMain Kotlin using `kotlinx.cinterop` to call StoreKit Swift/ObjC APIs directly.

The Paging3 premium gate is straightforward: `AllTimeLeadersPagingSource` receives a `Boolean` flag at construction time (not a Flow). The `AllTimeLeadersPagingSourceFactory` passes `isPremium` as a constructor argument, injected from `AllTimeLeadersViewModel` which collects the `StateFlow<Boolean>` from a new `ObservePremiumActive` use case. When premium=false and page>=3, the PagingSource returns `LoadResult.Page(data=emptyList(), prevKey=..., nextKey=null)` — this signals end of pagination, and the screen separately appends the premium indicator card as a final `item{}` in the LazyColumn.

**Primary recommendation:** Place `InAppPurchaseService` interface in `domain/service/`, implement in `data/androidMain/` and `data/iosMain/`, wire as Koin `single<>` in `dataModule`, then build PremiumViewModel and AllTimeLeadersViewModel changes in presentation.

---

## Standard Stack

### Core (existing project libraries — no new dependencies for domain/presentation)

| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Google Play Billing | 7.x → See note | Android subscription purchases | Official Google library |
| StoreKit 2 (cinterop) | iOS 15+ system | iOS subscription purchases | Apple system framework, no dep needed |
| Koin | 4.1.1 (existing) | DI wiring for InAppPurchaseService | Already in project |
| Paging3 | 3.4.0-beta01 (existing) | AllTimeLeaders pagination gate | Already in project |
| Compose Material3 | via CMP 1.9.3 | PremiumScreen UI | Already in project |
| Arrow Core | 2.2.0 (existing) | Either for error handling | Already in project |

### New Dependency Required

| Library | Version | Purpose | Where |
|---------|---------|---------|-------|
| billing-ktx | **7.1.1** (verified below) | BillingClient for Android IAP | `data/androidMain` only |

**Version note (CRITICAL):** The latest billing library is **8.3.0** (released 2025-12-23). However, Billing Library 8 introduced breaking changes:
- `enablePendingPurchases()` signature changed — now requires `PendingPurchasesParams`
- `ProductDetailsResponseListener.onProductDetailsResponse` signature changed
- `minSdkVersion` raised to 23 (project is already minSdk 24, so OK)
- Removed `queryPurchaseHistoryAsync`

The project minSdk is 24 (compatible with PBL 8). **Use Billing Library 7.1.1** for maximum stability and to match the Flutter app's existing integration. If the billing library is not yet in the project, you must add it to `data/build.gradle.kts` androidMain only.

**Verified:**
```
com.android.billingclient:billing-ktx:7.1.1  (stable)
com.android.billingclient:billing-ktx:8.3.0  (latest, released 2025-12-23)
```

Both are stable. Use 7.1.1 to match Flutter (no migration overhead). The project can upgrade to 8.x later.

**Installation (data/build.gradle.kts, androidMain block only):**
```kotlin
androidMain {
    dependencies {
        implementation("io.ktor:ktor-client-okhttp:3.3.3") // existing
        implementation("com.android.billingclient:billing-ktx:7.1.1")
    }
}
```

StoreKit 2 is an iOS system framework accessed via cinterop — zero Gradle dependency needed.

---

## Architecture Patterns

### Module Placement

The `InAppPurchaseService` interface lives in `domain/service/` (matches existing LeagueService, PlayerService etc.). Implementations live in `data/androidMain/` and `data/iosMain/`. A domain model `PremiumProduct` (data class: id, price, formattedPriceAndPeriod) carries the product info to the ViewModel.

```
domain/src/commonMain/
  service/
    InAppPurchaseService.kt          ← NEW interface
  model/
    PremiumProduct.kt                ← NEW data class

data/src/androidMain/
  service/
    InAppPurchaseService.android.kt  ← NEW actual (BillingClient)

data/src/iosMain/
  service/
    InAppPurchaseService.ios.kt      ← NEW actual (StoreKit 2)
```

### Pattern 1: InAppPurchaseService Interface

Domain defines the contract. Notice `premiumActiveFlow` is a `StateFlow<Boolean>` with `true` initial value (generous default per D-14), and `initialize()` is a suspend function that does the restore.

```kotlin
// domain/src/commonMain/.../service/InAppPurchaseService.kt
interface InAppPurchaseService {
    val premiumActiveFlow: StateFlow<Boolean>
    suspend fun initialize()   // called at startup; restores purchases; sets premiumActive
    suspend fun getProducts(): Either<Failure, List<PremiumProduct>>
    suspend fun buySubscription(productId: String): Either<Failure, Unit>
    fun manageSubscription()   // opens platform subscription management URL
}
```

**Why `StateFlow` not `Flow`:** The generous-default pattern (start as `true`, set `false` if not found after restore) requires an initial value, which is `StateFlow`'s purpose. ViewModels collect `.value` for one-shot reads and subscribe to changes.

### Pattern 2: Android Implementation (BillingClient)

Key constraints for the Android implementation:
1. BillingClient needs `Context` for construction — inject via Koin `androidContext()`
2. `launchBillingFlow()` requires an `Activity` reference, not just `Context`
3. The Activity reference must be set AFTER construction — use a setter pattern

```kotlin
// data/src/androidMain/.../service/InAppPurchaseService.android.kt
class AndroidInAppPurchaseService(
    private val context: Context  // injected via Koin androidContext()
) : InAppPurchaseService {

    private val _premiumActive = MutableStateFlow(true)  // generous default (D-14)
    override val premiumActiveFlow: StateFlow<Boolean> = _premiumActive.asStateFlow()

    private var activity: Activity? = null
    private var billingClient: BillingClient? = null
    private var cachedProductDetails: ProductDetails? = null

    fun setActivity(activity: Activity) {
        this.activity = activity
        // rebuild BillingClient if needed
    }

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            purchases?.forEach { p ->
                if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    handlePremiumPurchase(p)
                }
            }
        }
    }

    override suspend fun initialize() {
        // 1. Build BillingClient
        // 2. Connect
        // 3. queryPurchasesAsync for SUBS
        // 4. If premium found → _premiumActive.value = true; else → false
        // (stays true during loading per generous default)
    }

    override suspend fun getProducts(): Either<Failure, List<PremiumProduct>> {
        // queryProductDetailsAsync with ProductType.SUBS
        // Map basePlanId → price/period string per D-08
    }

    override suspend fun buySubscription(productId: String) {
        // Use cachedProductDetails + first offer token
        // billingClient.launchBillingFlow(activity!!, params)
    }

    override fun manageSubscription() {
        // Intent to Google Play subscriptions page
        // https://play.google.com/store/account/subscriptions
    }

    private fun handlePremiumPurchase(purchase: Purchase) {
        _premiumActive.value = true
        // acknowledge if not acknowledged
    }
}
```

**Activity injection:** `MainActivity` must call `(get<InAppPurchaseService>() as AndroidInAppPurchaseService).setActivity(this)` in `onResume()`. This is the standard approach for KMP services that need Activity. Alternatively, use `ActivityProvider` pattern with `Application.registerActivityLifecycleCallbacks`.

### Pattern 3: iOS Implementation (StoreKit 2 via cinterop)

StoreKit 2 is available in Kotlin iosMain as `platform.StoreKit.*` via automatic cinterop. No Swift wrapper needed — call StoreKit ObjC API directly.

```kotlin
// data/src/iosMain/.../service/InAppPurchaseService.ios.kt
@OptIn(ExperimentalForeignApi::class)
class IosInAppPurchaseService : InAppPurchaseService {

    private val _premiumActive = MutableStateFlow(true)  // generous default
    override val premiumActiveFlow: StateFlow<Boolean> = _premiumActive.asStateFlow()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override suspend fun initialize() {
        // Use StoreKit 2 Transaction.currentEntitlements
        // Iterate entitlements, check for premiumID
        // Set _premiumActive.value accordingly
        // Also start listening to Transaction.updates
    }

    override suspend fun getProducts(): Either<Failure, List<PremiumProduct>> {
        // platform.StoreKit.SKProduct or StoreKit 2 Product.products(for:)
        // Parse subscriptionPeriod for price/period per D-08
    }

    override suspend fun buySubscription(productId: String) {
        // product.purchase() via StoreKit 2
        // Handle Transaction result
    }

    override fun manageSubscription() {
        // platform.UIKit.UIApplication.sharedApplication
        //   .openURL("https://apps.apple.com/account/subscriptions")
    }
}
```

**StoreKit 2 Kotlin cinterop:** In iosMain Kotlin, call `platform.StoreKit.SKProduct` and Swift async APIs are available via callback-style wrappers (`suspendCancellableCoroutine`). StoreKit 2 `Transaction.currentEntitlements` is an async sequence — iterate with `for await` in a Swift helper, or use `withCheckedContinuation` pattern in Kotlin.

**Key StoreKit 2 APIs needed:**
- `platform.StoreKit.SKProductsRequest` (StoreKit 1 approach, simpler for query)
- Or use async `Product.products(for:)` via `kotlinx.coroutines` suspend bridge

Given complexity of bridging Swift async to Kotlin, use `SKProductsRequest` (StoreKit 1 API) for product fetch and StoreKit 2 for purchase verification. StoreKit 1 APIs remain available alongside StoreKit 2 on iOS 15+.

### Pattern 4: PremiumViewModel

```kotlin
class PremiumViewModel(
    private val getProducts: GetPremiumProducts,
    private val buySubscription: BuySubscription,
    private val observePremiumActive: ObservePremiumActive,
) : ViewModel() {

    private val _viewState = MutableStateFlow(PremiumViewState())
    val viewState: StateFlow<PremiumViewState> = _viewState.asStateFlow()

    init {
        loadProducts()
        observePremium()
    }

    private fun observePremium() {
        viewModelScope.launch {
            observePremiumActive().collect { isPremium ->
                _viewState.update { it.copy(isPremiumActive = isPremium) }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true) }
            getProducts().onSuspendSuccess { products ->
                _viewState.update { it.copy(
                    product = products.firstOrNull(),
                    isLoading = false
                ) }
            }.onSuspendGeneralError {
                _viewState.update { it.copy(error = it.error, isLoading = false) }
            }
        }
    }

    fun onBuyClick() {
        val productId = _viewState.value.product?.id ?: return
        viewModelScope.launch {
            buySubscription(productId)
            // premium state update comes via StateFlow observation
        }
    }
}

data class PremiumViewState(
    val product: PremiumProduct? = null,
    val isPremiumActive: Boolean = true,  // generous default
    val isLoading: Boolean = true,
    val error: Failure? = null,
)
```

### Pattern 5: AllTimeLeaders Premium Gate

The paging gate uses constructor injection (not flow collection inside PagingSource) to keep the pattern simple and testable.

**AllTimeLeadersPagingSource** — add `isPremium: Boolean` constructor arg:
```kotlin
class AllTimeLeadersPagingSource(
    private val pageSize: Int,
    private val statOption: AllTimeStatLeaderOption,
    private val isPremium: Boolean,          // NEW
    private val getAllTimeLeaders: GetAllTimeLeaders
) : BasePagingSource<AllTimeLeader>() {

    override suspend fun fetchData(page: Int): Either<Throwable, PageableData<AllTimeLeader>> {
        if (!isPremium && page >= 3) {
            // Return empty page with no next key = end of pagination
            return Either.Right(PageableData(data = emptyList(), next = null))
        }
        return getAllTimeLeaders.invoke(...)
    }
}
```

**AllTimeLeadersPagingSourceFactory** — add `isPremium`:
```kotlin
interface BaseAllTimeLeadersPagingSourceFactory {
    fun create(pageSize: Int, statOption: AllTimeStatLeaderOption, isPremium: Boolean): BasePagingSource<AllTimeLeader>
}
```

**AllTimeLeadersViewModel** — collect premium state, pass to factory:
```kotlin
class AllTimeLeadersViewModel(
    private val allTimeLeadersPagingSourceFactory: BaseAllTimeLeadersPagingSourceFactory,
    private val observePremiumActive: ObservePremiumActive,  // NEW
) : ViewModel() {

    private val isPremiumFlow: StateFlow<Boolean> by lazy {
        observePremiumActive().stateIn(viewModelScope, SharingStarted.Eagerly, true)
    }

    val pagingFlow: Flow<PagingData<AllTimeLeader>> by lazy {
        combine(statOptionFlow, isPremiumFlow) { statOption, isPremium ->
            Pair(statOption, isPremium)
        }
        .distinctUntilChanged()
        .flatMapLatest { (statOption, isPremium) ->
            Pager(...) {
                allTimeLeadersPagingSourceFactory.create(PAGE_SIZE, statOption, isPremium)
                    .also { pagingSource = it }
            }.flow
        }.cachedIn(viewModelScope)
    }
}
```

**AllTimeLeadersScreen** — add premium indicator item at end of LazyColumn:
```kotlin
// In LazyColumn, after regular items:
if (!viewState.isPremiumActive && leadersPagingItems.loadState.append.endOfPaginationReached) {
    item {
        PremiumIndicatorCard(onUpgradeToPremiumClick = onNavigateToPremium)
    }
}
```

The `viewState.isPremiumActive` needs to be added to `AllTimeLeadersViewState`. The ViewModel must expose it.

### Pattern 6: Koin DI Wiring

**DataModule changes:**
```kotlin
// data/src/commonMain/.../di/DataModule.kt
// Add:
single<InAppPurchaseService> { InAppPurchaseServiceImpl(get()) }  // platformContext via expect/actual

// OR for Android: inject androidContext()
// Android actual constructor takes Context (Koin provides via androidContext())
```

**Challenge:** `InAppPurchaseService` is expect/actual (data layer), but Koin module is in `commonMain`. This is the same situation as `HttpClientFactory` — the Koin module references the class, which is an expect/actual declaration. The actual class on Android takes `Context`. Use the pattern from `HttpClientFactory`:

```kotlin
// In DataModule (commonMain) — this works because actual class is resolved at compile time:
single<InAppPurchaseService> { InAppPurchaseServiceImpl(androidContext()) }
```

But `androidContext()` is Android-only. The right approach is to use `expect/actual` for the factory method or use Koin's platform-specific module. Inspect how `TournamentProvider` (no platform dep) differs from `HttpClientFactory` (uses `platformEngine()` expect/actual function).

The cleanest approach: define `expect fun createInAppPurchaseService(context: Any?): InAppPurchaseService` with `actual` implementations. The Android actual takes `Context`, iOS actual ignores the argument.

Alternatively: put the Koin binding in platform-specific modules. Since there's only `dataModule` (commonMain), use the existing `HttpClientFactory` pattern — the factory function uses `platformEngine()` expect/actual. For InAppPurchaseService, a similar `expect fun createInAppPurchaseService(): InAppPurchaseService` pattern (with Android actual injecting its own Context via `androidContext()` inside the factory) keeps everything in one module.

**PresentationModule changes:**
```kotlin
// Add use cases:
single<GetPremiumProducts> { GetPremiumProductsUseCase(get()) }
single<BuySubscription> { BuySubscriptionUseCase(get()) }
single<ObservePremiumActive> { ObservePremiumActiveUseCase(get()) }

// Add ViewModel:
viewModelOf(::PremiumViewModel)

// Update AllTimeLeadersViewModel (now has extra dep):
viewModelOf(::AllTimeLeadersViewModel)  // if using viewModelOf, OK (auto-resolves)
```

**Eager initialization (D-16):** InAppPurchaseService is a `single<>`. It initializes lazily by default. To make it eager, either:
1. Call `get<InAppPurchaseService>()` in `initKoin` block (force resolve at startup)
2. Or override Koin's eager singleton: `single(createdAtStart = true) { ... }`

Use `createdAtStart = true` on the singleton binding.

### Anti-Patterns to Avoid

- **Calling `launchBillingFlow` without an Activity:** BillingClient requires an Activity reference (not Application Context). The service needs to receive it before purchase is triggered.
- **Collecting premium StateFlow inside PagingSource:** PagingSource is recreated on each `flatMapLatest` emission. The premium status should be baked into constructor args so the factory creates a fresh PagingSource with the current value.
- **Starting Transaction.updates listener multiple times:** On iOS, `Transaction.updates` listener should be started once in `initialize()`. On Android, `PurchasesUpdatedListener` must be registered in BillingClient builder, before connection.
- **Not acknowledging Android purchases:** Subscriptions not acknowledged within 3 days are automatically refunded by Google Play. Always acknowledge after confirming `purchaseState == PURCHASED`.
- **Not calling `finishTransaction` on iOS:** StoreKit 2 automatically finishes transactions, but with StoreKit 1 APIs (used for product query) explicit finish is needed. If mixing StoreKit 1/2, ensure transactions are finished.

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Product query | Custom HTTP API call | BillingClient.queryProductDetailsAsync / SKProductsRequest | Platform APIs return authoritative price/currency/period |
| Price formatting | Manual string format | ProductDetails.formattedPrice (Android) / SKProduct.localizedPrice (iOS) | Locale-aware, auto-currency |
| Subscription period parsing | Regex on ISO 8601 period | basePlanId mapping (Android) / SKProduct.subscriptionPeriod (iOS) | Already validated by Flutter source |
| Purchase acknowledgment | Skip acknowledgment | Always call AcknowledgePurchaseParams (Android) | Google Play auto-refunds unacknowledged within 3 days |
| Deep link to subscriptions | Custom intent | Google Play URL: `https://play.google.com/store/account/subscriptions` / App Store: `https://apps.apple.com/account/subscriptions` | Standard platform URLs, no special API needed |

**Key insight:** IAP flows are tightly coupled to platform UX. Any custom intermediate layer between the app and the platform SDK introduces state synchronization bugs that are hard to reproduce.

---

## Common Pitfalls

### Pitfall 1: Activity Reference for launchBillingFlow
**What goes wrong:** `BillingClient.launchBillingFlow()` throws if called with `null` or Application Context. App crashes or silently fails to open the purchase sheet.
**Why it happens:** Android requires the purchase sheet to be presented over a live Activity. `BillingClient` validates the Activity reference internally.
**How to avoid:** Pass Activity via a setter from `MainActivity.onResume()`. Set `activity = null` in `onPause()` to avoid memory leaks.
**Warning signs:** `BillingResponseCode.DEVELOPER_ERROR` in the purchase update listener.

### Pitfall 2: Generous Default Race Condition
**What goes wrong:** If InAppPurchaseService isn't initialized before `AllTimeLeaders` is opened, the paging source receives `isPremium=true` and loads page 3+. After restore completes and `isPremium=false` is emitted, Pager re-creates — but Compose has already cached the data. User sees all 100 leaders briefly then the list truncates.
**Why it happens:** `cachedIn(viewModelScope)` caches PagingData. When `isPremiumFlow` changes, `flatMapLatest` cancels the old Pager and creates a new one — this triggers a full reload with correct gating.
**How to avoid:** Eager init (D-16) with `createdAtStart = true` ensures initialize() runs before any ViewModel collects from premiumActiveFlow. This minimizes the window for the race.
**Warning signs:** AllTimeLeaders shows more than 2 pages on first load for non-premium, then reloads.

### Pitfall 3: iOS StoreKit Coroutine Context
**What goes wrong:** StoreKit 2 callbacks must be dispatched on the main thread on iOS. Calling `_premiumActive.value = true` from a background cinterop callback crashes with `NSException: Main thread checker violation`.
**Why it happens:** KMP iOS coroutines default to a background dispatcher for network-like operations.
**How to avoid:** Use `withContext(Dispatchers.Main)` when updating StateFlow from cinterop callbacks. Or ensure the entire initialize() runs on `Dispatchers.Main`.
**Warning signs:** Intermittent crashes on iOS after purchase.

### Pitfall 4: Paging3 endOfPaginationReached Signal
**What goes wrong:** When PagingSource returns `LoadResult.Page(data=emptyList(), nextKey=null)`, Paging3 considers pagination complete. BUT if the screen also checks `leadersPagingItems.loadState.append.endOfPaginationReached` to show the premium card, there can be a single-frame flash where neither the last leader items NOR the premium card are shown.
**Why it happens:** The `endOfPaginationReached` state is set asynchronously after the page is loaded.
**How to avoid:** Combine the premium indicator condition with a check against `leadersPagingItems.itemCount > 0` to ensure data is loaded first.
**Warning signs:** Premium indicator flashes in/out on first load.

### Pitfall 5: Billing Library 8 Breaking Changes
**What goes wrong:** Using Billing Library 8.x requires updating `enablePendingPurchases()` call signature — the parameterless version is removed. `queryProductDetailsAsync` listener signature also changed.
**Why it happens:** PBL 8 was a major breaking release (2025-06-30).
**How to avoid:** Use Billing Library 7.1.1 to match the Flutter app's existing store configuration. Upgrade to 8.x in a dedicated migration task.
**Warning signs:** `UnsatisfiedLinkError` or compile errors mentioning `enablePendingPurchases()`.

---

## Code Examples

### Android BillingClient — Initialize and Restore

```kotlin
// Source: https://developer.android.com/google/play/billing/integrate
billingClient = BillingClient.newBuilder(context)
    .setListener(purchasesUpdatedListener)
    .enablePendingPurchases()  // PBL 7.x syntax
    .build()

billingClient.startConnection(object : BillingClientStateListener {
    override fun onBillingSetupFinished(result: BillingResult) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            coroutineScope.launch { restorePurchases() }
        }
    }
    override fun onBillingServiceDisconnected() { /* reconnect if needed */ }
})

private suspend fun restorePurchases() {
    val params = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.SUBS)
        .build()
    val result = withContext(Dispatchers.IO) {
        billingClient.queryPurchasesAsync(params)
    }
    val hasPremium = result.purchasesList.any { p ->
        p.products.contains(PREMIUM_PRODUCT_ID) &&
        p.purchaseState == Purchase.PurchaseState.PURCHASED
    }
    _premiumActive.value = hasPremium
}
```

### Android BillingClient — Query Products

```kotlin
// Source: https://developer.android.com/google/play/billing/integrate
val productList = listOf(
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId(PREMIUM_PRODUCT_ID)
        .setProductType(BillingClient.ProductType.SUBS)
        .build()
)
val params = QueryProductDetailsParams.newBuilder()
    .setProductList(productList).build()

billingClient.queryProductDetailsAsync(params) { result, details ->
    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
        // details: List<ProductDetails>
        val product = details.firstOrNull()
        // basePlanId from subscriptionOfferDetails[subscriptionIndex].basePlanId
    }
}
```

### Android BillingClient — Launch Purchase Flow

```kotlin
// Source: https://developer.android.com/google/play/billing/integrate
val offerToken = productDetails.subscriptionOfferDetails
    ?.firstOrNull()?.offerToken ?: return

val flowParams = BillingFlowParams.newBuilder()
    .setProductDetailsParamsList(listOf(
        BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .setOfferToken(offerToken)
            .build()
    ))
    .build()

billingClient.launchBillingFlow(activity, flowParams)  // must be Activity
```

### Android — Price/Period Display (matching Flutter priceAndPeriod())

```kotlin
// Based on Flutter source analysis (payments_screen.dart)
fun ProductDetails.priceAndPeriod(): String {
    val offer = subscriptionOfferDetails?.firstOrNull() ?: return ""
    val pricingPhase = offer.pricingPhases.pricingPhaseList.lastOrNull() ?: return ""
    val price = pricingPhase.formattedPrice
    val period = when (offer.basePlanId) {
        "premium-1-month"  -> "1 month"
        "premium-3-months" -> "3 months"
        "premium-6-months" -> "6 months"
        "premium-1-year"   -> "1 year"
        else               -> "6 months"
    }
    return "$price / $period"
}
```

### iOS — Open Subscription Management URL

```kotlin
// iosMain Kotlin — LocalUriHandler not available from service layer
// Use UIApplication directly via cinterop
import platform.UIKit.UIApplication
import platform.Foundation.NSURL

fun openSubscriptionManagement() {
    val url = NSURL(string = "https://apps.apple.com/account/subscriptions")
    UIApplication.sharedApplication.openURL(url!!)
}
```

### PremiumScreen — Subscribe/Active Card State

```kotlin
// Based on Flutter payments_screen.dart migration
@Composable
fun PremiumScreen(
    viewModel: PremiumViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = { ActionBar(title = ..., showBackButton = true, onBack = onNavigateBack) }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
                .background(BasketKrkColors.DefaultBackground)
        ) {
            when {
                viewState.isLoading -> FullScreenLoader()
                viewState.error != null -> ErrorView(retryAction = viewModel::onRetry)
                else -> PremiumContent(
                    viewState = viewState,
                    onSubscribeClick = viewModel::onBuyClick,
                    onManageClick = { uriHandler.openUri(viewState.subscriptionManagementUrl) },
                )
            }
        }
    }
}
```

### AllTimeLeaders Premium Indicator Card

```kotlin
// Based on Flutter custom_buy_premium_indicator.dart migration
@Composable
fun PremiumIndicatorCard(onUpgradeClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF8E1)  // amber.shade50
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.LockOpen, contentDescription = null,
                 modifier = Modifier.size(40.dp), tint = Color(0xFFE65100))
            Spacer(Modifier.height(12.dp))
            Text(stringResource(Res.string.pagination_premium_item_title),
                 fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(Res.string.pagination_premium_item_description),
                 textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onUpgradeClick,
                   colors = ButtonDefaults.buttonColors(
                       containerColor = Color(0xFFE65100))) {
                Icon(Icons.Default.Star, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.pagination_premium_item_button))
            }
        }
    }
}
```

---

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| `SkuDetails` / `querySkuDetailsAsync` | `ProductDetails` / `queryProductDetailsAsync` | PBL 5 → 6 (2022) | Old API removed in PBL 8 |
| `enablePendingPurchases()` (no args) | `enablePendingPurchases(PendingPurchasesParams)` | PBL 7 → 8 (2025) | Compile error in PBL 8 |
| `BillingClient.BillingResponseCode.OK` | Same constant still valid | — | No change |
| StoreKit 1 `SKPaymentQueue` | StoreKit 2 `Transaction.updates` + `Product.purchase()` | iOS 15 / 2021 | Simpler async API, but Kotlin cinterop is more complex |
| No acknowledgment tracking | Must acknowledge subs within 3 days | PBL 4+ | Auto-refund risk if skipped |

**Deprecated/outdated:**
- `querySkuDetailsAsync`: Removed in PBL 8. Use `queryProductDetailsAsync`.
- StoreKit 1 `SKPaymentQueue`: Still functional on iOS 15+ but Apple recommends StoreKit 2 for new implementations.

---

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Google Play Billing | Android IAP | Must add | — (not yet in project) | — |
| StoreKit | iOS IAP | System framework | iOS 15+ (system) | — |
| Koin | DI | ✓ | 4.1.1 | — |
| Paging3 | AllTimeLeaders gate | ✓ | 3.4.0-beta01 | — |

**Missing dependencies with no fallback:**
- `com.android.billingclient:billing-ktx:7.1.1` — must be added to `data/build.gradle.kts` androidMain block before Android implementation can compile.

**Note on testing IAP:**
- Android: Requires a device/emulator with Google Play Services. License testing can be done with a license test email in Play Console. Billing does NOT work in a plain emulator without Google Play.
- iOS: Requires a real device or StoreKit testing sandbox in Xcode. StoreKit Configuration files can simulate products in the Xcode simulator.

---

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | kotlin-test (via `libs.kotlin.test`) |
| Config file | none — inline in Gradle commonTest |
| Quick run command | `./gradlew :data:testDebugUnitTest` |
| Full suite command | `./gradlew test` |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| PREM-01 | Premium screen shows product price and period | manual-only | — | N/A |
| PREM-02 | Purchase flow opens Google Play / App Store sheet | manual-only | — | N/A |
| PREM-03 | Active premium shows green check card | manual-only | — | N/A |
| PREM-04 | Manage subscription opens correct URL | manual-only | — | N/A |
| PREM-05 | StateFlow defaults to true, updates after restore | unit | `./gradlew :data:testDebugUnitTest` | ❌ Wave 0 |
| PREM-06 | PagingSource blocks page 3+ when isPremium=false | unit | `./gradlew :presentation:testDebugUnitTest` | ❌ Wave 0 |

**Why PREM-01 through PREM-04 are manual-only:** These require live Google Play / App Store billing environments. Sandbox testing requires developer console configuration outside the codebase. No meaningful unit test can be written for the platform billing flow.

**PREM-05 and PREM-06 are automatable** — they only test in-memory state logic:
- PREM-05: Construct `InAppPurchaseService` with a fake purchase list; verify `premiumActiveFlow` emits `false` after `initialize()`.
- PREM-06: Construct `AllTimeLeadersPagingSource(isPremium=false, page=3)`, verify `fetchData(3)` returns `PageableData(data=emptyList(), next=null)`.

### Sampling Rate
- **Per task commit:** No automated test runs required (manual-only behaviors dominate)
- **Per wave merge:** `./gradlew :data:testDebugUnitTest :presentation:testDebugUnitTest`
- **Phase gate:** Manual smoke test on real device (Android) + simulator (iOS) before `/gsd:verify-work`

### Wave 0 Gaps
- [ ] `data/src/commonTest/.../service/InAppPurchaseServiceTest.kt` — covers PREM-05 (premium state defaults and restore)
- [ ] `presentation/src/commonTest/.../pagination/AllTimeLeadersPagingSourceTest.kt` — covers PREM-06 (page gate logic)

---

## Open Questions

1. **Activity injection pattern for BillingClient**
   - What we know: BillingClient requires an Activity. KoinHelper is in the `shared` module. `MainActivity` is in `composeApp/androidMain`.
   - What's unclear: The cleanest way to wire the Activity into `AndroidInAppPurchaseService` without creating a circular dependency between `data` and `composeApp`.
   - Recommendation: Use `Application.registerActivityLifecycleCallbacks` in `BasketKrkApplication` to forward Activity reference to the service. This keeps `data` independent of `composeApp`. Alternatively, a Koin `single<ActivityHolder>` that MainActivity writes into.

2. **StoreKit 2 vs StoreKit 1 for iOS product query**
   - What we know: StoreKit 2 `Product.products(for:)` requires Swift async/await which doesn't bridge trivially to KMP Kotlin coroutines via cinterop.
   - What's unclear: Whether StoreKit 1 `SKProductsRequest` is simpler to bridge, or if using `suspendCancellableCoroutine` with a custom `SKProductsRequestDelegate` is more maintainable.
   - Recommendation: Use StoreKit 1 `SKProductsRequest` for product query (delegate pattern maps cleanly to Kotlin continuation). Use StoreKit 2 `Transaction.currentEntitlements` for purchase verification (simpler than StoreKit 1 receipt validation).

3. **Eager initialization ordering vs Koin lazy resolution**
   - What we know: `createdAtStart = true` in Koin forces the singleton to be created when the module starts. But on Android, `BillingClient` needs a `Context` — available during Koin init.
   - What's unclear: Whether eager creation on iOS causes any issues if `UIApplication` isn't ready yet.
   - Recommendation: Use `createdAtStart = true` and test on iOS to verify no startup crash. Alternative: call `get<InAppPurchaseService>()` in `App.kt` composable's `LaunchedEffect(Unit)`.

---

## Sources

### Primary (HIGH confidence)
- Android Developers — Google Play Billing Library release notes: https://developer.android.com/google/play/billing/release-notes — version 8.3.0 confirmed current, 7.1.1 confirmed stable
- Android Developers — Integrate Billing Library: https://developer.android.com/google/play/billing/integrate — BillingClient API, queryProductDetailsAsync, launchBillingFlow, acknowledgePurchase patterns
- Android Developers — Migration Guide PBL 8: https://developer.android.com/google/play/billing/migrate-gpblv8 — breaking changes in PBL 8
- Flutter source (canonical reference from CONTEXT.md): `in_app_purchase_service.dart`, `payments_screen.dart`, `payments_bloc.dart`, `custom_buy_premium_indicator.dart` — migration reference for all feature behaviors
- Existing KMP codebase: `HttpClientFactory` expect/actual pattern, `BasePagingSource`, `AllTimeLeadersViewModel`, `PresentationModule.kt`, `DataModule.kt` — architecture validation

### Secondary (MEDIUM confidence)
- GitHub: Aditya-gupta99/In-app-purchase-kmp — iosMain IAPManager.ios.kt and androidMain IAPManager.android.kt — KMP IAP pattern reference (uses Swift wrapper, but shows cinterop approach)
- Apple Developer Documentation: StoreKit 2 `Transaction.currentEntitlements`, `Transaction.updates` — iOS purchase verification APIs

### Tertiary (LOW confidence)
- RevenueCat blog on PBL 8 changes — supporting documentation on breaking changes

---

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — billing library verified on Google Maven; existing project deps verified in codebase
- Architecture: HIGH — patterns derived directly from existing codebase (HttpClientFactory, PagingSource, DataModule) + official docs
- Pitfalls: MEDIUM — Activity injection pattern and StoreKit coroutine context are practical issues from comparable KMP IAP projects
- iOS StoreKit cinterop details: MEDIUM — StoreKit 1/2 cinterop confirmed feasible from reference library; exact API bridge pattern is discretionary

**Research date:** 2026-03-24
**Valid until:** 2026-06-24 (stable domain; billing library versions update frequently — verify before implementation)
