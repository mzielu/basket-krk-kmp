---
phase: 01-player-data-layer
plan: 02
subsystem: ui
tags: [kotlin, compose, koin, mvvm, stateflow, navigation]

# Dependency graph
requires:
  - phase: 01-player-data-layer
    plan: 01
    provides: PlayerRepository, PlayerService, domain models (PlayerDetails, PlayerLogList, PlayerStat, PlayerRecord), DTOs, mappers
provides:
  - GetPlayerDetailsUseCase with descending season sort
  - GetPlayerGameLogsUseCase
  - GetPlayerStatsUseCase
  - GetPlayerRecordsUseCase
  - PlayerDetailsViewModel with per-tab lazy loading and cache
  - PlayerDetailsScreen with header, team logo, and 3-tab navigation
  - Screen.PlayerDetails navigation route
  - DI registrations for PlayerService, PlayerRepository, 4 use cases, and ViewModel
affects: [phase-02-player-ui-details, any phase that navigates to player details]

# Tech tracking
tech-stack:
  added: []
  patterns: [per-tab ViewStateData lazy loading with cache check, koinInject with parametersOf for parameterized ViewModels, PrimaryTabRow with 3 tabs, Scaffold+ActionBar screen shell]

key-files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerDetailsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerGameLogsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerStatsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerRecordsUseCase.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt

key-decisions:
  - "Auto-fetch Game Logs tab after player details load on init (first tab always visible immediately)"
  - "Cache check: if current.data != null && !current.isError skip re-fetch on tab switch"
  - "Tab content shows placeholder text (content coming in Phase 2) — only loading/error states are functional now"

patterns-established:
  - "Per-tab lazy loading: each tab has its own ViewStateData<T?>, fetched on first selection only"
  - "PlayerDetailsBody is a private composable that takes details + viewState + onTabSelected, keeping PlayerDetailsContent clean"
  - "PrimaryTabRow with TabRowDefaults.SecondaryIndicator styled in BasketKrkColors.Main for all detail screens"

requirements-completed:
  - PLYR-01
  - PLYR-02

# Metrics
duration: 3min
completed: 2026-03-16
---

# Phase 1 Plan 2: Player Details Screen Summary

**PlayerDetailsScreen with team logo header, 3-tab navigation (Game Logs/Stats/Records) and per-tab lazy-loading ViewStateData, wired end-to-end from repository through 4 use cases to ViewModel to navigation**

## Performance

- **Duration:** 3 min
- **Started:** 2026-03-16T21:59:40Z
- **Completed:** 2026-03-16T22:03:00Z
- **Tasks:** 2
- **Files modified:** 10

## Accomplishments
- 4 player use cases created following the interface+class+Input pattern; GetPlayerDetailsUseCase sorts seasons descending
- PlayerDetailsViewModel with per-tab lazy loading (gameLogs, stats, records) and cache guard preventing redundant fetches
- PlayerDetailsScreen with Scaffold+ActionBar, team logo (BasketKrkImage), player name, seasons list, and PrimaryTabRow with 3 tabs
- Full DI wiring: PlayerService + PlayerRepository in DataModule, 4 use cases + ViewModel in PresentationModule
- Navigation wired in App.kt: Screen.PlayerDetails route added, both Main and AllTimeLeaders TODO lambdas replaced with actual navigation

## Task Commits

Each task was committed atomically:

1. **Task 1: Create use cases and PlayerDetailsViewModel** - `b0d47b1` (feat)
2. **Task 2: Create PlayerDetailsScreen and wire navigation + DI** - `c494980` (feat)

## Files Created/Modified
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerDetailsUseCase.kt` - Use case with sortedByDescending { it.num }
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerGameLogsUseCase.kt` - Pass-through with playerId + seasonId input
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerStatsUseCase.kt` - Pass-through by playerId
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerRecordsUseCase.kt` - Pass-through by playerId
- `presentation/.../screens/playerdetails/PlayerDetailsViewModel.kt` - ViewModel with 5 constructor params, init fetch, onTabSelected, cache guards
- `presentation/.../screens/playerdetails/PlayerDetailsScreen.kt` - Screen with team logo header and 3-tab PrimaryTabRow
- `presentation/.../navigation/Screen.kt` - Added PlayerDetails(playerId: Int) route
- `presentation/.../App.kt` - Added composable<Screen.PlayerDetails>, replaced two TODO lambdas
- `data/.../di/DataModule.kt` - Added PlayerService + PlayerRepository registrations
- `presentation/.../di/PresentationModule.kt` - Added 4 use cases + PlayerDetailsViewModel registrations

## Decisions Made
- Auto-fetch Game Logs tab immediately after player details load in init block, so the first visible tab shows data quickly
- Cache check uses `current.data != null && !current.isError` — a previously loaded tab will NOT refetch on switch back
- Tab content shows placeholder text for Phase 2; only loading/error states are wired now

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Full data pipeline (API -> DTO -> Repository -> UseCase -> ViewModel -> Screen) is compiled and navigable
- PlayerDetailsScreen displays player header with team logo, name, seasons, and 3 navigable tabs
- Phase 2 can implement actual table content for Game Logs, Stats, and Records tabs

---
*Phase: 01-player-data-layer*
*Completed: 2026-03-16*
