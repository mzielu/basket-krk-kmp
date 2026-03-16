---
phase: 03-team-data-layer
plan: "02"
subsystem: team-details-ui
tags: [use-cases, viewmodel, compose, di, navigation]
dependency_graph:
  requires:
    - 03-01 (TeamRepository, domain models, TeamService, NetworkTeamService, TeamRepositoryImpl)
  provides:
    - GetTeamDetailsUseCase (seasons sorted descending)
    - GetTeamResultsUseCase
    - GetTeamRosterUseCase
    - GetTeamRecordsUseCase
    - TeamDetailsViewModel (per-tab lazy loading, W-L computation)
    - TeamDetailsScreen + 3 stub tab composables
    - Full DI wiring (DataModule + PresentationModule)
    - Navigation: composable<Screen.TeamDetails> in App.kt
  affects:
    - App.kt (navigation graph)
    - DataModule.kt (new registrations)
    - PresentationModule.kt (new registrations)
tech_stack:
  added: []
  patterns:
    - MVVM+StateFlow (per-tab ViewStateData lazy loading with cache guard)
    - Koin DI (single for use cases, viewModel with parametersOf for teamId)
    - Compose navigation (composable<Screen.TeamDetails> type-safe route)
    - W-L computation: FINISHED/WALKOVER + REGULAR_SEASON filter only
key_files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetTeamDetailsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetTeamResultsUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetTeamRosterUseCase.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetTeamRecordsUseCase.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsViewModel.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamResultsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRecordsTab.kt
  modified:
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt
decisions:
  - W-L/point differential stored as nullable fields in ViewState, populated when results load, shown as "-" before load
  - Tab auto-fetch: init->fetchTeamDetails->on success auto-fetch tab 0 (Results); other tabs load lazily on selection
  - Cache guard: skip re-fetch if current.data != null && !current.isError
  - onSeasonSelected resets results/roster/winsLosses/pointDifferential and triggers immediate results re-fetch
  - Tab stubs show count text only; full implementations deferred to later plans
metrics:
  duration: 4min
  completed: "2026-03-16T23:40:12Z"
  tasks: 2
  files_created: 9
  files_modified: 3
---

# Phase 03 Plan 02: Team Use Cases + TeamDetailsScreen Summary

**One-liner:** End-to-end team details pipeline with 4 use cases, ViewModel with per-tab lazy loading and W-L computation, TeamDetailsScreen with header/3-tab UI, full DI wiring, and navigation integration.

## Tasks Completed

| # | Task | Commit | Files |
|---|------|--------|-------|
| 1 | Create 4 use cases and TeamDetailsViewModel with W-L computation | 812ed03 | 5 created |
| 2 | Create TeamDetailsScreen, 3 stub tab composables, wire DI and navigation | 94e0d9c | 4 created, 3 modified |

## What Was Built

### Use Cases (4 files)

All follow the `interface + class + Input` pattern mirroring existing player use cases:

- `GetTeamDetailsUseCase`: wraps `teamRepository.getTeamDetails()`, sorts seasons descending by num (most recent first)
- `GetTeamResultsUseCase`: pass-through to `teamRepository.getTeamResults(teamId, seasonId)`
- `GetTeamRosterUseCase`: pass-through to `teamRepository.getTeamRoster(teamId, seasonId)`
- `GetTeamRecordsUseCase`: pass-through to `teamRepository.getTeamRecords(teamId, category)`

### TeamDetailsViewModel

Per-tab lazy loading with cache guard (`data != null && !isError`). Key behaviors:
- `init` triggers `fetchTeamDetails()` which auto-fetches Results tab on success
- `onTabSelected(1)` triggers `fetchRosterIfNeeded`, `onTabSelected(2)` triggers `fetchRecordsIfNeeded`
- `onSeasonSelected` resets results/roster/winsLosses/pointDifferential and re-fetches results
- `computeWinsLosses` and `computePlusMinus` filter to FINISHED/WALKOVER + REGULAR_SEASON only
- `TeamDetailsViewState` has nullable `winsLosses: Pair<Int,Int>?` and `pointDifferential: Int?`

### TeamDetailsScreen

Mirrors PlayerDetailsScreen structure exactly:
- `Scaffold` with `ActionBar` showing team name
- Loading/error/content tri-state on `teamDetails`
- Header: `BasketKrkImage` (64dp) + Column { name, league name (nullable), seasons joined, W-L row }
- W-L text: `"3-1  +12"` when results loaded, `"-"` when null
- `PrimaryTabRow` with "Results" / "Roster" / "Records" tabs using `BasketKrkColors.Main` indicator
- Each tab: loading/error/data tri-state backed by respective `ViewStateData` field

### Stub Tab Composables

`TeamResultsTab`, `TeamRosterTab`, `TeamRecordsTab` display count text only; full implementations deferred.

### DI Wiring

- `DataModule`: added `single<TeamService> { NetworkTeamService(get()) }` and `single<TeamRepository> { TeamRepositoryImpl(get()) }`
- `PresentationModule`: added 4 use case registrations + `viewModel { (teamId: Int) -> TeamDetailsViewModel(teamId, get(), get(), get(), get()) }`

### Navigation

`App.kt` changes:
1. `openTeamDetails` callback: now navigates to `Screen.TeamDetails(teamId = it)`
2. `onNavigateToTeam` in PlayerDetailsScreen: now navigates to `Screen.TeamDetails(teamId = it)`
3. New `composable<Screen.TeamDetails>` block using `koinInject(parameters = { parametersOf(args.teamId) })`

## Deviations from Plan

None - plan executed exactly as written.

## Self-Check: PASSED

All files verified to exist. All commits verified in git log.
