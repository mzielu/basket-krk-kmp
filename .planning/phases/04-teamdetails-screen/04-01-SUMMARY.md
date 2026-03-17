---
phase: 04-teamdetails-screen
plan: 01
subsystem: ui
tags: [kotlin, compose, multiplatform, viewmodel, stateflow, shared-composables]

# Dependency graph
requires:
  - phase: 03-team-data-layer
    provides: TeamRecordStatOption, TeamRecordRange, TeamRecord, PlayerWithStat, TeamResultList domain models
  - phase: 02-playerdetails-screen
    provides: StatDisplayType, StatOption, getValueForGivenOption, PlayerStatsTab, PlayerGameLogsTable with private composables
provides:
  - TeamDetailsViewState with rosterSortOption, rosterSortAscending, rosterStatDisplayType fields
  - TeamDetailsViewModel handlers: onRosterSortByStat, onRosterStatDisplayTypeChanged, onRecordFilterChanged
  - Shared public composable StatDisplayTypeToggle in presentation/base/ui/
  - Shared public composable SortableTopRowCell in presentation/base/ui/
  - TeamResultsTab, TeamRosterTab, TeamRecordsTab stubs with full parameter signatures
  - TeamDetailsScreen threading all callbacks through Content->Body->tabs
affects: [04-02, 04-03, 04-04 (tab implementations)]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - Shared composables extracted to presentation/base/ui/ — single source of truth for StatDisplayTypeToggle and SortableTopRowCell
    - ViewModel state drives roster sort — sorts in-memory, stores in ViewState not composable remember
    - Record filter invalidates cache — onRecordFilterChanged resets records=ViewStateData(null) before fetching
    - Season change resets sort state — onSeasonSelected clears rosterSortOption and rosterSortAscending

key-files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/StatDisplayTypeToggle.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/SortableTopRowCell.kt
  modified:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordStatOption.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordRange.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsViewModel.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerStatsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerGameLogsTable.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsScreen.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamResultsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRecordsTab.kt

key-decisions:
  - "StatDisplayTypeToggle and SortableTopRowCell extracted to base/ui as public composables — eliminates duplication between PlayerDetails and TeamDetails tabs"
  - "onRosterSortByStat sorts in-memory roster list same as PlayerDetailsViewModel.onSortByStat — new option defaults descending, repeat toggles ascending"
  - "onRecordFilterChanged resets records=ViewStateData(null) before calling fetchRecordsIfNeeded — ensures stale data is cleared before re-fetch"
  - "Season change resets rosterSortOption and rosterSortAscending — prevents stale sort arrows after season switch"

patterns-established:
  - "Shared UI components: extract composables used by 2+ screens to presentation/base/ui/ as public @Composable fun"
  - "Callback threading: TeamDetailsScreen is single ViewModel access point; Content->Body->Tab chain passes all callbacks"

requirements-completed: [TRES-02, TROS-02, TROS-03, TROS-04, TREC-02, TREC-03]

# Metrics
duration: 15min
completed: 2026-03-17
---

# Phase 04 Plan 01: TeamDetailsViewModel Wiring and Shared Composables Summary

**TeamDetailsViewModel extended with roster sort/display state + record filter invalidation, and StatDisplayTypeToggle/SortableTopRowCell extracted to shared base/ui for reuse by both PlayerDetails and TeamDetails tabs**

## Performance

- **Duration:** ~15 min
- **Started:** 2026-03-17T09:00:00Z
- **Completed:** 2026-03-17T09:15:00Z
- **Tasks:** 2
- **Files modified:** 9 (2 created, 7 modified)

## Accomplishments
- Added displayName to TeamRecordStatOption and TeamRecordRange enums for UI display
- Extended TeamDetailsViewState with 3 roster state fields (rosterSortOption, rosterSortAscending, rosterStatDisplayType)
- Added 3 new ViewModel handlers: onRosterSortByStat (in-memory sort), onRosterStatDisplayTypeChanged, onRecordFilterChanged (cache-invalidating)
- Extracted StatDisplayTypeToggle and SortableTopRowCell as public shared composables to presentation/base/ui/
- Updated all 3 team detail tab stubs with full API signatures matching final implementation requirements
- TeamDetailsScreen threads all new callbacks through Content -> Body -> tabs

## Task Commits

Each task was committed atomically:

1. **Task 1: Add displayName to record enums and extend ViewModel with roster state + record filter handler** - `e780340` (feat)
2. **Task 2: Extract shared composables and update tab stub signatures with screen wiring** - `bcd2fe7` (feat)

## Files Created/Modified
- `domain/.../TeamRecordStatOption.kt` - Added displayName parameter (PTS/AST/REB/STL/BLK/EFF/FT/FG/3FG)
- `domain/.../TeamRecordRange.kt` - Added displayName parameter (All-Time/Season/Match)
- `presentation/.../TeamDetailsViewModel.kt` - 3 new ViewState fields + 3 new handlers + season reset for sort state
- `presentation/base/ui/StatDisplayTypeToggle.kt` - New public shared composable (extracted from PlayerStatsTab)
- `presentation/base/ui/SortableTopRowCell.kt` - New public shared composable (extracted from PlayerGameLogsTable)
- `presentation/.../PlayerStatsTab.kt` - Removed private copy, import shared StatDisplayTypeToggle
- `presentation/.../PlayerGameLogsTable.kt` - Removed private copy, import shared SortableTopRowCell
- `presentation/.../TeamDetailsScreen.kt` - Added 3 callbacks to Content/Body, updated tab call sites
- `presentation/.../TeamResultsTab.kt` - Expanded signature (seasons, selectedSeason, onSeasonSelected)
- `presentation/.../TeamRosterTab.kt` - Expanded signature (all sort/display/season params)
- `presentation/.../TeamRecordsTab.kt` - Expanded signature (selectedStatOption, selectedRange, onFilterChanged)

## Decisions Made
- Used same in-memory sort pattern as `PlayerDetailsViewModel.onSortByStat` — new option defaults descending, repeat toggles ascending
- `onRecordFilterChanged` resets `records = ViewStateData(null)` before calling `fetchRecordsIfNeeded()` — required to bypass cache guard and force re-fetch with new filter
- Shared composables placed in `presentation/base/ui/` (same package as BasketKrkColors, BasketKrkStyles) — no new package needed, consistent with existing shared UI utilities
- Season change resets `rosterSortOption = null, rosterSortAscending = false` — prevents stale sort indicator persisting after season switch

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
- Gradle configuration cache was stale on first build attempt — cleared with `--stop` and `--no-configuration-cache` flag. Subsequent builds used cache normally.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- All ViewModel state and callbacks wired — tab implementations (Plans 02-04) can reference `viewState.rosterSortOption`, `viewState.rosterStatDisplayType`, etc. directly
- Shared composables ready for immediate use in TeamRosterTab and TeamRecordsTab implementations
- Tab stubs compile with full final signatures — no signature changes expected in subsequent plans
