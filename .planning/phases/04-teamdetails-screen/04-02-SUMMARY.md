---
phase: 04-teamdetails-screen
plan: 02
subsystem: presentation
tags: [composable, lazy-column, dropdown, match-results, team-records, navigation]
dependency_graph:
  requires: [04-01]
  provides: [TeamResultsTab, TeamRecordsTab]
  affects: [TeamDetailsScreen]
tech_stack:
  added: []
  patterns:
    - "resolveMatchSign private function for MatchStatus -> color/text mapping"
    - "formatOneDecimal helper for KMP-compatible decimal formatting (no String.format)"
    - "Local variable capture for cross-module nullable smart cast (val matchId = record.matchId)"
key_files:
  created: []
  modified:
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamResultsTab.kt
    - presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/components/TeamRecordsTab.kt
decisions:
  - "formatOneDecimal uses manual rounding instead of String.format for KMP common code compatibility"
  - "Smart cast workaround: assign record.matchId and record.ats to local vals before null-checking — required for cross-module public API properties in KMP"
  - "Navigation in TeamRecordItem checks matchId != null first (not player.id which is always non-null Int)"
metrics:
  duration: "~10min"
  completed_date: "2026-03-17"
  tasks_completed: 2
  files_modified: 2
---

# Phase 4 Plan 02: TeamResultsTab and TeamRecordsTab Summary

**One-liner:** Full Results and Records tab composables with W/L badges, playoff backgrounds, filter dropdowns, and suffix calculations matching Flutter reference.

## Tasks Completed

| # | Task | Commit | Status |
|---|------|--------|--------|
| 1 | Implement TeamResultsTab with season dropdown, LazyColumn, and TeamResultItem | 31d6d54 | Done |
| 2 | Implement TeamRecordsTab with dual filter dropdowns, LazyColumn, and TeamRecordItem | 8bb0527 | Done |

## What Was Built

### Task 1: TeamResultsTab

Replaced the stub with a full composable that includes:

- `DropdownFormField` for season selection (100dp width, reads `season.num`)
- `LazyColumn` of `TeamResultItem` composables with `Spacer(4.dp)` between rows
- Empty state showing centered "No results available" text
- `TeamResultItem` private composable: border + rounded corners (8dp), `background(PlayoffsBg)` for `MatchType.PLAYOFFS`, clickable to `onMatchPress(result.id)`
- Item layout: "vs" text + opponent logo (30dp) + opponent name (weighted) + vertical divider + W/L badge (28dp square) + score column + date column
- `resolveMatchSign` function: `IN_PROGRESS` -> `MatchInProgress/"IP"`, `NON_STARTED` -> `MatchNotStarted/"?"`, else win/loss by point comparison

### Task 2: TeamRecordsTab

Replaced the stub with a full composable that includes:

- Two `DropdownFormField` dropdowns in a `Row`: Range (left, weight 1) and Category (right, weight 1), both using `displayName`
- `LazyColumn` of `TeamRecordItem` composables
- Empty state showing centered "No records available" text
- `TeamRecordItem` private composable: border + rounded corners, clickable
- Item layout: position (40dp, center) + player name with optional " (SX)" season suffix (weighted) + value (35dp, center) + suffix (60dp, end) + chevron icon (if `matchId != null`)
- `buildRecordSuffix`: percentage `(X.X%)` when `ats != null && ats > 0`, per-game `(X.X PG)` when `games > 0`, match-count `XM` when `matchId == null`, empty otherwise
- Navigation: `matchId != null` -> `onMatchPress(matchId)`, else `onPlayerPress(player.id)`

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Smart cast failures for cross-module nullable properties**
- **Found during:** Task 2 (compilation)
- **Issue:** KMP compiler cannot smart-cast `record.matchId` (Int?) and `record.ats` (Int?) after null check because they are public API properties declared in a different module (domain)
- **Fix:** Assigned nullable properties to local `val` variables before null-checking: `val ats = record.ats`, `val matchId = record.matchId`; used local vars in conditions and expressions
- **Files modified:** TeamRecordsTab.kt
- **Commit:** 8bb0527 (included in task commit)

## Decisions Made

1. **formatOneDecimal without String.format:** KMP common code cannot use `java.lang.String.format`. Implemented manual rounding via `(value * 10).toInt() / 10.0` then `.toString()` with fallback `.0` appended if no decimal point present.

2. **Smart cast via local variable:** Cross-module nullable properties require local `val` assignment before smart cast in KMP. This is a well-known KMP pattern — applied to `record.ats` and `record.matchId` in `buildRecordSuffix` and the click lambda.

3. **matchId-first navigation:** Navigation logic checks `record.matchId != null` before falling back to `onPlayerPress(record.player.id)` — `player.id` is always non-null (Int), so checking it first would always navigate to player (Pitfall 6 from RESEARCH.md).

## Self-Check: PASSED

| Item | Status |
|------|--------|
| TeamResultsTab.kt | FOUND |
| TeamRecordsTab.kt | FOUND |
| 04-02-SUMMARY.md | FOUND |
| Commit 31d6d54 | FOUND |
| Commit 8bb0527 | FOUND |
