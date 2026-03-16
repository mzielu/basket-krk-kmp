# Phase 2: PlayerDetails Screen - Context

**Gathered:** 2026-03-16
**Status:** Ready for planning

<domain>
## Phase Boundary

Replace the placeholder tab content in PlayerDetailsScreen with full implementations: scrollable game logs table with filtering and sorting, aggregated stats table with avg/total toggle, and records list with match navigation. Add season/team filter dropdowns and sort-by-column functionality.

This phase does NOT create TeamDetails (Phase 3/4) but DOES wire the team name click in stats tab to navigate to TeamDetails (PSTA-04) — the route will be created in Phase 3.

</domain>

<decisions>
## Implementation Decisions

### Game Logs table
- Reuse MatchDetailsTeamTable component — only the first left column is fixed, same synchronized scrolling pattern
- Fixed left column shows: date + opponent name + result (W/L + score) matching Flutter's game log table layout
- Clicking the fixed column (date/result cell) navigates to MatchDetails for that game (PLOG-05)
- Stat column headers are clickable to sort game logs by that stat (PLOG-04)
- Sort direction indicated visually on the active column header

### Stats table display
- Reuse MatchDetailsTeamTable with fixed columns: Season, League, Team
- Team name in the fixed column is clickable — navigates to TeamDetails (PSTA-04)
- Avg/total toggle button placed above the table, matching Flutter layout
- Toggle switches all stat values between averages and totals (PSTA-02)
- Totals row at the bottom of the table (PSTA-03)

### Records list
- ListView (not a table) matching Flutter's records tab
- Each record item shows: record type name, value, times achieved, date
- Clicking a record navigates to the associated match (PREC-02)
- Empty state when no records exist

### Filtering controls
- Season and team dropdowns placed above the table within the Game Logs tab content area
- Styled like DropdownFormField from the matches screen
- Changing season triggers data re-fetch for game logs
- Changing team filters the displayed game logs (client-side if Flutter does it that way, or re-fetch)

### Claude's Discretion
- Exact sort indicator styling (arrow icon, color)
- Avg/total toggle button styling
- Record item card/row design
- Empty state messaging and styling
- How to handle the TeamDetails navigation when TeamDetails screen doesn't exist yet (callback placeholder or Screen.TeamDetails route stub)

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (tab content reference)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/view/player_details_game_log_table.dart` — Game logs table layout, fixed column content, sort interaction
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/view/player_details_stat_table.dart` — Stats table layout, fixed columns (season/league/team), totals row
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/player_details_screen.dart` — Tab content structure, filter placement, records list
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/player_details_bloc.dart` — BLoC events for sorting, filtering, avg/total toggle

### Existing KMP components (reuse these)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/components/MatchDetailsTeamTable.kt` — Scrollable stat table with fixed left column, synchronized scrolling
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt` — Current screen shell to extend
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt` — Current ViewModel with per-tab ViewStateData
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/MatchesScreen.kt` — DropdownFormField usage pattern for filters

### Domain models (already built in Phase 1)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerLog.kt` — Game log model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerStat.kt` — Aggregated stat model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerRecord.kt` — Record model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/StatOption.kt` — Stat column definitions and sort options

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `MatchDetailsTeamTable`: Handles synchronized horizontal/vertical scroll, fixed left column, stat header row, clickable cells — core table component for game logs and stats tabs
- `DropdownFormField`: Season/round selector from MatchesScreen — reuse for season and team filter dropdowns
- `StatOption` sealed class: Defines stat column types (PTS, AST, REB, etc.) with display signs — use for table column headers
- `ViewStateData<T>`: Already wired in PlayerDetailsViewModel for all tabs — extend with filter/sort state
- `BasketKrkColors` and `BasketKrkStyles`: Consistent styling primitives

### Established Patterns
- `MatchDetailsTeamTable` accepts: list of players/items, stat list, onPlayerClick, onSortByStat callbacks
- ViewModel `_viewState.update { it.copy(...) }` pattern for state changes
- Filter changes trigger `viewModelScope.launch` with fetch call
- Navigation via lambda callbacks passed from `App.kt`

### Integration Points
- `PlayerDetailsScreen.kt` — Replace 3 placeholder `Text()` composables with actual tab content
- `PlayerDetailsViewModel.kt` — Add sort state, filter state, avg/total toggle state, filter change handlers
- `PlayerDetailsViewState` — Extend with `selectedSeason`, `selectedTeam`, `statDisplayType`, `sortOption` fields
- `App.kt` — Add `onNavigateToMatch` and `onNavigateToTeam` callback parameters

</code_context>

<specifics>
## Specific Ideas

- Match Flutter behavior exactly — 1:1 migration for all tab content
- Filter dropdowns styled like MatchesScreen's DropdownFormField component
- Game logs table: same synchronized scrolling as MatchDetailsTeamTable
- Stats table: same pattern but with Season/League/Team as fixed columns instead of player name
- Records: simple list, not a table — each item clickable to match

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 02-playerdetails-screen*
*Context gathered: 2026-03-16*
