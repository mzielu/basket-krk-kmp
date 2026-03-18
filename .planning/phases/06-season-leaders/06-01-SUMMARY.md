---
phase: 06-season-leaders
plan: 01
subsystem: ui
tags: [kotlin, kmp, viewmodel, stateflow, use-case, league-leaders, season-leaders]

# Dependency graph
requires:
  - phase: 05-navigation-integration
    provides: Navigation graph wired, onNavigateToPlayer available
provides:
  - GetLeagueLeaders interface and GetLeagueLeadersUseCase delegating to LeagueRepository.getLeagueLeaders
  - SeasonLeadersViewModel with cascading season/league/category filter logic
  - SeasonLeadersViewState with leaders list and PTS default stat option
affects: [06-season-leaders plan 02 (screen + navigation wiring), 07-more-screen]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - Reactive leader fetch via Pair(selectedLeague, selectedStatOption).distinctUntilChanged() — avoids double-fetch on init
    - Season change nulls selectedLeague so reactive collector triggers correct fetch
    - onSuspendSuccess / onSuspendGeneralError error handling pattern (from StandingsViewModel)

key-files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetLeagueLeadersUseCase.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/seasonleaders/SeasonLeadersViewModel.kt
  modified: []

key-decisions:
  - "Pair(selectedLeague, selectedStatOption) used as distinctUntilChanged key instead of separate flows — keeps reactive logic simple and avoids double-fetch"
  - "if (league != null) guard inside collect instead of filterNotNull on Pair — selectedStatOption is never null (enum), only league can be null"
  - "No direct fetchLeaders call in fetchInitData — reactive collector handles initial leader fetch when selectedLeague is set"

patterns-established:
  - "SeasonLeadersViewModel extends StandingsViewModel pattern with a third filter dimension (stat category)"
  - "fetchLeaders private method follows same loading/error pattern as fetchLeagueDetails in StandingsViewModel"

requirements-completed: [SLDR-01, SLDR-02, SLDR-03, SLDR-04, SLDR-05]

# Metrics
duration: 1min
completed: 2026-03-18
---

# Phase 6 Plan 01: Season Leaders Data and Business Logic Layer Summary

**Thin GetLeagueLeadersUseCase wrapping LeagueRepository.getLeagueLeaders, plus SeasonLeadersViewModel with reactive Pair(league, statOption).distinctUntilChanged() fetch pattern and cascading season/league/category filter state**

## Performance

- **Duration:** 1 min
- **Started:** 2026-03-18T00:20:20Z
- **Completed:** 2026-03-18T00:21:23Z
- **Tasks:** 2
- **Files modified:** 2 created

## Accomplishments

- Created GetLeagueLeadersUseCase with GetLeagueLeaders interface, delegating to existing LeagueRepository.getLeagueLeaders with no pagination (flat List<LeagueLeader>)
- Created SeasonLeadersViewModel with three-parameter cascading filter (season/league/stat category) using reactive distinctUntilChanged collector to avoid double-fetch on init
- Created SeasonLeadersViewState with leaders field and selectedStatOption defaulting to LeagueStatLeaderOption.PTS

## Task Commits

Each task was committed atomically:

1. **Task 1: Create GetLeagueLeadersUseCase** - `e9fbe5b` (feat)
2. **Task 2: Create SeasonLeadersViewModel and SeasonLeadersViewState** - `92f9ef1` (feat)

## Files Created/Modified

- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetLeagueLeadersUseCase.kt` - GetLeagueLeaders interface + GetLeagueLeadersUseCase implementation with Input(leagueId, statOption)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/seasonleaders/SeasonLeadersViewModel.kt` - SeasonLeadersViewModel with cascading filter logic + SeasonLeadersViewState

## Decisions Made

- Used `Pair(selectedLeague, selectedStatOption).distinctUntilChanged()` as the reactive key so both league and stat category changes trigger a leader fetch in a single collector, avoiding two separate flow collectors and potential race conditions.
- Used `if (league != null)` guard inside the collect lambda rather than `.filterNotNull()` on the Pair — selectedStatOption is a non-null enum, only the league can be null (on init before data loads or on season change).
- No direct `fetchLeaders` call inside `fetchInitData` — when `fetchInitData` sets `selectedLeague`, the reactive collector fires automatically, keeping the trigger logic in one place and avoiding a double-fetch.

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- GetLeagueLeadersUseCase and SeasonLeadersViewModel are ready for Plan 02 (SeasonLeadersScreen composable + navigation wiring + DI registration)
- The `seasonleaders` package directory was created as part of this plan, ready for the screen file
- No blockers

---
*Phase: 06-season-leaders*
*Completed: 2026-03-18*
