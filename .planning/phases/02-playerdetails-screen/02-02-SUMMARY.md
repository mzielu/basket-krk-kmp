---
phase: 02-playerdetails-screen
plan: 02
subsystem: ui
tags: [compose, kmp, multiplatform, tables, scrollable, lazy-column, mvvm]

# Dependency graph
requires:
  - phase: 02-playerdetails-screen plan 01
    provides: PlayerDetailsViewModel with ViewState, domain models (PlayerLog, PlayerStat, PlayerRecord, PlayerLogList, PlayerLogByTeam), PlayerDetailsScreen skeleton with placeholder tabs
  - phase: 01-player-data-layer
    provides: domain models, use cases, repository interfaces
provides:
  - PlayerGameLogsTable: synchronized scroll table with fixed left column, sortable headers, W/L badge per game
  - PlayerGameLogsTab: season and team filter dropdowns above game logs table
  - PlayerStatsTable: synchronized scroll table with 3 fixed columns (S/LGE/TEAM), totals row with getValueForGivenOptionWithSeasonsCount
  - PlayerStatsTab: avg/total toggle above stats table
  - PlayerRecordsTab: LazyColumn of RecordItem composables with value circle, sign, description, navigation
  - PlayerDetailsScreen: fully integrated with real tab content, all callbacks wired
affects: [03-teamdetails-screen, future tab-based screens]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - 4-layer Box table layout (body scroll / pinned header / pinned left / corner cell) — reused from MatchDetailsTeamTable
    - Tab content with loading/error/data tri-state inside each tab composable
    - Callback threading from Screen -> Content -> Body -> Tab composables

key-files:
  created:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerGameLogsTable.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerGameLogsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerStatsTable.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerStatsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerRecordsTab.kt
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt

key-decisions:
  - "PlayerGameLogsTable uses 4-layer Box pattern identical to MatchDetailsTeamTable for synchronized horizontal/vertical scroll"
  - "SortableTopRowCell is private to PlayerGameLogsTable — adds sort direction arrow icon to existing TopRowCell pattern"
  - "StatDisplayTypeToggle uses background(Main.copy(alpha = 0.2f)) for selected state — subtle highlight without full colored fill"
  - "buildSecondaryText is a plain (non-composable) function using hardcoded English matching string resource values — simpler than threading composable context"
  - "PlayerDetailsContent and PlayerDetailsBody accept all 4 extra callbacks (onSeasonSelected, onTeamSelected, onSortByStat, onStatDisplayTypeChanged) to keep PlayerDetailsScreen as single ViewModel access point"

patterns-established:
  - "Tab composable pattern: Tab = filters/toggles above table, table = 4-layer Box with synchronized scroll"
  - "Fixed left column cells use drawTopBottomBorder() + background(DefaultBackground) + Alignment.Center"
  - "Totals row uses bold = true in StatCell to visually distinguish from data rows"

requirements-completed: [PLOG-01, PLOG-02, PLOG-03, PLOG-04, PLOG-05, PSTA-01, PSTA-02, PSTA-03, PSTA-04, PREC-01, PREC-02]

# Metrics
duration: 12min
completed: 2026-03-16
---

# Phase 02 Plan 02: PlayerDetails UI Components Summary

**5 Compose tab composables using synchronized-scroll 4-layer Box tables for game logs (sortable + W/L badge), stats (3-fixed-col + avg/total toggle + totals row), and records (LazyColumn + value circle), fully wired into PlayerDetailsScreen**

## Performance

- **Duration:** 12 min
- **Started:** 2026-03-16T23:10:00Z
- **Completed:** 2026-03-16T23:22:00Z
- **Tasks:** 3
- **Files modified:** 6

## Accomplishments
- Created PlayerGameLogsTable with synchronized hScroll/vScroll, fixed left column showing date/W-L badge/score/opponent (clickable to navigate to match), and sortable stat headers with arrow indicators
- Created PlayerStatsTable with 3 fixed left columns (S, LGE, TEAM — team name is clickable), scrollable stat body, and a bold totals row using `getValueForGivenOptionWithSeasonsCount`
- Created PlayerRecordsTab with LazyColumn of RecordItem composables: value circle, stat sign, description, times/date secondary text, OpenInNew icon
- Replaced all three placeholder Text() composables in PlayerDetailsScreen with real tab composables and threaded all ViewModel callbacks through the composable hierarchy

## Task Commits

Each task was committed atomically:

1. **Task 1: Create Game Logs tab and table composables** - `9b72763` (feat)
2. **Task 2: Create Stats tab/table and Records tab composables** - `c7bae9f` (feat)
3. **Task 3: Integrate tab composables into PlayerDetailsScreen replacing placeholders** - `fb11719` (feat)

**Plan metadata:** _(to be added by final commit)_

## Files Created/Modified
- `components/PlayerGameLogsTable.kt` — Synchronized scroll table for game logs; fixed left col with W/L badge; sortable headers with arrow icon
- `components/PlayerGameLogsTab.kt` — Season + team dropdowns above game logs table
- `components/PlayerStatsTable.kt` — Synchronized scroll table with 3 fixed columns, avg/total display, bold totals row
- `components/PlayerStatsTab.kt` — StatDisplayTypeToggle (SUM/AVG) above stats table
- `components/PlayerRecordsTab.kt` — LazyColumn of RecordItem with circle, sign, description, OpenInNew
- `PlayerDetailsScreen.kt` — Replaced placeholders; updated Content/Body signatures; threaded 4 extra callbacks

## Decisions Made
- Used 4-layer Box table pattern matching MatchDetailsTeamTable for scroll synchronization — consistent with existing codebase
- `SortableTopRowCell` is private to PlayerGameLogsTable — same shape as TopRowCell but adds a 12dp arrow icon when sort is active
- `buildSecondaryText` is a plain non-composable function using hardcoded English matching the string resource values (simpler than threading composable context for a pure string operation)
- PlayerDetailsContent and PlayerDetailsBody each accept all 4 extra ViewModel callbacks to keep PlayerDetailsScreen as the single ViewModel access point

## Deviations from Plan
None - plan executed exactly as written.

## Issues Encountered
- `Icons.Default.OpenInNew` produces a deprecation warning (use AutoMirrored version). The plan specified this exact icon so the warning was left in place; only a warning, not an error.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- PlayerDetails screen is fully functional with all three tabs: Game Logs, Stats, Records
- All navigation callbacks (onNavigateToMatch, onNavigateToTeam) are wired through all tabs
- Phase 02 is complete — ready to proceed to Phase 03 (Team Details Screen) or whichever phase is next

---
*Phase: 02-playerdetails-screen*
*Completed: 2026-03-16*
