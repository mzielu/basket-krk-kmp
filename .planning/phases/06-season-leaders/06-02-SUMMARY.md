---
phase: 06-season-leaders
plan: 02
subsystem: ui
tags: [compose, koin, navigation, viewmodel, kotlin-multiplatform]

# Dependency graph
requires:
  - phase: 06-season-leaders/06-01
    provides: SeasonLeadersViewModel, SeasonLeadersViewState, GetLeagueLeadersUseCase, GetLeagueLeaders interface
provides:
  - SeasonLeadersScreen composable with 3 filter dropdowns (season, league, stat category)
  - SeasonLeaderItem composable with data-driven additional info column (shooting vs counting)
  - Screen.SeasonLeaders navigation route wired end-to-end
  - DI registrations for GetLeagueLeaders use case and SeasonLeadersViewModel
affects: [07-more-screen, 08-premium]

# Tech tracking
tech-stack:
  added: []
  patterns: [LazyColumn with indexed items, data-driven null check for conditional UI, 3-dropdown filter row with weighted widths]

key-files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/seasonleaders/components/SeasonLeaderItem.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/seasonleaders/SeasonLeadersScreen.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/MainScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt

key-decisions:
  - "SeasonLeaderItem additional info is data-driven via null checks on made/ats, not category enum — shooting categories (FT, FG, FG3) have made+ats populated by backend; counting categories have games only"
  - "Category dropdown uses LeagueStatLeaderOption.entries directly (no VM state needed for options, enum is static)"
  - "EmptyView shown when leaders list is empty (before initial load resolves and after filter returns no results)"

patterns-established:
  - "3-dropdown filter row: season weight=1f, league weight=3f, category weight=1.5f — matches plan spec exactly"
  - "Scaffold + windowInsetsPadding(safeDrawing) + ActionBar pattern from StandingsScreen extended to SeasonLeadersScreen"
  - "Navigation chain: Statistics tab -> openLeagueLeaders -> App.navigate(SeasonLeaders) -> screen -> tap leader -> PlayerDetails"

requirements-completed: [SLDR-01, SLDR-02, SLDR-03, SLDR-04, SLDR-05, SLDR-06]

# Metrics
duration: 8min
completed: 2026-03-18
---

# Phase 06 Plan 02: Season Leaders UI Summary

**Season Leaders screen with 3-dropdown filter (season/league/stat category), ranked leader list with data-driven shooting/counting info, and full navigation chain from Statistics tab through to PlayerDetails**

## Performance

- **Duration:** 8 min
- **Started:** 2026-03-18T00:17:00Z
- **Completed:** 2026-03-18T00:25:42Z
- **Tasks:** 2
- **Files modified:** 6

## Accomplishments
- SeasonLeaderItem composable renders position, team logo, player name, Double value (1 decimal), and conditional additional info: `(made/ats)` for shooting categories or `{games}M` for counting categories
- SeasonLeadersScreen with 3 weighted DropdownFormFields, Box state routing (loading/error/empty/data), and LazyColumn of SeasonLeaderItems
- Full navigation wired: Screen.SeasonLeaders route added, composable entry in App.kt, openLeagueLeaders parameter threaded through MainScreen to StatisticsScreen (replaced empty lambda), DI registrations for use case and ViewModel

## Task Commits

Each task was committed atomically:

1. **Task 1: Create SeasonLeaderItem composable** - `65d347f` (feat)
2. **Task 2: Create SeasonLeadersScreen and wire navigation + DI** - `48cb3df` (feat)

**Plan metadata:** (docs commit — see below)

## Files Created/Modified
- `presentation/.../seasonleaders/components/SeasonLeaderItem.kt` - Leader item composable with position, logo, name, value, and data-driven additional info
- `presentation/.../seasonleaders/SeasonLeadersScreen.kt` - Screen with 3 filter dropdowns and LazyColumn of leader items
- `presentation/.../navigation/Screen.kt` - Added SeasonLeaders data object route
- `presentation/.../App.kt` - Added openLeagueLeaders to MainScreen call and composable<Screen.SeasonLeaders> entry
- `presentation/.../main/MainScreen.kt` - Added openLeagueLeaders parameter, wired to StatisticsScreen
- `presentation/.../di/PresentationModule.kt` - Registered GetLeagueLeaders use case and SeasonLeadersViewModel

## Decisions Made
- Additional info column is data-driven (null checks on made/ats) rather than driven by the selected category enum — backend populates made/ats for shooting categories and leaves them null for counting categories
- Category dropdown feeds `LeagueStatLeaderOption.entries` directly as a static list (no ViewModel state required for the options themselves)
- EmptyView shown when leaders list is empty to handle both pre-load and filter-returns-nothing states

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
None.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- Season Leaders feature is fully operational end-to-end (data layer from Plan 01 + UI from Plan 02)
- Phase 06 complete — ready for Phase 07 (More Screen / Tournament Chooser)
- PlayerDetails navigation target already exists from v1.0

---
*Phase: 06-season-leaders*
*Completed: 2026-03-18*
