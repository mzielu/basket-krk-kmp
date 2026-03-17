# Phase 4: TeamDetails Screen - Research

**Researched:** 2026-03-17
**Domain:** Compose Multiplatform — tab UI, synchronized-scroll stat table, lazy lists, dropdowns
**Confidence:** HIGH

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- Match Flutter: each result item shows date, opponent name/logo, score, W/L badge, league info
- Results are clickable — tapping a result navigates to MatchDetails (TRES-03)
- Season dropdown above the results list for filtering (TRES-02)
- Simple LazyColumn of result items (not a table)
- Mirror Phase 2's PlayerStatsTable pattern for roster: fixed player name column, scrollable stat columns
- Same synchronized scroll, sort by column header, avg/total toggle above table
- Player name clickable — navigates to PlayerDetails (TROS-05)
- Season dropdown above the roster table for filtering (TROS-02)
- Match Flutter: two dropdown selectors for records — one for stat category, one for range
- Each record entry shows: position, player name, value, and optionally calculated percentages/averages
- Records are clickable — navigate to associated player or match (TREC-04)
- Changing either records filter triggers a new API call with the composite `cat` parameter
- Reuse MatchDetailsTeamTable scroll pattern for roster table
- Filter dropdowns styled like DropdownFormField from MatchesScreen
- Empty state handling for each tab
- 1:1 migration from Flutter — match all tab content behavior
- Results tab: LazyColumn of result items (not reusing the table component)
- Roster tab: Stat table like PlayerStatsTable but with player name as fixed column
- Records tab: Two dropdowns (stat + range) → API call with composite `cat` parameter → LazyColumn of record entries
- Record entries may navigate to player OR match depending on the record type

### Claude's Discretion
- Result item card/row design (W/L badge styling, score layout)
- Exact roster table column widths
- Record entry layout design
- Empty state messaging
- How to handle record entries that have both player and match navigation options

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| TRES-01 | User can view match results as a list showing date, opponent, score, W/L status | TeamResultItem layout pattern from Flutter; TeamResult domain model; MatchStatus.key for W/L resolution |
| TRES-02 | User can filter results by season using a dropdown selector | DropdownFormField<Season> already exists; TeamDetailsViewModel.onSeasonSelected already handles re-fetch; season list from teamDetails.seasons |
| TRES-03 | User can click a result to navigate to match details | TeamResultsTab already receives onMatchPress: (Int) -> Unit; wire TeamResult.id |
| TROS-01 | User can view team roster as a scrollable stat table with player names and stat columns | 4-layer Box synchronized scroll pattern from PlayerGameLogsTable/PlayerStatsTable; LeftColumnPlayerCell for fixed player column |
| TROS-02 | User can filter roster by season using a dropdown selector | Same DropdownFormField<Season> pattern; needs ViewModel handler for roster season change |
| TROS-03 | User can toggle between average and total stat display | StatDisplayTypeToggle from PlayerStatsTab; StatDisplayType.SUM/AVG; needs rosterStatDisplayType state in ViewModel |
| TROS-04 | User can sort roster by clicking any stat column header | SortableTopRowCell from PlayerGameLogsTable; in-memory sort same as PlayerDetailsViewModel.onSortByStat; needs rosterSortOption + rosterSortAscending in ViewModel |
| TROS-05 | User can click a player name to navigate to PlayerDetails | LeftColumnPlayerCell with onClick; TeamRosterTab already receives onPlayerPress: (Int) -> Unit |
| TREC-01 | User can view team records showing position, player name, value | TeamRecord model: position, player.name, value, games, ats, sNum, matchId; record item row layout |
| TREC-02 | User can filter records by stat category (PTS, AST, REB, STL, BLK, EFF, FT, FG, 3FG) | TeamRecordStatOption enum already has all 9 values with apiKey; DropdownFormField<TeamRecordStatOption> |
| TREC-03 | User can filter records by range (All-Time, Season, Match) | TeamRecordRange enum: ALL_TIME, SEASON, MATCH with apiKey; DropdownFormField<TeamRecordRange> |
| TREC-04 | User can click a record entry to navigate to the player or match | TeamRecord.matchId nullable: if non-null → onMatchPress(matchId), else → onPlayerPress(player.id) |
</phase_requirements>

## Summary

Phase 4 is a pure UI implementation phase. All data infrastructure (use cases, repositories, domain models, ViewModel scaffolding) was completed in Phase 3. The three stub composables in `presentation/screens/teamdetails/components/` — `TeamResultsTab`, `TeamRosterTab`, `TeamRecordsTab` — each need to be replaced with full implementations. The ViewModel needs roster-specific state fields (sort option, sort direction, stat display type, roster season) and their handlers added.

The critical insight is that this phase reuses two already-proven patterns: (1) the `PlayerStatsTable` 4-layer Box synchronized scroll for the roster table, adapted to use a player name fixed column instead of S/LGE/TEAM columns, and (2) the `PlayerRecordsTab` LazyColumn structure for the records list, adapted with position + player name + value layout and dual-filter dropdowns. The results tab is the simplest: a LazyColumn of styled items using the Flutter `TeamResultItem` layout as reference.

The ViewModel already holds `selectedRecordStatOption` and `selectedRecordRange` in its ViewState and has `fetchRecordsIfNeeded()`. What is missing is the `onRecordFilterChanged(stat, range)` handler, roster-specific state fields, and their corresponding handlers.

**Primary recommendation:** Implement in three focused tasks — one per tab — plus a ViewModel extension task for the missing state/handlers. The ViewModel task must come first because the tab composables depend on state it exposes.

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Compose Multiplatform | (project-defined) | All UI composition | Project standard — all screens use it |
| `kotlinx.coroutines` / `viewModelScope` | (project-defined) | Async fetch in ViewModel | Established pattern in all existing ViewModels |
| `androidx.compose.foundation.lazy.LazyColumn` | (CMP) | Scrollable item lists | Used in PlayerRecordsTab, CompetitionsScreen |
| `androidx.compose.foundation.horizontalScroll` + `verticalScroll` | (CMP) | Synchronized scroll table | Used in PlayerStatsTable, PlayerGameLogsTable |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `BasketKrkImage` | project | Network image loading | Opponent logo in result items |
| `DropdownFormField<T>` | project | Generic dropdown | Season, stat category, range selectors |
| `StatCell` / `TopRowCell` / `TopRowCornerCell` / `LeftColumnPlayerCell` | project | Stat table cells | Roster table construction |
| `SortableTopRowCell` (from PlayerGameLogsTable) | project | Clickable sortable header | Roster column headers with sort arrow |
| `SumAvgCornerCell` | project | Top-left corner showing AVG/SUM | Roster table corner |
| `StatCellMapper` | project | Derives stat columns from Stat model | Roster scrollable columns |
| `StatDisplayTypeToggle` (from PlayerStatsTab) | project | AVG/SUM toggle widget | Roster tab toolbar |
| `buildRecordCategory()` | project | Composites stat+range apiKey | Records API call parameter |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| 4-layer Box scroll | `TableView` (two_dimensional_scrollables) | Flutter uses TableView for MatchDetailsTeamTable; KMP uses 4-layer Box because two_dimensional_scrollables has no KMP port |
| DropdownFormField | custom dropdown | DropdownFormField already exists, generic, styled correctly |

## Architecture Patterns

### Recommended Project Structure
```
presentation/screens/teamdetails/
├── TeamDetailsViewModel.kt     # Add roster state fields + handlers
├── TeamDetailsScreen.kt        # No changes needed (already wires callbacks)
└── components/
    ├── TeamResultsTab.kt       # Replace stub: season dropdown + LazyColumn
    ├── TeamRosterTab.kt        # Replace stub: season+toggle toolbar + roster table
    ├── TeamRecordsTab.kt       # Replace stub: stat+range dropdowns + records list
    └── TeamRosterTable.kt      # NEW: 4-layer Box synchronized scroll table
```

### Pattern 1: ViewModel Extension for Roster State

The current `TeamDetailsViewState` does NOT have roster sort/display fields. These must be added:

```kotlin
// Add to TeamDetailsViewState:
val rosterSortOption: StatOption? = null,
val rosterSortAscending: Boolean = false,
val rosterStatDisplayType: StatDisplayType = StatDisplayType.SUM,
val rosterSelectedSeason: Season? = null,  // tracks roster-specific season separately
```

And in `TeamDetailsViewModel`:

```kotlin
fun onRosterSortByStat(statOption: StatOption) {
    val currentSort = _viewState.value.rosterSortOption
    val currentAsc = _viewState.value.rosterSortAscending
    val newAscending = if (currentSort == statOption) !currentAsc else false
    _viewState.update { state ->
        val sortedRoster = state.roster.data?.sortedWith(compareBy {
            it.stat.getValueForGivenOption(statOption, StatDisplayType.SUM) ?: 0.0
        })?.let { if (!newAscending) it.reversed() else it }
        state.copy(
            rosterSortOption = statOption,
            rosterSortAscending = newAscending,
            roster = sortedRoster?.let { state.roster.data(it) } ?: state.roster
        )
    }
}

fun onRosterStatDisplayTypeChanged(type: StatDisplayType) {
    _viewState.update { it.copy(rosterStatDisplayType = type) }
}

fun onRecordFilterChanged(stat: TeamRecordStatOption, range: TeamRecordRange) {
    _viewState.update {
        it.copy(
            selectedRecordStatOption = stat,
            selectedRecordRange = range,
            records = ViewStateData(null)
        )
    }
    fetchRecordsIfNeeded()
}
```

**Important:** `onSeasonSelected` already resets `roster = ViewStateData(null)`. When it does, also reset `rosterSortOption = null`, `rosterSortAscending = false`.

### Pattern 2: Roster Season vs Results Season

The current ViewModel uses a single `selectedSeason` for both results AND roster. The Flutter app has separate `resultsSelectedSeason` and `rosterSelectedSeason`. The KMP ViewState already links one `selectedSeason` to both, and `onSeasonSelected` resets both `results` and `roster`. This is acceptable — both tabs share the same season selector in the header (not per-tab). The season dropdown lives in each tab according to CONTEXT.md decisions, but the source of truth is the shared `selectedSeason` in ViewState.

**Resolution:** Each tab's season dropdown reads `viewState.selectedSeason` and calls `viewModel.onSeasonSelected(season)`. This resets both tabs' data, matching existing ViewModel behavior.

### Pattern 3: 4-Layer Box Synchronized Scroll for TeamRosterTable

The roster table is structurally identical to `PlayerGameLogsTable` but with:
- Fixed left column = player name (using `LeftColumnPlayerCell`) instead of game log info
- Column headers are sortable (use `SortableTopRowCell` from `PlayerGameLogsTable`)
- Top-left corner = "Player" text (use `TopRowCornerCell`)
- Bottom row = totals (use `SumAvgCornerCell` for bottom-left, `StatCell` bold for totals)
- Stat columns driven by `StatCellMapper.getStatOptionsFromModel(roster.first().stat)`

```kotlin
// TeamRosterTable.kt — new file, mirrors PlayerStatsTable structure:
private val rosterPlayerColWidth = 120.dp   // wider for player names
private val rosterHeaderHeight = 40.dp
private val rosterRowHeight = 45.dp
private val rosterStatCellWidth = 35.dp

@Composable
fun TeamRosterTable(
    roster: List<PlayerWithStat>,
    statDisplayType: StatDisplayType,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onPlayerPress: (Int) -> Unit,
    onSortByStat: (StatOption) -> Unit,
)
```

The totals row follows `PlayerStatsTable`'s `TotalsStatLine` using `StatCellMapper.getSumStatFromStats`.

### Pattern 4: Result Item Layout

Reference: Flutter `TeamResultItem` — Row layout:
```
[vs] [opponent logo 30dp] [opponent name - expanded] | [W/L badge 28dp] [score + date column]
```

W/L badge color resolution (matches Flutter):
- `MatchStatus.IN_PROGRESS` → `BasketKrkColors.MatchInProgress`, text = "IP"
- `MatchStatus.NON_STARTED` → `BasketKrkColors.MatchNotStarted`, text = "?"
- Finished/Walkover with `points > opponent.points` → `BasketKrkColors.MatchWin`, text = "W"
- Finished/Walkover with `points < opponent.points` → `BasketKrkColors.MatchLost`, text = "L"

Playoff items get `BasketKrkColors.PlayoffsBg` background (from `MatchType.PLAYOFFS`).

The item is wrapped in a `Box` with `BorderRoundedItem` border and `RoundedCornerShape(8.dp)`, clickable for navigation.

### Pattern 5: Record Item Layout

Reference: Flutter `_teamRecordItem` in `team_details_screen.dart`:
```
[position 40dp] [player name (+ season suffix) — expanded] [value 35dp] [suffix widget 60dp] [chevron icon]
```

Suffix widget logic (from Flutter, must match):
- If `teamRecord.ats != null` → show `(X.X%)` — percentage of attempts
- Else if `teamRecord.games > 0` → show `(X.X PG)` — per-game average
- Else if `teamRecord.matchId == null` → show `XM` — count of matches
- If `teamRecord.matchId != null` → show chevron icon (navigation to match)

Player name format: `"${player.name}"` + if `sNum != null` append `" (SX)"` where X = season number.

Navigation: if `matchId != null` → `onMatchPress(matchId)`, else → `onPlayerPress(player.id)`.

### Pattern 6: Records Filter → Re-fetch

When either dropdown changes, call `onRecordFilterChanged(stat, range)` which:
1. Updates `selectedRecordStatOption` and `selectedRecordRange` in ViewState
2. Resets `records = ViewStateData(null)` (clears cache)
3. Calls `fetchRecordsIfNeeded()` which now sees null data and fires API call

This avoids duplicate fetches and matches the Flutter BLoC `TeamDetailsRecordInputChangedEvent`.

### Anti-Patterns to Avoid
- **Adding per-tab season state to ViewModel:** Both tabs read the shared `selectedSeason`. Don't create `rosterSelectedSeason` as a separate field in ViewState — it would desync.
- **Calling onSeasonSelected from roster season dropdown when the dropdown is on results tab:** Season dropdown belongs to the correct tab but both call the same `onSeasonSelected`. This is correct — season change resets both.
- **Mutable state inside composables for roster sort:** Sort state must live in ViewModel so it survives recomposition and tab switching.
- **Using `data class` copy inside composable lambda directly:** Always delegate sort/filter to ViewModel handlers.
- **Sorting roster in-place on original data:** Store sorted roster back in ViewState — do not sort a local copy in the composable.

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Synchronized horizontal+vertical scroll table | Custom scroll syncing | 4-layer Box pattern (see PlayerStatsTable/PlayerGameLogsTable) | Already proven — 4 layers: body, pinned top row, pinned left column, corner |
| Generic dropdown with selected highlighting | Custom dropdown | `DropdownFormField<T>` | Already handles expanded state, selection highlight, read-only |
| Stat cell rendering | Custom text cell | `StatCell` | Handles alt background, seconds formatting, bold for totals |
| Stat column list derivation | Manual column list | `StatCellMapper.getStatOptionsFromModel(stat)` | Handles optional columns (StatSeconds, StatMvp, etc.) |
| Totals row sum | Manual sum | `StatCellMapper.getSumStatFromStats(list)` | Handles nullable fields with correct null-propagation |
| AVG/SUM toggle | Custom toggle | `StatDisplayTypeToggle` (from PlayerStatsTab) | Already styled to project spec |
| Top-left corner cell | Custom Box | `TopRowCornerCell` | Correct shape, color, text style |
| Player left column cell | Custom Box | `LeftColumnPlayerCell` | Handles jersey number suffix, AutoSizeText for long names |
| W/L color logic | Custom when | Match Flutter color mapping in result item | Exact same 4-case logic as Flutter |

## Common Pitfalls

### Pitfall 1: Roster Season Desyncs from Results Season
**What goes wrong:** If a separate `rosterSelectedSeason` state is added to ViewModel, calling `onSeasonSelected` only resets one tab's data, and the dropdowns show different seasons.
**Why it happens:** Over-engineering separate season tracking per tab.
**How to avoid:** Both tabs read `viewState.selectedSeason`. Both call `viewModel.onSeasonSelected()`. The single shared season is the source of truth.
**Warning signs:** Roster dropdown shows season X but results dropdown shows season Y after a switch.

### Pitfall 2: Record Filter Change Without Cache Invalidation
**What goes wrong:** Changing stat category/range dropdown does not trigger a re-fetch because `records.data != null` from the previous filter result.
**Why it happens:** `fetchRecordsIfNeeded()` has an early return guard `if (current.data != null && !current.isError) return`.
**How to avoid:** `onRecordFilterChanged` must reset `records = ViewStateData(null)` BEFORE calling `fetchRecordsIfNeeded()`. This is the same pattern as `onSeasonSelected` resetting `results` and `roster`.
**Warning signs:** Changing dropdown has no effect — list stays the same.

### Pitfall 3: Roster Sort State Lost on Tab Switch
**What goes wrong:** Tapping away from Roster tab and back resets sort arrows, and list is re-rendered unsorted.
**Why it happens:** Sort state stored as `remember` in composable is lost on tab switch if the composable leaves composition.
**How to avoid:** Sort state (`rosterSortOption`, `rosterSortAscending`) must live in `TeamDetailsViewState`, not in the composable. The composable reads from ViewState.
**Warning signs:** Sort arrow disappears after switching tabs.

### Pitfall 4: Roster Season Change Does Not Reset Sort
**What goes wrong:** User sorts roster by PTS, then changes season. New roster appears but with stale sort state, showing wrong sort arrow.
**Why it happens:** `onSeasonSelected` resets `roster = ViewStateData(null)` but not `rosterSortOption`/`rosterSortAscending`.
**How to avoid:** In `onSeasonSelected`, also reset `rosterSortOption = null, rosterSortAscending = false`.
**Warning signs:** Sort arrow persists after season change.

### Pitfall 5: TeamRosterTable Totals Row Uses Wrong Division Value
**What goes wrong:** Totals row shows average per player count instead of average per match count when in AVG mode.
**Why it happens:** `getValueForGivenOptionWithSeasonsCount` is for multi-season player stats; roster totals should use `getValueForGivenOption` directly because each `PlayerWithStat` already holds its own `stat.m` match count.
**How to avoid:** For roster totals row, use `StatCellMapper.getSumStatFromStats(roster.map { it.stat })` to get the summed stat, then render with `getValueForGivenOption(statOption, statDisplayType)` — same as `PlayerStatsTable`'s `TotalsStatLine` (not `getValueForGivenOptionWithSeasonsCount`).
**Warning signs:** Totals row shows fractional averages that don't match Flutter's totals.

### Pitfall 6: Record Navigation When Both matchId and player.id Exist
**What goes wrong:** All records navigate to player even when a match association exists.
**Why it happens:** Checking `player.id != null` (always true since PlayerShort.id is non-null Int) instead of checking `matchId`.
**How to avoid:** Navigation logic: `if (record.matchId != null) onMatchPress(record.matchId) else onPlayerPress(record.player.id)`. The `matchId` field is nullable in `TeamRecord`.
**Warning signs:** Tapping a record with a match association opens player instead of match.

## Code Examples

### TeamResultsTab with season dropdown and lazy list
```kotlin
// Based on: Flutter _resultsMainView + TeamResultItem patterns
@Composable
fun TeamResultsTab(
    resultList: TeamResultList,
    seasons: List<Season>,
    selectedSeason: Season?,
    onSeasonSelected: (Season) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        DropdownFormField(
            label = "Season",
            options = seasons,
            selectedOption = selectedSeason,
            onOptionSelected = onSeasonSelected,
            readableValue = { it?.num?.toString() ?: "" },
            modifier = Modifier.width(100.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(resultList.data) { result ->
                TeamResultItem(result = result, onClick = { onMatchPress(result.id) })
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
```

### TeamResultItem W/L badge color resolution
```kotlin
// Based on: Flutter TeamResultItem._resolveMatchSignColor / _resolveMatchSignText
private fun resolveMatchSign(result: TeamResult): Pair<Color, String> = when (result.status) {
    MatchStatus.IN_PROGRESS -> BasketKrkColors.MatchInProgress to "IP"
    MatchStatus.NON_STARTED -> BasketKrkColors.MatchNotStarted to "?"
    else -> if (result.points > result.opponent.points)
        BasketKrkColors.MatchWin to "W"
    else
        BasketKrkColors.MatchLost to "L"
}
```

### TeamRosterTab with toolbar and table
```kotlin
// Based on: Flutter _rosterMainView + PlayerStatsTab pattern
@Composable
fun TeamRosterTab(
    roster: List<PlayerWithStat>,
    seasons: List<Season>,
    selectedSeason: Season?,
    statDisplayType: StatDisplayType,
    sortOption: StatOption?,
    sortAscending: Boolean,
    onSeasonSelected: (Season) -> Unit,
    onStatDisplayTypeChanged: (StatDisplayType) -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onPlayerPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            DropdownFormField(
                label = "Season",
                options = seasons,
                selectedOption = selectedSeason,
                onOptionSelected = onSeasonSelected,
                readableValue = { it?.num?.toString() ?: "" },
                modifier = Modifier.width(100.dp)
            )
            Spacer(Modifier.weight(1f))
            StatDisplayTypeToggle(statDisplayType = statDisplayType, onToggle = onStatDisplayTypeChanged)
        }
        Spacer(Modifier.height(8.dp))
        if (roster.isEmpty()) {
            // empty state
        } else {
            TeamRosterTable(
                roster = roster,
                statDisplayType = statDisplayType,
                sortOption = sortOption,
                sortAscending = sortAscending,
                onPlayerPress = onPlayerPress,
                onSortByStat = onSortByStat,
            )
        }
    }
}
```

### TeamRecordsTab with dual filter
```kotlin
// Based on: Flutter _recordsMainView
@Composable
fun TeamRecordsTab(
    records: List<TeamRecord>,
    selectedStatOption: TeamRecordStatOption,
    selectedRange: TeamRecordRange,
    onFilterChanged: (TeamRecordStatOption, TeamRecordRange) -> Unit,
    onPlayerPress: (Int) -> Unit,
    onMatchPress: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp, start = 4.dp, end = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            DropdownFormField(
                label = "Range",
                options = TeamRecordRange.entries,
                selectedOption = selectedRange,
                onOptionSelected = { onFilterChanged(selectedStatOption, it) },
                readableValue = { it?.name ?: "" },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
            DropdownFormField(
                label = "Category",
                options = TeamRecordStatOption.entries,
                selectedOption = selectedStatOption,
                onOptionSelected = { onFilterChanged(it, selectedRange) },
                readableValue = { it?.name ?: "" },
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(records) { record ->
                TeamRecordItem(
                    record = record,
                    onClick = {
                        if (record.matchId != null) onMatchPress(record.matchId)
                        else onPlayerPress(record.player.id)
                    }
                )
            }
        }
    }
}
```

### Record suffix calculation (from Flutter)
```kotlin
// Based on: Flutter _suffixWidget logic
fun buildRecordSuffix(record: TeamRecord): String = when {
    record.ats != null -> "(${String.format("%.1f", record.value.toDouble() / record.ats * 100)}%)"
    record.games > 0   -> "(${String.format("%.1f", record.value.toDouble() / record.games)} PG)"
    record.matchId == null -> "${record.games}M"
    else -> ""
}
```

### ViewModel additions for roster sort
```kotlin
// Add to TeamDetailsViewModel — mirrors PlayerDetailsViewModel.onSortByStat
fun onRosterSortByStat(statOption: StatOption) {
    val currentSort = _viewState.value.rosterSortOption
    val currentAsc = _viewState.value.rosterSortAscending
    val newAscending = if (currentSort == statOption) !currentAsc else false
    _viewState.update { state ->
        val sorted = state.roster.data?.sortedWith(
            compareBy { it.stat.getValueForGivenOption(statOption, StatDisplayType.SUM) ?: 0.0 }
        )?.let { if (!newAscending) it.reversed() else it }
        state.copy(
            rosterSortOption = statOption,
            rosterSortAscending = newAscending,
            roster = sorted?.let { state.roster.data(it) } ?: state.roster
        )
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Flutter TableView (two_dimensional_scrollables) | KMP 4-layer Box synchronized scroll | Phase 2 decision | No external dep needed; pattern proven in PlayerGameLogsTable |
| Flutter separate resultsSelectedSeason / rosterSelectedSeason | KMP single shared selectedSeason | Phase 3 ViewModel decision | Simpler state; season dropdown in each tab calls same handler |
| Stub composables returning placeholder Text | Full implementations | This phase | All 3 tabs become functional |

## Open Questions

1. **StatDisplayTypeToggle import path**
   - What we know: `StatDisplayTypeToggle` is a private composable inside `PlayerStatsTab.kt`
   - What's unclear: Whether to extract it to a shared location or duplicate it in `TeamRosterTab.kt`
   - Recommendation: Extract to `presentation/base/ui/StatDisplayTypeToggle.kt` as a shared composable. Both `PlayerStatsTab` and `TeamRosterTab` need it. One source of truth avoids drift.

2. **SortableTopRowCell import path**
   - What we know: `SortableTopRowCell` is a private composable inside `PlayerGameLogsTable.kt`
   - What's unclear: Whether to extract or duplicate for `TeamRosterTable`
   - Recommendation: Same as above — extract to `presentation/base/ui/SortableTopRowCell.kt`. The roster table is the second consumer.

3. **Human-readable labels for TeamRecordRange and TeamRecordStatOption**
   - What we know: Flutter uses `.label` property (e.g., `TeamRecordRange.ALL_TIME.label = "All-Time"`). KMP enums only have `apiKey`.
   - What's unclear: Whether to add a `label` property to the domain enums or define label mapping in the presentation layer.
   - Recommendation: Add `displayName: String` to each enum value in the domain module — consistent with how Flutter defines them. This keeps the label co-located with the data.

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | None detected — no test files or test config found in repository |
| Config file | None |
| Quick run command | N/A |
| Full suite command | N/A |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| TRES-01 | Result item renders date, opponent, score, W/L | manual-only | N/A | No test infra |
| TRES-02 | Season dropdown triggers results re-fetch | manual-only | N/A | No test infra |
| TRES-03 | Tapping result navigates to MatchDetails | manual-only | N/A | No test infra |
| TROS-01 | Roster table renders with fixed player column | manual-only | N/A | No test infra |
| TROS-02 | Season dropdown triggers roster re-fetch | manual-only | N/A | No test infra |
| TROS-03 | AVG/SUM toggle changes stat values | manual-only | N/A | No test infra |
| TROS-04 | Column header tap sorts roster | manual-only | N/A | No test infra |
| TROS-05 | Player name tap navigates to PlayerDetails | manual-only | N/A | No test infra |
| TREC-01 | Record entry shows position, name, value | manual-only | N/A | No test infra |
| TREC-02 | Stat category dropdown triggers re-fetch | manual-only | N/A | No test infra |
| TREC-03 | Range dropdown triggers re-fetch | manual-only | N/A | No test infra |
| TREC-04 | Record tap navigates to player or match | manual-only | N/A | No test infra |

**Justification for manual-only:** The project has zero test infrastructure — no test directories, no test config, no test files. All verification is via manual device/emulator testing. This matches the pattern of Phases 1-3 which also used no automated tests.

### Sampling Rate
- **Per task commit:** Manual: run the app, navigate to a team, exercise the relevant tab
- **Per wave merge:** Manual: full TeamDetails screen walkthrough on both Android and iOS
- **Phase gate:** All 3 tabs functional with correct navigation before `/gsd:verify-work`

### Wave 0 Gaps
None — existing test infrastructure covers all phase requirements (n/a: project has no test infrastructure and this is not a gap to fill in this phase).

## Sources

### Primary (HIGH confidence)
- Direct read of `TeamDetailsViewModel.kt` — current state and missing handlers identified
- Direct read of `TeamDetailsScreen.kt` — stub tab signatures confirmed
- Direct read of `TeamResultsTab.kt`, `TeamRosterTab.kt`, `TeamRecordsTab.kt` — stub bodies confirmed
- Direct read of `PlayerStatsTable.kt` — 4-layer Box synchronized scroll pattern documented
- Direct read of `PlayerGameLogsTable.kt` — SortableTopRowCell, in-memory sort pattern
- Direct read of `PlayerStatsTab.kt` — StatDisplayTypeToggle, onStatDisplayTypeChanged pattern
- Direct read of `PlayerDetailsViewModel.kt` — onSortByStat, onStatDisplayTypeChanged implementations
- Direct read of Flutter `team_details_screen.dart` — result item layout, records item layout, suffix widget logic
- Direct read of Flutter `team_result_item.dart` — W/L badge color/text resolution logic
- Direct read of domain models: `TeamResult`, `TeamRecord`, `TeamResultList`, `PlayerWithStat`, `TeamRecordStatOption`, `TeamRecordRange`, `Stat`, `MatchStatus`, `StatDisplayType`
- Direct read of `StatCellMapper.kt` — `getStatOptionsFromModel`, `getSumStatFromStats`
- Direct read of `DropdownFormField.kt` — signature and usage pattern
- Direct read of `LeftColumnPlayerCell.kt` — player name with jersey number, AutoSizeText
- Direct read of `BasketKrkColors.kt` — MatchWin, MatchLost, MatchInProgress, MatchNotStarted, PlayoffsBg

### Secondary (MEDIUM confidence)
None — all findings are from direct code inspection

### Tertiary (LOW confidence)
None

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — all libraries confirmed by direct file reads
- Architecture: HIGH — all patterns verified in existing KMP files; Flutter reference verified
- Pitfalls: HIGH — derived from direct code analysis of existing patterns and Flutter source

**Research date:** 2026-03-17
**Valid until:** 2026-04-17 (stable project, no external dependencies changing)
