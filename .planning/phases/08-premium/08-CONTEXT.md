# Phase 8: Premium - Context

**Gathered:** 2026-03-24
**Status:** Ready for planning

<domain>
## Phase Boundary

Integrate platform-specific in-app purchase subscriptions (BillingClient on Android, StoreKit 2 on iOS), build the full Premium screen replacing the current placeholder, and gate All-Time Leaders pagination at page 3+ for non-premium users. This is a 1:1 Flutter migration of the premium/payments feature.

This phase does NOT include: premium upsell dialog (premium_dialog_* strings), ad banner integration, or server-side receipt validation.

</domain>

<decisions>
## Implementation Decisions

### IAP library approach
- **D-01:** Use expect/actual with native APIs — BillingClient on Android, StoreKit 2 on iOS. No third-party wrapper or RevenueCat.
- **D-02:** Domain layer defines InAppPurchaseService interface. Data layer provides expect/actual platform implementations (androidMain/iosMain). Matches existing architecture (e.g., HttpClientFactory pattern).
- **D-03:** Single product ID `'premium'` (non-consumable subscription), same as Flutter.
- **D-04:** Local-only receipt validation — trust BillingClient/StoreKit purchase status directly. No server-side validation.
- **D-05:** Subscription type (recurring), matching Flutter's buyNonConsumable + subscription period display.
- **D-06:** StoreKit 2 for iOS (modern async/await API, requires iOS 15+).

### Premium screen layout
- **D-07:** 1:1 Flutter match — subscription card with two states (subscribe vs active), price/period display, platform-specific legal text at bottom.
- **D-08:** Price/period display using platform-specific extraction — Android: basePlanId mapping, iOS: subscriptionPeriod parsing. Matches Flutter's priceAndPeriod() extension.
- **D-09:** All string resources already exist in strings.xml (EN + PL) — no additions needed. Uses: premium_account, subscribe_button, subscribe_active_button, manage_subscription_button, premium_active_label, premium_description, premium_*_legal_info, premium_*_page, premium_delay_explanation, pagination_premium_item_*.
- **D-10:** Premium upsell dialog (premium_dialog_* strings) is DEFERRED — not part of this phase.

### All-Time Leaders gating
- **D-11:** PagingSource intercept — AllTimeLeadersPagingSource checks premium status before loading page 3+. If not premium, returns endOfPaginationReached=true and screen appends a premium indicator item.
- **D-12:** Premium indicator card matches Flutter's CustomBuyPremiumIndicator — lock icon, "Want to see TOP 100?", "Upgrade to Premium" button.
- **D-13:** Tapping "Upgrade to Premium" navigates to Screen.Premium (same as MoreScreen's "Buy Premium" item).

### Premium state management
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

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (migration reference)
- `~/Documents/Development/flutter/basket_krk/lib/services/in_app_purchase_service.dart` — InAppPurchaseService with premiumActiveStream, purchase handling, restore logic, product query
- `~/Documents/Development/flutter/basket_krk/lib/presentation/payments/payments_screen.dart` — PaymentsScreen UI with subscribe/active card states, price/period display, platform-specific legal text
- `~/Documents/Development/flutter/basket_krk/lib/presentation/payments/payments_bloc.dart` — PaymentsBloc with fetch products, buy subscription, observe premium state
- `~/Documents/Development/flutter/basket_krk/lib/presentation/payments/payments_state.dart` — PaymentsState/PaymentsEvent definitions
- `~/Documents/Development/flutter/basket_krk/lib/presentation/payments/payments_view_state.dart` — PaymentsViewState with premiumDetails, isPremiumActive, fullScreenLoading
- `~/Documents/Development/flutter/basket_krk/lib/presentation/views/custom_buy_premium_indicator.dart` — Premium gating indicator card for All-Time Leaders
- `~/Documents/Development/flutter/basket_krk/lib/presentation/stats/all_time/all_time_stats_screen.dart` — AllTimeStatsScreen with premiumIndicatorVisible integration
- `~/Documents/Development/flutter/basket_krk/lib/presentation/stats/all_time/all_time_stats_view_state.dart` — AllTimeStatsViewState with premiumIndicatorVisible flag
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/buy_subscription_usecase.dart` — BuySubscription use case
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/get_possible_purchases_usecase.dart` — GetPossiblePurchases use case
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/is_premium_active_usecase.dart` — IsPremiumActive use case
- `~/Documents/Development/flutter/basket_krk/lib/domain/usecases/observe_premium_active_change_usecase.dart` — ObservePremiumActiveChange use case

### Existing KMP components (modify these)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/premium/PremiumScreen.kt` — Current placeholder ("Coming soon") to be replaced with full UI
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersViewModel.kt` — Needs premium state integration
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersScreen.kt` — Needs premium indicator item
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/pagination/AllTimeLeadersPagingSource.kt` — Needs page 3+ gating
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/pagination/AllTimeLeadersPagingSourceFactory.kt` — Needs premium state parameter
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` — Screen.Premium route already wired
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` — Screen.Premium already defined
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt` — Register new ViewModels and use cases

### String resources (already complete)
- `presentation/src/commonMain/composeResources/values/strings.xml` — All premium/subscription/pagination strings (EN)
- `presentation/src/commonMain/composeResources/values-pl/strings.xml` — All premium/subscription/pagination strings (PL)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `PremiumScreen` placeholder — replace content, keep ActionBar + Scaffold structure
- `Screen.Premium` route — already registered in navigation graph
- `ActionBar` composable — back button + title, already used by PremiumScreen placeholder
- `BasketKrkColors` — app color palette
- `GetPlatform` use case — available for platform-specific legal text (Google Play vs App Store)
- `BasePagingSource` — base class for paging sources, used by AllTimeLeadersPagingSource
- All premium string resources — fully localized EN + PL

### Established Patterns
- MVVM + StateFlow for ViewModels
- Koin DI: `viewModelOf()`, `single<Interface> { Impl }`, `factoryOf()`
- Either<Failure, T> for error handling (Arrow)
- expect/actual for platform-specific code (exists in data layer for HTTP engines)
- Paging3 with PagingSource + Pager + cachedIn(viewModelScope)
- `onSuspendSuccess` / `onSuspendGeneralError` extension functions

### Integration Points
- `DataModule.kt` — register InAppPurchaseService, premium repository, use cases
- `PresentationModule.kt` — register PremiumViewModel
- `AllTimeLeadersViewModel` — inject premium state observation
- `AllTimeLeadersPagingSourceFactory` — pass premium status to PagingSource
- `AllTimeLeadersScreen` — render premium indicator item at end of list
- `App.kt` composable<Screen.Premium> — already wired, replace PremiumScreen content

</code_context>

<specifics>
## Specific Ideas

- 1:1 Flutter migration — match same product ID ('premium'), same UI states, same gating behavior
- Generous default: premium=true until purchases restored (Flutter behavior)
- StoreKit 2 for iOS (modern API), BillingClient for Android
- PagingSource-level gating (not UI-level) — stop fetching at page 3 for non-premium
- Eager InAppPurchaseService initialization — premium state ready before any screen needs it
- Legal text at bottom of Premium screen with clickable platform-specific subscription management link

</specifics>

<deferred>
## Deferred Ideas

- **Premium upsell dialog** — premium_dialog_* strings exist but dialog implementation deferred to future enhancement
- **Debug/release AdMob APPLICATION_ID variant** — user wants different AdMob keys per build type, belongs in ads/monetization phase
- **Ad banner integration** — Out of scope per PROJECT.md, separate monetization phase

</deferred>

---

*Phase: 08-premium*
*Context gathered: 2026-03-24*
