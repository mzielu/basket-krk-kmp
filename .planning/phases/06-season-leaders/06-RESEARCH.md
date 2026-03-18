# Phase 6: Season Leaders - Research

**Researched:** 2026-03-18
**Domain:** Kotlin Multiplatform / Compose Multiplatform — Season Leaders feature (data layer + presentation layer)
**Confidence:** HIGH

---

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- **Leader item layout**: Reuse the existing AllTimeLeaders `LeaderItem` composable (or extend it). Shows: position, team logo, player name, stat value. Additional info: made/attempts for shooting stats (FT%, FG%, 3FG%), games played for counting stats (PTS, AST, REB, STL, BLK). Tapping a leader entry navigates to PlayerDetails (SLDR-06).
- **Filter dropdowns layout**: Match Flutter: 3 dropdowns above the leaders list — Season dropdown (narrow), League dropdown, Category dropdown. All use `DropdownFormField` component (same as matches screen and other filters). Cascading behavior: changing season updates available leagues; changing league or category updates leaders list.
- **Entry point**: Clickable item on the existing Statistics tab (not a separate entry screen). Tapping "Season Leaders" item navigates to the full Season Leaders screen. This means adding a navigation item to the statistics section, alongside AllTimeLeaders and Standings.
- **Default selections**: Match Flutter — default to most recent season, first available league, PTS category.
- **1:1 migration from Flutter** — match same API contract and behavior.

### Claude's Discretion
- Exact LeaderItem extension for additional info display (inline text, subtitle, or separate column)
- How to add the "Season Leaders" item in the Statistics tab layout
- Loading/error state design
- Empty state when no leaders for selected filters

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

---

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| SLDR-01 | User can view season leaders as a ranked list showing position, team logo, player name, and stat value | `LeaderItem` composable already shows all 4 fields; reuse or extend for `LeagueLeader` type |
| SLDR-02 | User can filter season leaders by season using a dropdown selector | `DropdownFormField` + season list from `GetLeaguesInfo`; pattern identical to `StandingsViewModel.onSeasonSelected` |
| SLDR-03 | User can filter season leaders by league using a dropdown selector (leagues update based on selected season) | `GetLeaguesForSeason` use case already exists; cascading pattern identical to `StandingsViewModel` |
| SLDR-04 | User can filter season leaders by stat category (PTS, AST, REB, STL, BLK, FT%, FG%, 3FG%) | `LeagueStatLeaderOption` enum already exists with all 8 values; `DropdownFormField` handles rendering |
| SLDR-05 | User can see additional info per leader (made/attempts for shooting stats, games played for others) | `LeagueLeader` domain model already has `games`, `made`, `ats` fields; Flutter renders `(made/ats)` or `{games}M` |
| SLDR-06 | User can tap a leader entry to navigate to PlayerDetails | Navigation graph already wired; `onNavigateToPlayer(playerId: Int)` lambda pattern identical to other screens |
</phase_requirements>

---

## Summary

The Season Leaders feature is a **primarily assembly task** — nearly every building block already exists in the codebase. The domain model (`LeagueLeader`), the API service method (`NetworkLeagueService.getLeagueLeaders`), the repository interface method (`LeagueRepository.getLeagueLeaders`), the stat category enum (`LeagueStatLeaderOption`), the cascading season/league use cases (`GetLeaguesInfo`, `GetLeaguesForSeason`), and all reusable UI components (`DropdownFormField`, `LeaderItem`, `NavigationItem`) are already present and working.

What this phase adds is the **ViewModel, screen composable, navigation route, and DI wiring** to connect these existing pieces into a new end-to-end feature. The `GetLeagueLeaders` use case does not yet exist and must be created — it is a thin wrapper over `LeagueRepository.getLeagueLeaders`. The `SeasonLeadersScreen` composable follows the `StandingsScreen` layout pattern with 3 `DropdownFormField` instances, but substitutes `LazyColumn` of `SeasonLeaderItem` rows instead of standings data.

The entry point is already partially wired: `StatisticsScreen` already has `openLeagueLeaders: () -> Unit` parameter and a `NavigationItem` for "Season Leaders" (string `season_leaders` already in strings.xml), but `MainScreen` currently passes an empty lambda `{}`. The fix is to wire `openLeagueLeaders` through `MainScreen` → `App.kt` composable just like `openAllTimeLeaders` was wired.

**Primary recommendation:** Create `GetLeagueLeadersUseCase` + `SeasonLeadersViewModel` + `SeasonLeadersScreen` + wire navigation in `Screen.kt`, `App.kt`, `MainScreen`, and both DI modules. Use `StandingsViewModel` as the direct structural template.

---

## Standard Stack

### Core (all already in the project)
| Library | Purpose | Role in this phase |
|---------|---------|-------------------|
| Compose Multiplatform | UI | `SeasonLeadersScreen` composable |
| AndroidX ViewModel | State management | `SeasonLeadersViewModel : ViewModel()` |
| Kotlin Coroutines / StateFlow | Async + state | `viewModelScope.launch`, `MutableStateFlow` |
| Arrow-kt `Either` | Error handling | `Either<Failure, List<LeagueLeader>>` from use case |
| Koin | DI | Register use case + ViewModel in `PresentationModule` |
| Compose Navigation | Routing | `Screen.SeasonLeaders` data object, `composable<Screen.SeasonLeaders>` |

### No new dependencies required
This phase adds no new libraries. Everything needed is already declared in the project.

---

## Architecture Patterns

### Recommended File Structure
```
domain/src/commonMain/.../domain/
└── usecase/
    └── GetLeagueLeadersUseCase.kt          # NEW — thin use case wrapping repository

presentation/src/commonMain/.../presentation/
├── navigation/
│   └── Screen.kt                           # ADD SeasonLeaders data object
├── screens/main/
│   ├── MainScreen.kt                       # WIRE openLeagueLeaders param
│   └── statistics/
│       └── seasonleaders/
│           ├── SeasonLeadersScreen.kt      # NEW
│           ├── SeasonLeadersViewModel.kt   # NEW
│           └── components/
│               └── SeasonLeaderItem.kt     # NEW — extends LeaderItem for LeagueLeader type
├── App.kt                                  # ADD composable<Screen.SeasonLeaders>
└── di/
    └── PresentationModule.kt               # ADD use case + ViewModel registrations
```

### Pattern 1: ViewModel — Cascading Filter + Leader Fetch

The `SeasonLeadersViewModel` follows `StandingsViewModel` exactly, with one additional dimension: a `selectedStatOption: LeagueStatLeaderOption`. Leaders are re-fetched whenever `selectedLeague` or `selectedStatOption` changes. When `selectedSeason` changes, leagues are refreshed via `GetLeaguesForSeason`, then leaders are fetched for the new first league + current category.

```kotlin
// Mirrors StandingsViewModel — source: StandingsViewModel.kt (line 41-49)
// Key reactive chain: selectedLeague changes → auto-fetch leaders
init {
    fetchInitData()

    viewModelScope.launch {
        viewState
            .map { Pair(it.selectedLeague, it.selectedStatOption) }
            .distinctUntilChanged()
            .filterNotNull()  // guard: both must be non-null
            .collect { (league, category) ->
                if (league != null) fetchLeaders(leagueId = league.id, statOption = category)
            }
    }
}
```

### Pattern 2: Filter Change Handlers

```kotlin
// Reuse verbatim from StandingsViewModel
fun onSeasonSelected(newSeason: Season) {
    if (newSeason != _viewState.value.selectedSeason) {
        _viewState.update { it.copy(selectedSeason = newSeason, selectedLeague = null) }
        fetchLeaguesData(seasonId = newSeason.id)
        // leaders fetch is triggered reactively by selectedLeague change
    }
}

fun onLeagueSelected(newLeague: League) {
    _viewState.update { it.copy(selectedLeague = newLeague) }
    // leaders fetch triggered reactively
}

fun onStatOptionChanged(newOption: LeagueStatLeaderOption) {
    _viewState.update { it.copy(selectedStatOption = newOption) }
    // leaders fetch triggered reactively
}
```

### Pattern 3: Error Handling (from StandingsViewModel)

```kotlin
// Source: StandingsViewModel.kt lines 88-93
.onSuspendGeneralError { error ->
    Logger.e("Error when fetching leaders data", error)
    _viewState.update { it.copy(error = error, fullScreenLoading = false) }
}
```

### Pattern 4: Screen Layout — 3 Dropdowns Above List

```kotlin
// Pattern from StandingsScreen (2 dropdowns) extended to 3
Row(
    modifier = Modifier.fillMaxWidth().padding(8.dp),
    horizontalArrangement = Arrangement.SpaceAround
) {
    DropdownFormField(
        modifier = Modifier.weight(1f),  // narrow — season number only
        label = stringResource(Res.string.season_input_hint),
        options = viewState.seasons,
        selectedOption = viewState.selectedSeason,
        onOptionSelected = onSeasonSelected,
        readableValue = { it?.num.toString() }
    )
    Spacer(Modifier.width(8.dp))
    DropdownFormField(
        modifier = Modifier.weight(3f),  // wide — league name
        label = stringResource(Res.string.league_input_hint),
        options = viewState.leagues,
        selectedOption = viewState.selectedLeague,
        onOptionSelected = onLeagueSelected,
        readableValue = { it?.name.orEmpty() }
    )
    Spacer(Modifier.width(8.dp))
    DropdownFormField(
        modifier = Modifier.weight(1.5f),  // medium — category label
        label = stringResource(Res.string.category_input_hint),
        options = LeagueStatLeaderOption.entries,
        selectedOption = viewState.selectedStatOption,
        onOptionSelected = onStatOptionChanged,
        readableValue = { it?.label.orEmpty() }
    )
}
```

### Pattern 5: SeasonLeaderItem Composable

The existing `LeaderItem` composable is typed to `AllTimeLeader`. For season leaders, a new `SeasonLeaderItem` composable (or a renamed/parallel one) typed to `LeagueLeader` is needed. The layout is identical to the Flutter implementation.

```kotlin
// Based on: LeaderItem.kt + Flutter stats_screen.dart lines 242-292
// LeagueLeader has: position, team, player (SearchItem.Player), value (Double), games, made?, ats?
@Composable
fun SeasonLeaderItem(
    leader: LeagueLeader,
    onOpenPlayerDetails: (SearchItem.Player) -> Unit,
    modifier: Modifier = Modifier
) {
    // Same Box/Row/border structure as LeaderItem
    // Position: 40.dp fixed width
    // Team logo: 30.dp width, BasketKrkImage
    // Player name: weight(1f)
    // Value: center, show as formatted Double (e.g. "18.5")
    // Additional info (rightmost, 60.dp wide):
    //   if (made != null && ats != null) -> "(made/ats)" style: competitionItemRowLight
    //   else -> "${games}M"               style: competitionItemRowLight
}
```

The `value` field is `Double` in `LeagueLeader` (vs `Int` in `AllTimeLeader`), so formatting matters. Use `String.format("%.1f", value)` or the Kotlin equivalent for one decimal place, matching the Flutter `"${leader.value}"` output.

### Pattern 6: Use Case

```kotlin
// Follows GetAllTimeLeadersUseCase pattern exactly
interface GetLeagueLeaders :
    SuspendInOutUseCase<GetLeagueLeadersUseCase.Input, Either<Failure, List<LeagueLeader>>>

class GetLeagueLeadersUseCase(private val repository: LeagueRepository) : GetLeagueLeaders {
    override suspend fun invoke(input: Input): Either<Failure, List<LeagueLeader>> {
        return repository.getLeagueLeaders(
            leagueId = input.leagueId,
            statOption = input.statOption
        )
    }

    data class Input(val leagueId: Int, val statOption: LeagueStatLeaderOption)
}
```

### Pattern 7: Navigation Wiring

```kotlin
// In Screen.kt — add alongside existing routes
@Serializable
data object SeasonLeaders : Screen()

// In App.kt — add composable block
composable<Screen.SeasonLeaders> {
    SeasonLeadersScreen(
        viewModel = koinViewModel<SeasonLeadersViewModel>(),
        onNavigateBack = { navController.popBackStack() },
        onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) },
    )
}

// In App.kt — update MainScreen call
MainScreen(
    ...
    openLeagueLeaders = { navController.navigate(Screen.SeasonLeaders) },
    ...
)

// In MainScreen.kt — add parameter and forward to StatisticsScreen
// StatisticsScreen already has openLeagueLeaders param and NavigationItem wired;
// only MainScreen.openLeagueLeaders -> StatisticsScreen needs forwarding
```

### Pattern 8: DI Registration

```kotlin
// In PresentationModule.kt — add to use cases block
single<GetLeagueLeaders> { GetLeagueLeadersUseCase(get()) }

// Add to view models block
viewModelOf(::SeasonLeadersViewModel)
```

### Pattern 9: ViewState

```kotlin
@Immutable
data class SeasonLeadersViewState(
    val fullScreenLoading: Boolean = false,
    val seasons: List<Season> = emptyList(),
    val leagues: List<League> = emptyList(),
    val leaders: List<LeagueLeader> = emptyList(),
    val selectedSeason: Season? = null,
    val selectedLeague: League? = null,
    val selectedStatOption: LeagueStatLeaderOption = LeagueStatLeaderOption.PTS,
    val error: Failure? = null
)
```

### Anti-Patterns to Avoid

- **Don't use paging for season leaders.** Season leaders are returned as a flat list (`List<LeagueLeader>`) — not paginated. The `AllTimeLeadersViewModel` uses `Pager`/`PagingData` because all-time leaders have pagination. Season leaders have no pagination; use a plain `LazyColumn` over a `List<LeagueLeader>`.
- **Don't fetch leaders in `init` directly.** Let the reactive `collect` on `selectedLeague`/`selectedStatOption` changes drive the fetch — this avoids double-fetch bugs and keeps the "season change → league refresh → leader fetch" chain clean.
- **Don't forget to reset `selectedLeague = null` on season change.** This is the `StandingsViewModel.onSeasonSelected` pattern (line 67-70). It clears the league dropdown while leagues are loading, preventing a stale league from triggering a leader fetch.
- **Don't apply `filterNotNull()` to the pair collector** — instead guard with `if (league != null)` inside collect, because `selectedStatOption` is always non-null (enum with default).

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead |
|---------|-------------|-------------|
| Season/league cascade | Custom event bus | Reactive `StateFlow.map().distinctUntilChanged().collect` (already in `StandingsViewModel`) |
| API error handling | Try/catch | `Either.catchWithError` + `onSuspendGeneralError` (established project pattern) |
| Filter dropdowns | Custom dropdown | `DropdownFormField` already handles selection highlight, expand/collapse, readableValue |
| Leader list item | Custom layout | `SeasonLeaderItem` following `LeaderItem` structure — both share same `BasketKrkStyles.competitionItemRow` |
| Navigation | Custom back stack | `navController.navigate(Screen.SeasonLeaders)` + `navController.popBackStack()` |
| String resources | Hardcoded strings | All needed strings already exist: `season_leaders`, `season_input_hint`, `league_input_hint`, `category_input_hint`, `stats_empty_message` |

---

## Common Pitfalls

### Pitfall 1: Double-Fetch on Init
**What goes wrong:** Calling `fetchLeaders(...)` in both `fetchInitData()`'s success block AND the reactive `selectedLeague` collector causes two API calls on initial load.
**Why it happens:** `fetchInitData` sets `selectedLeague`, which triggers the collector.
**How to avoid:** Only fetch leaders in the reactive collector. `fetchInitData` only sets `selectedSeason`, `seasons`, `leagues`, and `selectedLeague`. The collector handles the leader fetch automatically.
**Warning signs:** Two network requests to `/league/{id}/avg_stats?cat=pts` on screen open.

### Pitfall 2: Stale League After Season Change
**What goes wrong:** User changes season; the old league is still selected; a fetch fires for the old league ID under the new season context.
**Why it happens:** `selectedLeague` not nulled on season change.
**How to avoid:** In `onSeasonSelected`, set `selectedLeague = null` before triggering `fetchLeaguesData`. The reactive collector guards with `if (league != null)`, so no fetch fires until the new league list arrives and `selectedLeague` is set.

### Pitfall 3: Value Formatting for Double
**What goes wrong:** `LeagueLeader.value` is `Double` (e.g. `18.5`), displayed as-is. Without formatting, Kotlin `Double.toString()` may produce `"18.5"` or `"18.500000000001"`.
**How to avoid:** Use `"%.1f".format(leader.value)` in `SeasonLeaderItem` to match Flutter's default display.

### Pitfall 4: openLeagueLeaders Lambda Not Wired
**What goes wrong:** `StatisticsScreen` has the `NavigationItem` for Season Leaders but `MainScreen` passes `openLeagueLeaders = {}` (empty lambda). The navigation item is visible but tapping does nothing.
**Why it happens:** Phase 5 wired AllTimeLeaders but left SeasonLeaders as a stub.
**How to avoid:** Add `openLeagueLeaders: () -> Unit` parameter to `MainScreen`, thread it through to `StatisticsScreen`, and wire it in `App.kt` to `navController.navigate(Screen.SeasonLeaders)`.

### Pitfall 5: Missing `@Immutable` on ViewState
**What goes wrong:** Compose recomposition may be excessive without `@Immutable`.
**How to avoid:** Annotate `SeasonLeadersViewState` with `@Immutable` (same as `StandingsViewState`).

---

## Code Examples

### Existing API Call (verified — NetworkLeagueService.kt line 33-39)
```kotlin
// API endpoint: GET /league/{leagueId}/avg_stats?cat={category.name.lowercase()}
// category values: pts, ast, reb, stl, blk, ft, fg, fg3
override suspend fun getLeagueLeaders(
    leagueId: Int,
    statOption: LeagueStatLeaderOption
): Either<Failure, List<LeagueLeader>> {
    return Either.catchWithError {
        apiService.get<LeagueLeadersResponseDto>("/league/$leagueId/avg_stats?cat=${statOption.name.lowercase()}")
            .toDomain()
    }
}
```

### Existing Domain Model (verified — LeagueLeader.kt)
```kotlin
data class LeagueLeader(
    val player: SearchItem.Player,
    val team: SearchItem.Team,
    val value: Double,
    val position: Int,
    val games: Int,
    val made: Int? = null,
    val ats: Int? = null
)
```

### Existing DTO + Mapper (verified — LeagueLeaderDto.kt)
```kotlin
@Serializable
data class LeagueLeaderDto(
    val player: PlayerDto,
    val team: TeamDto,
    val value: Double,
    val position: Int,
    val games: Int,
    val made: Int? = null,
    val ats: Int? = null
)

fun LeagueLeaderDto.toDomain() = LeagueLeader(
    player = player.toDomain(),
    team = team.toDomain(),
    value = value,
    position = position,
    games = games,
    made = made,
    ats = ats
)
```

### Stat Category Enum (verified — LeagueStatLeaderOption.kt)
```kotlin
enum class LeagueStatLeaderOption(val label: String) {
    PTS("PTS"), AST("AST"), REB("REB"), STL("STL"), BLK("BLK"),
    FT("FT%"), FG("FG%"), FG3("3FG%")
}
// API uses .name.lowercase(): "pts", "ast", "reb", "stl", "blk", "ft", "fg", "fg3"
```

### Shooting vs Counting Detection
```kotlin
// Flutter reference: stats_screen.dart lines 273-286
// Shooting categories: FT, FG, FG3 → have made + ats populated by backend
// Counting categories: PTS, AST, REB, STL, BLK → have games, made == null, ats == null
// Decision is data-driven (null checks on made/ats), not category-driven
val isShootingStat = leader.made != null && leader.ats != null
if (isShootingStat) {
    Text("(${leader.made}/${leader.ats})", style = BasketKrkStyles.competitionItemRowLight)
} else {
    Text("${leader.games}M", style = BasketKrkStyles.competitionItemRowLight)
}
```

---

## What Already Exists (No New Work Needed)

| Component | Location | Status |
|-----------|----------|--------|
| `LeagueLeader` domain model | `domain/.../model/LeagueLeader.kt` | EXISTS |
| `LeagueStatLeaderOption` enum | `domain/.../model/LeagueStatLeaderOption.kt` | EXISTS |
| `LeagueRepository.getLeagueLeaders` | `domain/.../repository/LeagueRepository.kt` | EXISTS |
| `LeagueRepositoryImpl.getLeagueLeaders` | `data/.../repository/LeagueRepositoryImpl.kt` | EXISTS |
| `NetworkLeagueService.getLeagueLeaders` | `data/.../service/NetworkLeagueService.kt` | EXISTS |
| `LeagueLeaderDto` + `LeagueLeadersResponseDto` + mapper | `data/.../dto/` | EXISTS |
| `GetLeaguesInfo` use case | `domain/.../usecase/GetLeaguesInfoUseCase.kt` | EXISTS |
| `GetLeaguesForSeason` use case | `domain/.../usecase/GetLeaguesForSeasonUseCase.kt` | EXISTS |
| `DropdownFormField` composable | `presentation/.../base/ui/DropdownFormField.kt` | EXISTS |
| `LeaderItem` composable (AllTimeLeader-typed) | `presentation/.../alltimeleaders/components/LeaderItem.kt` | EXISTS (needs parallel `SeasonLeaderItem`) |
| `NavigationItem` composable | `presentation/.../base/ui/NavigationItem.kt` | EXISTS |
| `StatisticsScreen` with `openLeagueLeaders` param + `NavigationItem` | `presentation/.../statistics/StatisticsScreen.kt` | EXISTS (wired as empty lambda in MainScreen) |
| `season_leaders` string resource | `presentation/.../composeResources/values/strings.xml` line 94 | EXISTS |
| `stats_empty_message` string resource | strings.xml line 117 | EXISTS |
| `Season`, `League` models | `domain/.../model/` | EXISTS |
| `ViewStateData<T>` | `presentation/.../base/ViewStateData.kt` | EXISTS (not needed here — use direct fields in ViewState like StandingsViewState) |

## What Must Be Created

| Component | Location | Notes |
|-----------|----------|-------|
| `GetLeagueLeadersUseCase` | `domain/.../usecase/` | Thin wrapper — mirrors `GetAllTimeLeadersUseCase` structure |
| `SeasonLeadersViewModel` + `SeasonLeadersViewState` | `presentation/.../statistics/seasonleaders/` | Follow `StandingsViewModel` pattern; add `selectedStatOption` dimension |
| `SeasonLeadersScreen` + `SeasonLeadersContent` | same package | Follow `StandingsScreen` pattern; 3 dropdowns + LazyColumn |
| `SeasonLeaderItem` composable | `presentation/.../statistics/seasonleaders/components/` | Follow `LeaderItem` but typed to `LeagueLeader`; show games or made/ats |
| `Screen.SeasonLeaders` route | `presentation/.../navigation/Screen.kt` | Data object, `@Serializable` |
| `composable<Screen.SeasonLeaders>` entry | `presentation/.../App.kt` | Wire `onNavigateBack` + `onNavigateToPlayer` |
| `openLeagueLeaders` wiring in `MainScreen` | `presentation/.../screens/main/MainScreen.kt` | Add param, forward to `StatisticsScreen` |
| `GetLeagueLeaders` registration | `presentation/.../di/PresentationModule.kt` | `single<GetLeagueLeaders> { GetLeagueLeadersUseCase(get()) }` |
| `SeasonLeadersViewModel` registration | `PresentationModule.kt` | `viewModelOf(::SeasonLeadersViewModel)` |

---

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | None detected (no test directories, no test config files in project) |
| Config file | None — Wave 0 gap |
| Quick run command | N/A |
| Full suite command | N/A |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| SLDR-01 | Leader list shows position, team logo, player name, stat value | manual-only | N/A — no test infrastructure | Wave 0 gap |
| SLDR-02 | Season dropdown updates available leagues | manual-only | N/A | Wave 0 gap |
| SLDR-03 | League dropdown updates leaders list | manual-only | N/A | Wave 0 gap |
| SLDR-04 | 8 stat categories available and correct | manual-only | N/A | Wave 0 gap |
| SLDR-05 | Shooting stats show made/ats; counting stats show games | manual-only | N/A | Wave 0 gap |
| SLDR-06 | Tap leader entry navigates to PlayerDetails | manual-only | N/A | Wave 0 gap |

Note: All requirements are manual-only because no automated test infrastructure exists in the project. This is consistent with all prior phases (v1.0 was delivered without automated tests).

### Wave 0 Gaps
None mandated — existing pattern for this project is no automated tests. All verification is done via device/emulator smoke testing per the `/gsd:verify-work` workflow.

---

## Open Questions

1. **Value display precision for Double**
   - What we know: `LeagueLeader.value` is `Double`; Flutter renders `"${leader.value}"` which shows one decimal for averages (e.g. `18.5`)
   - What's unclear: Whether the backend always returns exactly one decimal or may return integers (e.g. `18.0`)
   - Recommendation: Use `"%.1f".format(leader.value)` for consistent display; revisit if backend format is confirmed

2. **`fetchLeaders` trigger on both league AND category change**
   - What we know: `StandingsViewModel` only reacts to `selectedLeague`; this ViewModel needs to react to both `selectedLeague` and `selectedStatOption`
   - What's unclear: Whether a `Pair` comparison in `distinctUntilChanged` is sufficient or whether two separate collectors are cleaner
   - Recommendation: Collect `map { Pair(it.selectedLeague, it.selectedStatOption) }.distinctUntilChanged()` — this is idiomatic and avoids race conditions from two collectors

---

## Sources

### Primary (HIGH confidence)
- Direct code inspection — `NetworkLeagueService.kt`, `LeagueRepositoryImpl.kt`, `LeagueRepository.kt`, `LeagueLeader.kt`, `LeagueStatLeaderOption.kt` — confirmed `getLeagueLeaders` fully implemented end-to-end
- Direct code inspection — `StandingsViewModel.kt`, `StandingsScreen.kt` — cascading filter pattern confirmed and extracted as template
- Direct code inspection — `AllTimeLeadersScreen.kt`, `LeaderItem.kt` — leader item layout confirmed
- Direct code inspection — `StatisticsScreen.kt` — entry point already has `openLeagueLeaders` param and `NavigationItem`; wired as empty lambda in `MainScreen.kt`
- Direct code inspection — `DropdownFormField.kt` — generic composable confirmed, works for any type T
- Direct code inspection — `Screen.kt`, `App.kt` — navigation registration pattern confirmed
- Direct code inspection — `PresentationModule.kt`, `DataModule.kt` — Koin DI registration pattern confirmed
- Flutter source — `stats_screen.dart`, `stats_bloc.dart`, `stat_leader_option.dart`, `league_leader.dart`, `league_remote_datasource.dart` — 1:1 migration reference confirmed

### Secondary (MEDIUM confidence)
- N/A — all findings are from direct codebase inspection

---

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — all libraries confirmed present in project
- Architecture: HIGH — pattern is 1:1 copy of existing `StandingsViewModel`/`StandingsScreen` with an extra filter dimension; all building blocks verified in source
- Pitfalls: HIGH — identified from direct code reading and Flutter BLoC event sequencing

**Research date:** 2026-03-18
**Valid until:** 2026-04-18 (stable project, no fast-moving dependencies)
