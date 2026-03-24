---
phase: 08-premium
plan: "02"
subsystem: premium-screen-ui
tags: [premium, compose, viewmodel, koin, iap, subscription]
dependency_graph:
  requires: [InAppPurchaseService, GetPremiumProducts, BuySubscription, ObservePremiumActive, PremiumProduct]
  provides: [PremiumScreen, PremiumViewModel, PremiumViewState]
  affects: [App.kt nav graph, PresentationModule]
tech_stack:
  added: []
  patterns: [MVVM+StateFlow, koinViewModel, ClickableText annotated string, generous-default isPremiumActive=true]
key_files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/premium/PremiumViewModel.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/premium/PremiumScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt
decisions:
  - "PremiumViewState.isPremiumActive defaults to true (generous default per D-14) — user sees active state on first load before IAP status resolves"
  - "subscriptionManagementUrl computed property on PremiumViewState maps Platform enum to platform-specific subscription management URL"
  - "LegalText handles linkText-not-found gracefully (renders plain text fallback) — defensive guard for string resource mismatches"
metrics:
  duration: "2 min"
  completed: "2026-03-24"
  tasks_completed: 2
  files_created: 1
  files_modified: 3
---

# Phase 08 Plan 02: Premium Screen UI Summary

**One-liner:** Full PremiumScreen replacing "Coming soon" placeholder with subscribe/active subscription card, price/period display, and platform-specific legal text with clickable subscription management link.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | PremiumViewModel with product loading, premium observation, and purchase action | 8d97a49 | PremiumViewModel.kt created, PresentationModule.kt modified |
| 2 | PremiumScreen UI — replace placeholder with subscribe/active card and legal text | 5148b14 | PremiumScreen.kt rewritten, App.kt updated |

## What Was Built

### PremiumViewModel (Task 1)

**`PremiumViewModel`** wiring four use cases:
- `GetPremiumProducts` — suspended product catalog call on init
- `BuySubscription` — suspended purchase call in `onBuyClick()`
- `ObservePremiumActive` — collects `StateFlow<Boolean>` to update `isPremiumActive`
- `GetPlatform` — synchronous platform detection for URL routing

**`PremiumViewState`** with:
- `product: PremiumProduct?` — first product from catalog (one subscription SKU expected)
- `isPremiumActive: Boolean = true` — generous default per D-14
- `isLoading: Boolean = true` — spinner shown during product fetch
- `error: Failure?` — exposes IAP fetch errors
- `platform: Platform = Platform.ANDROID` — overridden on init
- `subscriptionManagementUrl: String` — computed from platform enum

DI registration: `viewModelOf(::PremiumViewModel)` appended to `PresentationModule.kt`.

### PremiumScreen (Task 2)

Three-state Scaffold content:
1. **Loading** — `FullScreenLoader()` while products fetch
2. **Error** — `ErrorView(error, retryAction = viewModel::onRetry)` with retry
3. **Content** — `PremiumContent` with scrollable Column layout

**`SubscriptionCard`** composable handles two branches:
- Not subscribed: Premium Account title + description + `formattedPriceAndPeriod` + Subscribe ElevatedButton (white/MainDark colors)
- Subscribed: `Icons.Default.CheckCircle` in green (0xFF4CAF50) + "Subscription Active" label + premium_active_label text + Manage Subscription OutlinedButton (opens platform URL)

**`LegalText`** composable:
- Platform-conditional (ANDROID/IOS) legal text and link text from string resources
- `buildAnnotatedString` with `SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)` for clickable link
- Falls back to plain `Text` if link text not found in full text

**App.kt** updated: `composable<Screen.Premium>` now passes `viewModel = koinViewModel<PremiumViewModel>()` explicitly.

## Deviations from Plan

None — plan executed exactly as written.

## Known Stubs

None — all data flows are wired. Product data comes from `GetPremiumProducts` use case (backed by platform IAP implementation from Plan 01). Premium active state flows from `ObservePremiumActive`. No placeholder values reach the UI.

## Self-Check: PASSED
