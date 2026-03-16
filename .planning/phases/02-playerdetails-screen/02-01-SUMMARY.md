---
phase: 02-playerdetails-screen
plan: 01
subsystem: ui
tags: [kotlin-multiplatform, compose, viewmodel, stateflow, navigation, domain-extensions]

# Dependency graph
requires:
  - phase: 01-player-data-layer
    provides: PlayerStat, PlayerRecord, PlayerLogList, PlayerLogByTeam, Stat, StatOption domain models and use cases

provides:
  - Stat.getValueForGivenOptionWithSeasonsCount extension (handles StatMatches with season count divider)
  - PlayerStat.toReadableStatOptionText extension (NonSummable fixed columns: team/league/season)
  - PlayerRecordType.getSign() and toDescription() extensions
  - PlayerDetailsViewState with 4 new fields: selectedTeam, sortOption, sortAscending, statDisplayType
  - PlayerDetailsViewModel with 4 new handlers: onSeasonSelected, onTeamSelected, onSortByStat, onStatDisplayTypeChanged
  - Screen.TeamDetails stub for Phase 3 forward reference
  - PlayerDetailsScreen navigation callbacks: onNavigateToMatch and onNavigateToTeam
  - App.kt navigation wiring for match navigation

affects:
  - 02-02-PLAN (PlayerDetailsScreen UI composables that consume ViewModel state and callbacks)
  - 03-teamdetails-screen (TeamDetails screen that Screen.TeamDetails stub references)

# Tech tracking
tech-stack:
  added: []
  patterns:
    - In-memory sort pattern with ascending/descending toggle (used in onSortByStat)
    - Client-side filter without API call (onTeamSelected)
    - Force re-fetch bypassing cache on season change (onSeasonSelected)
    - Domain extensions in same file as data class (no separate extension files)

key-files:
  created: []
  modified:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Stat.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerStat.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerRecordType.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt

key-decisions:
  - "getValueForGivenOptionWithSeasonsCount delegates to existing getValueForGivenOption for non-StatMatches options — avoids duplication"
  - "PlayerRecordType.toDescription uses hardcoded English strings — domain module has no access to Compose resources"
  - "onSortByStat operates on in-memory copy of logs — no re-fetch needed, mutates gameLogs state in-place"
  - "Screen.TeamDetails added as stub now — allows App.kt onNavigateToTeam TODO comment to compile cleanly"
  - "fetchGameLogsIfNeeded updated to auto-select first team — unblocks UI rendering without extra user action"

patterns-established:
  - "Sort toggle pattern: same option again toggles ascending; new option defaults to descending"
  - "Season change resets both sortOption and selectedTeam to null to avoid stale state"

requirements-completed: [PLOG-02, PLOG-03, PLOG-04, PSTA-02, PSTA-03, PSTA-04, PLOG-05, PREC-02]

# Metrics
duration: 8min
completed: 2026-03-16
---

# Phase 2 Plan 01: Player Details Domain Extensions and ViewModel Expansion Summary

**Domain extensions for stat totals/text/records plus PlayerDetailsViewModel sort/filter/toggle state and navigation callback plumbing**

## Performance

- **Duration:** 8 min
- **Started:** 2026-03-16T22:47:21Z
- **Completed:** 2026-03-16T22:49:30Z
- **Tasks:** 2
- **Files modified:** 7

## Accomplishments

- Extended 3 domain model files with 5 new extension functions (getValueForGivenOptionWithSeasonsCount, toReadableStatOptionText, getSign, toDescription, and PlayerStat.toReadableStatOptionText)
- Expanded PlayerDetailsViewState with 4 new state fields and PlayerDetailsViewModel with 4 new handler methods
- Wired navigation callbacks through PlayerDetailsScreen, PlayerDetailsContent, PlayerDetailsBody, and App.kt

## Task Commits

Each task was committed atomically:

1. **Task 1: Domain model extensions** - `1203617` (feat)
2. **Task 2: ViewModel expansion and navigation wiring** - `ed60519` (feat)

## Files Created/Modified

- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Stat.kt` - Added getValueForGivenOptionWithSeasonsCount for season-count-aware match average
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerStat.kt` - Added toReadableStatOptionText returning team/league/season strings or stat value
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerRecordType.kt` - Added getSign() and toDescription() for record display
- `presentation/.../playerdetails/PlayerDetailsViewModel.kt` - Added 4 new ViewState fields and 4 handler methods; updated fetchGameLogsIfNeeded to auto-select first team
- `presentation/.../playerdetails/PlayerDetailsScreen.kt` - Added onNavigateToMatch and onNavigateToTeam callbacks through composable hierarchy
- `presentation/.../navigation/Screen.kt` - Added Screen.TeamDetails stub
- `presentation/.../App.kt` - Wired onNavigateToMatch to MatchDetails; onNavigateToTeam as TODO placeholder for Phase 3

## Decisions Made

- getValueForGivenOptionWithSeasonsCount delegates to existing getValueForGivenOption for non-StatMatches options — avoids duplicating the large when-expression
- PlayerRecordType.toDescription uses hardcoded English strings — domain module has no access to Compose/Android resources
- onSortByStat operates entirely in-memory, mutating both selectedTeam and gameLogs state to keep them in sync
- Screen.TeamDetails added as stub now so App.kt onNavigateToTeam placeholder compiles without error
- fetchGameLogsIfNeeded updated to auto-select first team — necessary so UI can render game log content immediately on load

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness

- All data contracts and ViewModel state ready for Plan 02 UI composables
- onNavigateToMatch is fully wired; onNavigateToTeam has TODO placeholder for Phase 3
- PlayerDetailsScreen accepts callbacks but tab content placeholders remain until Plan 02 replaces them

---
*Phase: 02-playerdetails-screen*
*Completed: 2026-03-16*
