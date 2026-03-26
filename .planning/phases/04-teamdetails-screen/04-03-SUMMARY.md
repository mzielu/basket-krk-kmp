---
phase: 04-teamdetails-screen
plan: 03
subsystem: ui
tags: [compose, synchronized-scroll, roster-table, kmp, multiplatform]

# Dependency graph
requires:
  - phase: 04-teamdetails-screen
    provides: TeamRosterTab stub signature + shared composables (StatDisplayTypeToggle, SortableTopRowCell, LeftColumnPlayerCell, TopRowCornerCell) from Plan 01
provides:
  - TeamRosterTab: wrapper composable with season dropdown (DropdownFormField) and AVG/SUM toggle (StatDisplayTypeToggle)
  - TeamRosterTable: 4-layer Box synchronized scroll table for roster display with sortable headers and totals row
affects: [TeamDetailsScreen integration, PlayerDetails navigation from roster]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - 4-layer Box synchronized scroll table (mirrors PlayerStatsTable/PlayerGameLogsTable pattern)
    - rememberScrollState shared between pinned layers and scrollable body for synchronized scrolling
    - Totals row via getSumStatFromStats + getValueForGivenOption (not getValueForGivenOptionWithSeasonsCount)

key-files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTable.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTab.kt

key-decisions:
  - "Roster totals row uses getValueForGivenOption (not getValueForGivenOptionWithSeasonsCount) — each PlayerWithStat.stat already has its own match count, so getSumStatFromStats produces correct sums and the summed stat's own m field handles AVG division correctly"
  - "rosterPlayerColWidth=120.dp (wider than GameLogs 100.dp) to accommodate longer player names with jersey numbers"

patterns-established:
  - "4-layer Box synchronized scroll: Layer 1 body (hScroll+vScroll), Layer 2 pinned top row (hScroll only), Layer 3 pinned left column (vScroll only), Layer 4 top-left corner cell"
  - "Roster stat table dimensions: playerColWidth=120dp, headerHeight=40dp, rowHeight=45dp, statCellWidth=35dp"

requirements-completed: [TROS-01, TROS-03, TROS-04, TROS-05]

# Metrics
duration: 2min
completed: 2026-03-17
---

# Phase 4 Plan 3: Team Roster Tab Summary

**TeamRosterTab wrapper + TeamRosterTable 4-layer synchronized scroll table with sortable column headers, clickable player names, totals row, and AVG/SUM toggle**

## Performance

- **Duration:** ~2 min
- **Started:** 2026-03-17T09:11:59Z
- **Completed:** 2026-03-17T09:11:47Z
- **Tasks:** 2
- **Files modified:** 2

## Accomplishments
- TeamRosterTab wrapper: season DropdownFormField + StatDisplayTypeToggle toolbar, empty-state guard, delegates to TeamRosterTable
- TeamRosterTable: full 4-layer Box synchronized horizontal+vertical scroll matching PlayerGameLogsTable pattern
- Sortable column headers (SortableTopRowCell) with sort arrow indicator on active column
- Player name left column (LeftColumnPlayerCell) clickable for PlayerDetails navigation
- Totals row using getSumStatFromStats aggregation rendered with bold StatCell and correct AVG/SUM logic

## Task Commits

Each task was committed atomically:

1. **Task 1: Implement TeamRosterTab wrapper with season dropdown and AVG/SUM toggle** - `dc1a1db` (feat)
2. **Task 2: Create TeamRosterTable with 4-layer Box synchronized scroll, sortable headers, totals row** - `abb45d1` (feat)

## Files Created/Modified
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTab.kt` - Replaced stub with full wrapper: season dropdown + AVG/SUM toggle toolbar, empty state, delegates to TeamRosterTable
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRosterTable.kt` - New 4-layer Box synchronized scroll table with SortableTopRowCell headers, LeftColumnPlayerCell rows, TotalsLeftCell, TopRowCornerCell corner

## Decisions Made
- Roster totals row uses `getValueForGivenOption` (not `getValueForGivenOptionWithSeasonsCount`) because each `PlayerWithStat.stat` already carries its own `m` (match count), so `getSumStatFromStats` produces a correctly summed stat where the `m` field drives AVG division automatically (Pitfall 5 from RESEARCH.md)
- `rosterPlayerColWidth` set to 120dp (wider than PlayerGameLogsTable 100dp) to accommodate player names that include jersey numbers in parentheses

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered
- Compilation showed pre-existing smart cast errors in `TeamRecordsTab.kt` on first run (cached result). Forced `--rerun-tasks` revealed the file already had the correct local-variable pattern applied; the errors were stale cache artifacts. Compilation succeeded cleanly on rerun.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- TeamRosterTab and TeamRosterTable are fully implemented and compile cleanly
- Ready for Phase 4 Plan 4 (remaining tabs: TeamResultsTab, TeamStandingsTab) or final integration

---
*Phase: 04-teamdetails-screen*
*Completed: 2026-03-17*

## Self-Check: PASSED
- TeamRosterTab.kt: FOUND
- TeamRosterTable.kt: FOUND
- SUMMARY.md: FOUND
- Commit dc1a1db (Task 1): FOUND
- Commit abb45d1 (Task 2): FOUND
- Compilation: BUILD SUCCESSFUL
