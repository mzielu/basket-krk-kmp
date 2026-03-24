---
phase: 08-premium
plan: "03"
subsystem: alltimeleaders-premium-gate
tags: [premium, paging, pagination, compose, monetization]
dependency_graph:
  requires: [InAppPurchaseService, ObservePremiumActive]
  provides: [AllTimeLeadersPremiumGate, PremiumIndicatorCard]
  affects: [AllTimeLeadersPagingSource, AllTimeLeadersPagingSourceFactory, AllTimeLeadersViewModel, AllTimeLeadersScreen, App]
tech_stack:
  added: []
  patterns: [PagingSource intercept at page >= 3, StateFlow combine for reactive Pager recreation, generous default isPremiumActive=true, endOfPaginationReached conditional UI item]
key_files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/components/PremiumIndicatorCard.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/pagination/AllTimeLeadersPagingSource.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/pagination/AllTimeLeadersPagingSourceFactory.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersViewModel.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt
decisions:
  - "PagingSource intercept at fetchData page >= 3 returns PageableData.empty() (next=null) to naturally signal end-of-pagination without exposing premium logic to BasePagingSource"
  - "isPremiumFlow initialized with SharingStarted.Eagerly and default true (generous default D-14) so paging starts unrestricted before first IAP check completes"
  - "combine(statOptionFlow, isPremiumFlow) + distinctUntilChanged recreates Pager when either filter or premium state changes — ensures correct gate when user buys premium mid-session"
  - "PremiumIndicatorCard shown only when !isPremiumActive && endOfPaginationReached && itemCount > 0 — avoids showing card before any data is loaded"
metrics:
  duration: "8 min"
  completed: "2026-03-24"
  tasks_completed: 2
  files_created: 1
  files_modified: 5
---

# Phase 08 Plan 03: All-Time Leaders Premium Gate Summary

**One-liner:** Page 3+ gate in AllTimeLeadersPagingSource with PremiumIndicatorCard (amber Flutter-matching design) and reactive ViewModel combining stat filter + premium state to recreate Pager.

## Tasks Completed

| Task | Name | Commit | Files |
|------|------|--------|-------|
| 1 | PagingSource premium gate and factory/ViewModel integration | 50a788e | 3 modified |
| 2 | PremiumIndicatorCard component and AllTimeLeadersScreen integration | 6a2530a | 1 created, 2 modified |

## What Was Built

### Task 1 — PagingSource Gate and ViewModel Reactivity

**`AllTimeLeadersPagingSource`** — Added `isPremium: Boolean` constructor parameter. `fetchData(page)` now returns `Either.Right(PageableData.empty())` when `!isPremium && page >= 3`. `PageableData.empty()` has `next = null`, which causes `BasePagingSource.load()` to set `nextKey = null` — cleanly signaling end-of-pagination to the Paging 3 library without errors.

**`BaseAllTimeLeadersPagingSourceFactory` interface** — `create()` signature updated with `isPremium: Boolean` parameter. `AllTimeLeadersPagingSourceFactory` passes it through to `AllTimeLeadersPagingSource` constructor.

**`AllTimeLeadersViewModel`** — Added `ObservePremiumActive` dependency (auto-resolved by Koin `viewModelOf`). `isPremiumFlow` wraps the use case as a `StateFlow<Boolean>` with `SharingStarted.Eagerly` and generous default `true`. An `init` block collects it to update `viewState.isPremiumActive` for the screen UI. `pagingFlow` now uses `combine(statOptionFlow, isPremiumFlow)` + `distinctUntilChanged()` + `flatMapLatest` so the Pager is recreated whenever either the stat filter or the premium status changes.

**`AllTimeLeadersViewState`** — Added `val isPremiumActive: Boolean = true` field.

Also fixed pre-existing typo: `statOptionFow` → `statOptionFlow`.

### Task 2 — PremiumIndicatorCard and Screen Integration

**`PremiumIndicatorCard`** — New composable matching Flutter's `custom_buy_premium_indicator.dart`:
- `Card` with `Color(0xFFFFF8E1)` (amber.shade50) background, 16.dp rounded corners, 4.dp elevation
- `Icons.Default.Lock` (40.dp) tinted `Color(0xFFF57C00)` (amber[800])
- Title: `pagination_premium_item_title` (bold, `titleMedium`)
- Description: `pagination_premium_item_description` (center-aligned, `bodyMedium`)
- `Button` with star icon: amber[800] background, white text, 30.dp rounded shape

**`AllTimeLeadersScreen`** — Added `onNavigateToPremium: () -> Unit` parameter to both `AllTimeLeadersScreen` and `AllTimeLeadersContent`. Inside `LazyColumn`, after the error/loading state block, conditionally renders `PremiumIndicatorCard` when `!viewState.isPremiumActive && leadersPagingItems.loadState.append.endOfPaginationReached && leadersPagingItems.itemCount > 0`.

**`App.kt`** — `composable<Screen.AllTimeLeaders>` now passes `onNavigateToPremium = { navController.navigate(Screen.Premium) }`.

## Deviations from Plan

### Auto-fixed Issues

None — plan executed exactly as written.

**Note:** The worktree was behind `main` (at commit 75bfbb4, missing 08-01 work). A `git merge main` was performed at the start to bring in the `ObservePremiumActive` use case and other dependencies required by this plan. This is expected parallel-execution behavior — 08-03 depends_on 08-01.

## Known Stubs

None — premium gating is fully wired: PagingSource blocks page 3+ for non-premium users, PremiumIndicatorCard appears at end-of-list, tapping navigates to Screen.Premium.

## Self-Check: PASSED
