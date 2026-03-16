# Phase 2: PlayerDetails Screen - Research

**Researched:** 2026-03-16
**Domain:** Compose Multiplatform — scrollable stat tables, sort/filter state, avg/total toggle, records list, navigation callbacks
**Confidence:** HIGH

## Summary

Phase 2 replaces three placeholder `Text()` composables inside `PlayerDetailsScreen` with fully functional tab content. All three tabs reuse patterns already established in the project. The Game Logs and Stats tabs both reuse `MatchDetailsTeamTable`'s synchronized-scroll architecture with custom left-column content. The Records tab is a simple `LazyColumn`. All state changes (sort, filter, toggle) live in `PlayerDetailsViewModel` as new fields on `PlayerDetailsViewState`.

The Flutter BLoC reference is the ground truth for behavior. The KMP project already has direct equivalents for every widget Flutter uses: `DropdownFormField` mirrors `DropdownButtonFormField`, `StatCellMapper` mirrors Dart's `StatCellMapper`, and `StatDisplayType.SUM/AVG` maps 1:1 to Flutter's `tot/avg`. The one missing piece is `getValueForGivenOptionWithSeasonsCount` on `Stat` — it does not exist in KMP yet and must be added for the stats totals row.

Navigation to `TeamDetails` (PSTA-04) is a forward reference. `Screen.TeamDetails` does not exist yet; the callback must be threaded through with a no-op stub in `App.kt` that will be wired in Phase 3.

**Primary recommendation:** Build three new composable components (`PlayerGameLogsTab`, `PlayerStatsTab`, `PlayerRecordsTab`) and corresponding new ViewModel state/handlers. Do NOT modify `MatchDetailsTeamTable` — create new table composables that follow its pattern with different left-column content.

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- Reuse `MatchDetailsTeamTable` component — only the first left column is fixed, same synchronized scrolling pattern
- Fixed left column shows: date + opponent name + result (W/L + score) matching Flutter's game log table layout
- Clicking the fixed column (date/result cell) navigates to MatchDetails for that game (PLOG-05)
- Stat column headers are clickable to sort game logs by that stat (PLOG-04)
- Sort direction indicated visually on the active column header
- Reuse `MatchDetailsTeamTable` with fixed columns: Season, League, Team (for Stats tab)
- Team name in the fixed column is clickable — navigates to TeamDetails (PSTA-04)
- Avg/total toggle button placed above the table, matching Flutter layout
- Toggle switches all stat values between averages and totals (PSTA-02)
- Totals row at the bottom of the table (PSTA-03)
- ListView (not a table) matching Flutter's records tab
- Each record item shows: record type name, value, times achieved, date
- Clicking a record navigates to the associated match (PREC-02)
- Empty state when no records exist
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

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| PLOG-01 | User can view game logs as a scrollable stat table with fixed player/opponent column and scrollable stat columns | `MatchDetailsTeamTable` scroll architecture; new `PlayerGameLogsTable` composable with `PlayerLog`-based left column |
| PLOG-02 | User can filter game logs by season using a dropdown selector | `DropdownFormField` reuse; `fetchGameLogsIfNeeded(seasonId)` already exists in ViewModel; extend to force re-fetch on season change |
| PLOG-03 | User can filter game logs by team (when player played for multiple teams in a season) | `PlayerLogList` contains `List<PlayerLogByTeam>`; client-side selection of which `PlayerLogByTeam` to display; ViewModel holds `selectedTeam: PlayerLogByTeam?` |
| PLOG-04 | User can sort game logs by clicking any stat column header | Sort state `sortOption: StatOption?` + `sortAscending: Boolean` in ViewState; in-memory sort of `PlayerLog` list by `stat.getValueForGivenOption(statOption, SUM)` |
| PLOG-05 | User can click a game log row to navigate to the match details | `onNavigateToMatch: (Int) -> Unit` callback on `PlayerDetailsScreen`; wired through `App.kt` → `navController.navigate(Screen.MatchDetails(id))` |
| PSTA-01 | User can view aggregated stats per season/team/league in a scrollable stat table | New `PlayerStatsTable` composable; fixed left columns are Season/League/Team (NonSummable `StatOption` objects); scrollable body uses same pattern as `MatchDetailsTeamTable` |
| PSTA-02 | User can toggle between average and total stat display | `statDisplayType: StatDisplayType` in ViewState; toggle triggers `_viewState.update { it.copy(statDisplayType = ...) }`; `Stat.getValueForGivenOption(statOption, statDisplayType)` drives values |
| PSTA-03 | User can see a totals row at the bottom of the stats table | Requires new `Stat.getValueForGivenOptionWithSeasonsCount()` extension function on KMP `Stat` (mirrors Flutter); totals row uses `StatCellMapper.getSumStatFromStats()` + that extension |
| PSTA-04 | User can click a team name in stats to navigate to TeamDetails | `onNavigateToTeam: (Int) -> Unit` callback; wired as no-op stub in `App.kt` (TeamDetails screen not yet in `Screen` sealed class — add stub or leave TODO) |
| PREC-01 | User can view a list of player record achievements (type, value, times, date) | `LazyColumn` over `List<PlayerRecord>`; each item uses existing `BasketKrkStyles.recordValue/recordStatSign/recordDescription` styles |
| PREC-02 | User can click a record to navigate to the associated match | `PlayerRecord.matchId` → `onNavigateToMatch(matchId)` callback (same callback as PLOG-05) |
</phase_requirements>

---

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Compose Multiplatform (already in project) | current | UI framework | Entire app uses it |
| `androidx.compose.foundation` | current | `LazyColumn`, `horizontalScroll`, `verticalScroll`, `rememberScrollState` | Already imported in `MatchDetailsTeamTable` |
| `androidx.compose.material3` | current | `ToggleButton` / `Row` for avg/total toggle | Already used in `PlayerDetailsScreen` |
| Kermit Logger (co.touchlab.kermit) | current | Error logging in ViewModel | Already used in `PlayerDetailsViewModel` |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| `org.jetbrains.compose.resources` | current | String resources for record type descriptions, empty state text | Adding new string keys for record type labels |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Manual synchronized scroll (current `MatchDetailsTeamTable` approach) | `LazyTable` / `BasicLazyLayout` | Manual scroll sharing is already working and battle-tested in this codebase; no reason to change |
| `ToggleButton` group for avg/total | `SegmentedButton` | `SegmentedButton` is Material3 but adds dependency on experimental API; simple `Row` with two styled `Button`s suffices |

**Installation:** No new dependencies needed. All required libraries are already in the project.

---

## Architecture Patterns

### Recommended File Structure for Phase 2

```
presentation/src/commonMain/kotlin/.../screens/playerdetails/
├── PlayerDetailsScreen.kt              # MODIFY — replace 3 placeholder Texts
├── PlayerDetailsViewModel.kt           # MODIFY — add sort/filter/toggle state + handlers
├── components/
│   ├── PlayerGameLogsTab.kt            # NEW — full Game Logs tab composable
│   ├── PlayerGameLogsTable.kt          # NEW — scrollable table with PlayerLog left column
│   ├── PlayerStatsTab.kt               # NEW — full Stats tab composable (toggle + table)
│   ├── PlayerStatsTable.kt             # NEW — scrollable table with Season/League/Team fixed cols
│   └── PlayerRecordsTab.kt             # NEW — LazyColumn records list

domain/src/commonMain/kotlin/.../model/
└── Stat.kt                             # MODIFY — add getValueForGivenOptionWithSeasonsCount()
```

### Pattern 1: Stats Table with Multi-Column Fixed Left Section

The existing `MatchDetailsTeamTable` has a single fixed left column. For the Stats tab, three columns (Season, League, Team) must be fixed. The pattern is the same — use `NonSummable` stat options as the first columns in the left pinned section and handle them specially.

Flutter approach: `pinnedColumnCount: 3` in `TableView.builder`, dispatching on `vicinity.column` in `cellBuilder`. The KMP approach mirrors this with a separate `Column` for fixed content and a `Box(horizontalScroll(hScroll))` for the scrollable body, sharing the same `hScroll` and `vScroll` states.

**Key difference from `MatchDetailsTeamTable`:** The fixed left section in the Stats table contains *three* fixed columns (Season, League, Team) rendered using `StatSeason`, `StatLeague`, `StatTeam` objects, not a player name. The combined fixed width will be wider (e.g., `120.dp` Season + `60.dp` League + `80.dp` Team = `260.dp`).

```kotlin
// Conceptual pattern for PlayerStatsTable (Stats tab)
val fixedStatOptions = listOf(StatSeason, StatLeague, StatTeam)  // NonSummable
val scrollableStatOptions = StatCellMapper.getStatOptionsFromModel(playersStats.first().stat)

val hScroll = rememberScrollState()
val vScroll = rememberScrollState()

Box(modifier = Modifier.fillMaxSize()) {
    // Scrollable body — stat values
    Row(modifier = Modifier
        .padding(start = fixedColsTotalWidth, top = headerHeight)
        .fillMaxSize()
        .horizontalScroll(hScroll)
        .verticalScroll(vScroll)
    ) { /* stat cells */ }

    // Pinned top row — stat option headers
    Row(modifier = Modifier
        .padding(start = fixedColsTotalWidth)
        .height(headerHeight)
        .fillMaxWidth()
        .horizontalScroll(hScroll)
    ) { /* TopRowCell for each scrollable stat option */ }

    // Pinned left multi-column section
    Row(modifier = Modifier
        .width(fixedColsTotalWidth)
        .padding(top = headerHeight)
        .fillMaxHeight()
        .verticalScroll(vScroll)
    ) { /* Season + League + Team fixed cells; Team cell clickable */ }

    // Top-left corner
    Row(modifier = Modifier
        .width(fixedColsTotalWidth)
        .height(headerHeight)
    ) { /* headers: S, LGE, TEAM */ }
}
```

### Pattern 2: ViewModel Sort State for Game Logs

Flutter sorts in-memory inside `PLayerDetailsSortGameLogsEvent`. The KMP equivalent sorts the currently displayed `PlayerLogByTeam.logs` list inside the ViewModel.

Sort state is tracked in `PlayerDetailsViewState`:

```kotlin
// Additions to PlayerDetailsViewState
val sortOption: StatOption? = null,
val sortAscending: Boolean = false,   // false = descending (highest first, matching Flutter)
val selectedTeam: PlayerLogByTeam? = null,
val statDisplayType: StatDisplayType = StatDisplayType.SUM,
```

Sort handler in ViewModel:

```kotlin
fun onSortByStat(statOption: StatOption) {
    val currentSort = _viewState.value.sortOption
    val currentAsc = _viewState.value.sortAscending
    val newAscending = if (currentSort == statOption) !currentAsc else false // toggle or default desc
    _viewState.update { state ->
        val sorted = state.selectedTeam?.logs?.sortedWith(compareBy {
            it.stat.getValueForGivenOption(statOption, StatDisplayType.SUM) ?: 0.0
        })?.let { if (!newAscending) it.reversed() else it }
        state.copy(
            sortOption = statOption,
            sortAscending = newAscending,
            selectedTeam = sorted?.let { state.selectedTeam?.copy(logs = it) }
        )
    }
}
```

### Pattern 3: Season Change Forces Re-fetch

Flutter's `PlayerDetailsGameLogsSeasonChangedEvent` always re-fetches (does not use cache check). The KMP ViewModel must bypass the `if (current.data != null && !current.isError) return` guard when season changes.

```kotlin
fun onSeasonSelected(season: Season) {
    _viewState.update { it.copy(selectedSeason = season, sortOption = null) }
    // Force re-fetch — do not use fetchGameLogsIfNeeded (which skips if data exists)
    viewModelScope.launch {
        _viewState.update { it.copy(gameLogs = it.gameLogs.loading()) }
        getPlayerGameLogs(input = GetPlayerGameLogsUseCase.Input(playerId, season.id))
            .onSuspendSuccess { logs -> _viewState.update { it.copy(gameLogs = it.gameLogs.data(logs)) } }
            .onSuspendGeneralError { error -> /* ... */ }
    }
}
```

### Pattern 4: Team Filter is Client-Side

Flutter's `PlayerDetailsGameLogsTeamChangedEvent` only changes the selected `PlayerLogByTeam` in state — no API call. The `PlayerLogList` (all teams) is already fetched; team change is purely a display filter.

```kotlin
fun onTeamSelected(playerLogByTeam: PlayerLogByTeam) {
    _viewState.update { it.copy(selectedTeam = playerLogByTeam, sortOption = null) }
}
```

After a season fetch completes, `selectedTeam` should be auto-set to `gameLogs.data?.data?.firstOrNull()`.

### Pattern 5: Stats Totals Row

The Flutter stats table has a `rowCount: playersStats.length + 2` (+1 for header, +1 for totals). The totals row uses `getValueForGivenOptionWithSeasonsCount`. This function does not exist in KMP — it must be added to `Stat.kt`:

```kotlin
// Add to Stat.kt
fun Stat.getValueForGivenOptionWithSeasonsCount(
    statOption: StatOption,
    statDisplayType: StatDisplayType,
    seasonCount: Int
): Double? {
    if (statOption == StatMatches) {
        return m?.let {
            when (statDisplayType) {
                StatDisplayType.SUM -> it.toDouble()
                StatDisplayType.AVG -> it.toDouble() / seasonCount
            }
        }
    }
    return getValueForGivenOption(statOption, statDisplayType)
}
```

For `NonSummable` columns in the totals row (Season, League, Team): render empty string, same as Flutter.

### Pattern 6: PlayerStat Fixed-Column Text

Flutter's `PlayerStat.toReadableStatOptionText()` handles Season/League/Team text in fixed columns. This extension does not exist in KMP. Must be added as an extension on `PlayerStat`:

```kotlin
// Add to PlayerStat.kt
fun PlayerStat.toReadableStatOptionText(statOption: StatOption, statDisplayType: StatDisplayType): String {
    return when (statOption) {
        StatTeam -> team.name
        StatLeague -> league.name
        StatSeason -> season.toString()
        else -> stat.getValueForGivenOption(statOption, statDisplayType)
                    ?.toReadableStatOptionText(statOption) ?: ""
    }
}
```

### Pattern 7: Navigation Callback Threading

`PlayerDetailsScreen` currently only has `onNavigateBack`. Two new callbacks must flow from `App.kt` through `PlayerDetailsScreen` to the tab composables:

```kotlin
// PlayerDetailsScreen signature change
@Composable
fun PlayerDetailsScreen(
    viewModel: PlayerDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMatch: (Int) -> Unit,     // NEW
    onNavigateToTeam: (Int) -> Unit,      // NEW
)
```

In `App.kt`:
```kotlin
PlayerDetailsScreen(
    viewModel = viewModel,
    onNavigateBack = { navController.popBackStack() },
    onNavigateToMatch = { navController.navigate(Screen.MatchDetails(matchId = it)) },
    onNavigateToTeam = { /* TODO Phase 3 — Screen.TeamDetails not yet in Screen sealed class */ },
)
```

`Screen.TeamDetails` stub can be added to `Screen.kt` with a `teamId: Int` parameter so the callback signature is correct even though the composable route doesn't exist yet.

### Anti-Patterns to Avoid

- **Modifying `MatchDetailsTeamTable` directly:** It serves `MatchDetails` — creating new table composables keeps each use case isolated.
- **Putting sort logic in the Composable:** Sort belongs in the ViewModel. The table composable only fires `onSortByStat(statOption)`.
- **Re-fetching stats/records on filter change:** Only game logs season change triggers a re-fetch. Team change and avg/total toggle are purely client-side.
- **Using `remember(playersWithStats)` for sorted lists:** The sorted list must come from ViewModel state, not be computed inside the Composable.

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Synchronized horizontal + vertical scroll | Custom `ScrollState` linkage | Existing `MatchDetailsTeamTable` scroll pattern (`rememberScrollState()` shared across Box layers) | Already tested and working in MatchDetails |
| Dropdown selectors | Custom dropdown | `DropdownFormField` | Already implements Material3 `ExposedDropdownMenuBox` with selected-item highlight |
| Stat value formatting | Custom number/time formatter | `Double.toReadableStatOptionText(statOption)` from `Stat.kt` | Handles minutes:seconds, percentages, integer-like doubles |
| Stat column list derivation | Manual hardcoded list | `StatCellMapper.getStatOptionsFromModel(stat)` | Derives correct columns dynamically from what the stat object contains |
| Sum stat for totals row | Manual summation | `StatCellMapper.getSumStatFromStats(stats.map { it.stat })` | Already handles nullable fields correctly |
| Win/Loss color coding | Custom color logic | `BasketKrkColors.MatchWin`, `.MatchLost`, `.MatchInProgress` | Consistent with Flutter colors |

---

## Common Pitfalls

### Pitfall 1: Sort State Reset on Season Change

**What goes wrong:** When a new season is fetched, the `sortOption` still holds the previous sort. The newly fetched list is unsorted but the header still shows the sort indicator.
**Why it happens:** Season change triggers a fresh fetch but `sortOption` is not cleared.
**How to avoid:** In `onSeasonSelected`, reset `sortOption = null` and `selectedTeam = null` in the state update before launching the fetch.
**Warning signs:** Sort indicator stuck on wrong column after season change.

### Pitfall 2: Team Dropdown Not Populated Until Season Fetch Completes

**What goes wrong:** Season dropdown changes while team dropdown still shows teams from the previous season's `PlayerLogList`.
**Why it happens:** The team dropdown is driven by `gameLogs.data?.data` — if shown while loading, it has stale options.
**How to avoid:** Disable or hide the team dropdown while `gameLogs.isLoading` is true.

### Pitfall 3: Stats Totals Row Using Wrong Division Value

**What goes wrong:** Average totals row divides by total matches count instead of season count.
**Why it happens:** Flutter's `getValueForGivenOptionWithSeasonsCount` uses `playersStats.length` as `matchesDivider` for `StatMatches` — this is the *number of seasons*, not total match count. Regular stats use `getValueForGivenOption` which divides by `stat.m` (actual matches played).
**How to avoid:** Pass `playersStats.size` as `seasonCount` to `getValueForGivenOptionWithSeasonsCount` for the totals row, matching Flutter's behavior exactly.

### Pitfall 4: `selectedTeam` Null State on Game Logs Load

**What goes wrong:** Game logs fetch completes but `selectedTeam` is still null — no logs are displayed.
**Why it happens:** `fetchGameLogsIfNeeded` sets `gameLogs.data` but doesn't initialize `selectedTeam`.
**How to avoid:** After successful fetch in `fetchGameLogs`, auto-select first team: `selectedTeam = logs.data.firstOrNull()`.

### Pitfall 5: Stats Table Fixed Columns Not Scrolling Vertically

**What goes wrong:** Season/League/Team fixed column stays at top while stat body scrolls down.
**Why it happens:** The fixed column `Column` is not sharing the `vScroll` state.
**How to avoid:** Same as `MatchDetailsTeamTable` — the pinned left column `Column` must use `.verticalScroll(vScroll)` with the same shared `vScroll` instance.

### Pitfall 6: `PlayerDetailsViewState` Not `@Immutable` After Adding New Fields

**What goes wrong:** Compose recomposition is suboptimal.
**Why it happens:** Existing `PlayerDetailsViewState` has `@Immutable` — must remain valid (all fields immutable).
**How to avoid:** All new fields must be immutable (`val`, not `var`). Sorted lists must be new list instances, not mutated in place.

---

## Code Examples

### Game Logs Left Column Cell (Flutter reference → KMP pattern)

```kotlin
// PlayerGameLogsTable.kt — left column cell
// Source: Flutter player_details_game_log_table.dart _fixedColumnItemWidget

@Composable
fun GameLogLeftColumnCell(
    playerLog: PlayerLog,
    height: Dp,
    onClick: () -> Unit
) {
    val winLossColor = when {
        playerLog.pts > playerLog.opponent.points -> BasketKrkColors.MatchWin
        playerLog.pts < playerLog.opponent.points -> BasketKrkColors.MatchLost
        else -> BasketKrkColors.MatchInProgress
    }
    val winLossText = when {
        playerLog.pts > playerLog.opponent.points -> "W"
        playerLog.pts < playerLog.opponent.points -> "L"
        else -> "IP"
    }

    Box(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
            .background(BasketKrkColors.DefaultBackground)
            .drawTopBottomBorder()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(playerLog.date, style = BasketKrkStyles.gameLogsDate)
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(winLossColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(winLossText, style = BasketKrkStyles.gameLogsSignResult)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("${playerLog.pts}-${playerLog.opponent.points}", style = BasketKrkStyles.gameLogsResult)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text("vs ${playerLog.opponent.shortName}", style = BasketKrkStyles.gameLogsVsTeam)
        }
    }
}
```

### Record Item (Flutter reference → KMP pattern)

```kotlin
// PlayerRecordsTab.kt — record list item
// Source: Flutter record_item_view.dart

@Composable
fun RecordItem(
    record: PlayerRecord,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BasketKrkColors.BorderRoundedItem, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Circle with value + stat sign
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(35.dp)
                        .border(3.dp, BasketKrkColors.Main, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${record.value}", style = BasketKrkStyles.recordValue)
                }
                Text(record.recordType.name, style = BasketKrkStyles.recordStatSign)
            }
            Spacer(modifier = Modifier.width(8.dp))
            // Description + secondary text
            Column(modifier = Modifier.weight(1f)) {
                Text(record.recordType.toDescription(), style = BasketKrkStyles.recordDescription)
                Text(record.toSecondaryText(), style = BasketKrkStyles.itemAdditionalInfo)
            }
            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = BasketKrkColors.Main)
        }
    }
}
```

### Avg/Total Toggle (Flutter `ToggleButtons` → KMP equivalent)

```kotlin
// PlayerStatsTab.kt — toggle above the stats table
// Source: Flutter stat_option_toggle_view.dart (ToggleButtons with 30h x 60w constraints)

@Composable
fun StatDisplayTypeToggle(
    statDisplayType: StatDisplayType,
    onToggle: (StatDisplayType) -> Unit
) {
    Row(
        modifier = Modifier.border(1.5.dp, BasketKrkColors.TextSecondary, RoundedCornerShape(5.dp))
    ) {
        StatDisplayType.entries.forEachIndexed { index, type ->
            val isSelected = statDisplayType == type
            Box(
                modifier = Modifier
                    .width(60.dp).height(30.dp)
                    .background(
                        if (isSelected) BasketKrkColors.Main.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .clickable { onToggle(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    type.name.uppercase(),
                    style = BasketKrkStyles.fixedColumnText.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp,
                        color = if (isSelected) BasketKrkColors.Main else BasketKrkColors.TextSecondary
                    )
                )
            }
        }
    }
}
```

### Sort Indicator on Column Header

```kotlin
// PlayerGameLogsTable.kt — clickable header cell with sort indicator
// Extend TopRowCell pattern; add arrow icon when this column is active sort

@Composable
fun SortableTopRowCell(
    text: String,
    width: Dp,
    height: Dp,
    roundedEnd: Boolean,
    isSortActive: Boolean,
    sortAscending: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width).height(height)
            .clip(if (roundedEnd) RoundedCornerShape(topEnd = 10.dp) else RoundedCornerShape(0.dp))
            .background(BasketKrkColors.Main)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, style = BasketKrkStyles.fixedRowText, maxLines = 1)
            if (isSortActive) {
                Icon(
                    imageVector = if (sortAscending) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
```

---

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Flutter BLoC with events | KMP ViewModel + StateFlow | This project's KMP migration | All state mutations go through `_viewState.update { ... }` |
| Flutter `PlayerLogList.data` list of `PlayerLogByTeam` | KMP `PlayerLogList(val data: List<PlayerLogByTeam>)` | Phase 1 | Direct 1:1 model mapping exists |
| Flutter `StatDisplayType.tot/avg` | KMP `StatDisplayType.SUM/AVG` | Phase 1 | Enum names differ; mapping is already in `Stat.getValueForGivenOption` |

**Missing in KMP (must be added this phase):**
- `Stat.getValueForGivenOptionWithSeasonsCount()` — needed for stats totals row
- `PlayerStat.toReadableStatOptionText()` — needed for stats table fixed column values
- `PlayerRecordType` description strings in string resources (or a `toDescription()` extension)
- `Screen.TeamDetails(teamId: Int)` stub in `Screen.kt` (navigation forward reference)

---

## Open Questions

1. **Record type description strings**
   - What we know: Flutter uses `AppLocalizations` strings like `pts_record_description`, `ftm_record_description`, etc. The KMP project uses `stringResource(Res.string.*)`.
   - What's unclear: Whether these string keys already exist in KMP string resources.
   - Recommendation: Check `presentation/src/commonMain/composeResources/values/strings.xml` during Wave 0. If missing, add them. Alternatively, use a `PlayerRecordType.toDescription()` extension that returns hardcoded English strings (matching Flutter's values file).

2. **`MatchTeam.pts` field vs `PlayerLog.pts`**
   - What we know: `PlayerLog` has `val pts: Int` (player's points) and `val opponent: MatchTeam` which has `val points: Int` (opponent's team points). The game result is `playerLog.pts vs playerLog.opponent.points`.
   - What's unclear: Whether `pts` on `PlayerLog` represents the player's personal points or the player's team's score. Flutter's `PlayerLog` has `points` (team score) and `opponent.pts` (opponent score).
   - Recommendation: Verify by checking `PlayerLogDtoMapper` — if `PlayerLog.pts` maps to `PlayerLogDto.pts` (team score), use it as the home score. Resolve before implementing the left column cell.

3. **Stats table fixed-column widths**
   - What we know: Flutter uses 40dp for StatTeam column, 35dp default. KMP uses `leftColWidth = 120.dp` for single player name.
   - What's unclear: Optimal combined width for Season + League + Team in the fixed region on mobile.
   - Recommendation: Claude's discretion — try `80.dp + 50.dp + 90.dp = 220.dp` as the combined fixed width. Adjust if content clips.

---

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | None detected — KMP project has no existing test files |
| Config file | none — see Wave 0 |
| Quick run command | `./gradlew :domain:test` |
| Full suite command | `./gradlew test` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| PLOG-04 | Sort game logs by stat column — descending by default, toggles ascending | unit | `./gradlew :domain:test --tests "*.PlayerDetailsViewModelTest.sortGameLogs*"` | Wave 0 |
| PSTA-02 | Toggle between SUM and AVG correctly changes displayed values | unit | `./gradlew :domain:test --tests "*.StatTest.getValueForGivenOption*"` | Wave 0 |
| PSTA-03 | Totals row uses getValueForGivenOptionWithSeasonsCount | unit | `./gradlew :domain:test --tests "*.StatTest.getValueForGivenOptionWithSeasonsCount*"` | Wave 0 |
| PREC-02 | Record click passes correct matchId | manual | tap record item → verify MatchDetails opens | N/A — UI only |
| PLOG-03 | Team filter changes displayed logs client-side | unit | `./gradlew :domain:test --tests "*.PlayerDetailsViewModelTest.teamFilter*"` | Wave 0 |

### Sampling Rate
- **Per task commit:** `./gradlew :domain:test`
- **Per wave merge:** `./gradlew test`
- **Phase gate:** Full suite green before `/gsd:verify-work`

### Wave 0 Gaps
- [ ] `domain/src/commonTest/kotlin/com/mzs/basket_krk/domain/model/StatTest.kt` — covers PSTA-02, PSTA-03 (test `getValueForGivenOption` AVG division and new `getValueForGivenOptionWithSeasonsCount`)
- [ ] `presentation/src/commonTest/kotlin/.../playerdetails/PlayerDetailsViewModelTest.kt` — covers PLOG-03, PLOG-04 (sort + team filter logic)
- [ ] Test framework setup: `./gradlew :domain:test` — verify it runs before implementation starts

---

## Sources

### Primary (HIGH confidence)
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/components/MatchDetailsTeamTable.kt` — full scroll architecture; shared `hScroll`/`vScroll` pattern
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt` — current ViewState shape; existing fetch pattern
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Stat.kt` — `getValueForGivenOption`; `toReadableStatOptionText`; confirmed `getValueForGivenOptionWithSeasonsCount` is absent
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerStat.kt` — confirmed `toReadableStatOptionText` extension absent
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/DropdownFormField.kt` — exact signature and behavior
- `/Users/marcinzielinski/Documents/Development/kmp/basket-krk-kmp/presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` — confirmed `Screen.TeamDetails` absent

### Secondary (MEDIUM confidence — Flutter reference, architecture is translated)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/player_details_bloc.dart` — sort logic, season change behavior, team filter behavior
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/view/player_details_game_log_table.dart` — left column content layout (date, W/L badge, score, vs opponent)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/view/player_details_stat_table.dart` — three-column fixed section, totals row, `getValueForGivenOptionWithSeasonsCount` call
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/player_details_screen.dart` — tab structure, filter placement above table, records as `ListView`
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/view/record_item_view.dart` — record item layout: circle value, stat sign, description, times+date, chevron icon
- `~/Documents/Development/flutter/basket_krk/lib/presentation/views/stat_option_toggle_view.dart` — toggle dimensions (30h x 60w per button), color behavior

---

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — all libraries already in project, verified by reading build files and imports
- Architecture patterns: HIGH — derived directly from existing KMP code and verified Flutter 1:1 reference
- Pitfalls: HIGH — derived from reading actual Flutter BLoC event logic and KMP ViewModel patterns
- Missing extensions: HIGH — confirmed absent by searching codebase with grep

**Research date:** 2026-03-16
**Valid until:** 2026-04-16 (stable codebase; no external dependencies changing)
