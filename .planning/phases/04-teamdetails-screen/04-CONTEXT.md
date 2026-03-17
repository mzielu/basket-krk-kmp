# Phase 4: TeamDetails Screen - Context

**Gathered:** 2026-03-17
**Status:** Ready for planning

<domain>
## Phase Boundary

Replace the 3 stub tab composables in TeamDetailsScreen with full implementations: results list with season filtering, roster stat table with sorting and avg/total toggle, and records list with stat category + range filtering. Add all necessary ViewModel handlers and navigation callbacks.

This phase does NOT modify navigation entry points (Phase 5) but DOES wire player name clicks in roster to PlayerDetails.

</domain>

<decisions>
## Implementation Decisions

### Results tab
- Match Flutter: each result item shows date, opponent name/logo, score, W/L badge, league info
- Clickable — tapping a result navigates to MatchDetails (TRES-03)
- Season dropdown above the results list for filtering (TRES-02)
- Simple LazyColumn of result items (not a table)

### Roster tab
- Mirror Phase 2's PlayerStatsTable pattern: fixed player name column, scrollable stat columns
- Same synchronized scroll, sort by column header, avg/total toggle above table
- Player name clickable — navigates to PlayerDetails (TROS-05)
- Season dropdown above the roster table for filtering (TROS-02)

### Records tab
- Match Flutter: two dropdown selectors — one for stat category (PTS, AST, REB, STL, BLK, EFF, FT, FG, 3FG), one for range (All-Time, Season, Match)
- Each record entry shows: position, player name, value, and optionally calculated percentages/averages
- Clickable — navigate to associated player or match (TREC-04)
- Changing either filter triggers a new API call with the composite `cat` parameter

### General patterns (from Phase 2)
- Reuse MatchDetailsTeamTable scroll pattern for roster table
- Filter dropdowns styled like DropdownFormField from MatchesScreen
- Empty state handling for each tab

### Claude's Discretion
- Result item card/row design (W/L badge styling, score layout)
- Exact roster table column widths
- Record entry layout design
- Empty state messaging
- How to handle record entries that have both player and match navigation options

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (tab content reference)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/teams/details/view/team_result_item.dart` — Result item layout
- `~/Documents/Development/flutter/basket_krk/lib/presentation/teams/details/team_details_screen.dart` — Tab content structure, records filter placement
- `~/Documents/Development/flutter/basket_krk/lib/presentation/teams/details/team_details_bloc.dart` — BLoC events for season change, record filter change, roster sort
- `~/Documents/Development/flutter/basket_krk/lib/presentation/matches/details/view/match_details_team_table.dart` — Reused for roster display in Flutter

### Existing KMP components (reuse these)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerStatsTable.kt` — Scroll pattern to follow for roster
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/components/PlayerRecordsTab.kt` — Records list pattern to follow
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsViewModel.kt` — Current ViewModel to extend
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/teamdetails/TeamDetailsScreen.kt` — Current screen with stub tabs
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/DropdownFormField.kt` — Filter dropdown component

### Domain models (already built in Phase 3)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamResult.kt` — Result model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecord.kt` — Record model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordStatOption.kt` — Stat category enum
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordRange.kt` — Range enum
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerWithStat.kt` — Roster player model

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `PlayerStatsTable` pattern: 4-layer Box synchronized scroll — adapt for roster (player name as fixed column instead of S/LGE/TEAM)
- `PlayerRecordsTab` pattern: LazyColumn of RecordItem — adapt for team records with position + player name
- `DropdownFormField`: Reuse for season, stat category, and range dropdowns
- `StatOption` sealed class: For roster table column headers
- `Stat.getValueForGivenOption`: For roster stat values with avg/total toggle
- `TeamRecordStatOption` and `TeamRecordRange` enums: Already have `apiKey` for building `cat` parameter

### Established Patterns (from Phase 2)
- ViewModel `onSeasonSelected` → force re-fetch (already in TeamDetailsViewModel)
- ViewModel `onSortByStat` → in-memory sort with ascending/descending toggle
- ViewModel `onStatDisplayTypeChanged` → client-side AVG/SUM switch
- Navigation via lambda callbacks threaded from App.kt

### Integration Points
- `TeamDetailsScreen.kt` — Replace 3 stub tab composables
- `TeamDetailsViewModel.kt` — Add roster sort/filter state, record filter state, handlers
- `App.kt` — Add `onNavigateToPlayer` callback for roster player clicks

</code_context>

<specifics>
## Specific Ideas

- 1:1 migration from Flutter — match all tab content behavior
- Results tab: LazyColumn of result items (not reusing the table component)
- Roster tab: Stat table like PlayerStatsTable but with player name as fixed column
- Records tab: Two dropdowns (stat + range) → API call with composite `cat` parameter → LazyColumn of record entries
- Record entries may navigate to player OR match depending on the record type

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 04-teamdetails-screen*
*Context gathered: 2026-03-17*
