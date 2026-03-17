# Phase 5: Navigation Integration - Research

**Researched:** 2026-03-17
**Domain:** Compose Multiplatform navigation wiring (KMP/Kotlin)
**Confidence:** HIGH

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- Match Flutter: player names in the stat table (MatchDetails) are clickable → opens PlayerDetails
- Team names in the stat table (MatchDetails) may also be clickable → opens TeamDetails
- The `MatchDetailsTeamTable` component already has an `onPlayerClick` callback — wire it to navigate
- Match Flutter: team name/entry in standings table is clickable → opens TeamDetails
- Match Flutter: tapping a player entry in the all-time leaders list opens PlayerDetails
- The `LeaderItem` component may already have a click handler — wire it to navigate
- Match Flutter: tap player search result → PlayerDetails, tap team search result → TeamDetails
- `SearchItem` sealed class already distinguishes `Player` and `Team` — use type to determine destination
- Cross-navigation (NAV-04): already implemented — PlayerDetails stats tab → TeamDetails (Phase 2), TeamDetails roster → PlayerDetails (Phase 4)
- Verify these still work correctly after Phase 5 changes

### Claude's Discretion
- Whether to add visual click affordance (ripple, underline) to newly-clickable elements
- How to handle any existing click handlers that may conflict

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| NAV-01 | User can navigate to PlayerDetails from MatchDetails (clicking player name in stat table) | `MatchDetailsScreen` needs `onNavigateToPlayer` and `onNavigateToTeam` parameters; TODO placeholders exist at call site in `MatchDetailsContent` |
| NAV-02 | User can navigate to TeamDetails from Standings (clicking team name) | `StandingsScreen` needs `onNavigateToTeam` parameter; TODO placeholder exists in `StandingsContent`; `CompetitionItem` + `StandingItem` already handle click structurally |
| NAV-03 | User can navigate to PlayerDetails from AllTimeLeaders (clicking player entry) | Already fully wired — `AllTimeLeadersScreen` accepts `openPlayerDetails`, App.kt wires it, `LeaderItem` fires it |
| NAV-04 | User can navigate from PlayerDetails to TeamDetails and vice versa (cross-navigation) | Already fully implemented — `PlayerDetailsScreen` has `onNavigateToTeam`, `TeamDetailsScreen` has `onNavigateToPlayer`, both wired in App.kt |
| NAV-05 | User can navigate to PlayerDetails or TeamDetails from search results | Already fully wired — `SearchScreen` accepts `openTeamDetails`/`openPlayerDetails`, MainScreen threads them from App.kt, `SearchListItem` fires them |
</phase_requirements>

## Summary

Phase 5 is a wiring-only phase. No new screens, ViewModels, data layers, or components are needed. The navigation infrastructure (`Screen.kt`, `App.kt`, `navController.navigate()`) is fully in place. The destination routes `Screen.PlayerDetails` and `Screen.TeamDetails` already exist.

Code inspection reveals that NAV-03, NAV-04, and NAV-05 are already implemented. Only NAV-01 (MatchDetails) and NAV-02 (Standings) require actual code changes. Both follow an identical pattern: add a navigation callback parameter to the Screen composable, thread it from App.kt through the composable call hierarchy, and replace the existing TODO comment with a real call.

**Primary recommendation:** This phase is two targeted wiring changes — add `onNavigateToPlayer`/`onNavigateToTeam` to `MatchDetailsScreen`, and add `onNavigateToTeam` to `StandingsScreen`. Then verify the three already-implemented requirements still work.

---

## Actual State of Each Requirement

### NAV-01: MatchDetails → PlayerDetails / TeamDetails

**Status: NOT yet wired (2 TODOs in live code)**

`MatchDetailsScreen.kt` currently:
- Signature: `MatchDetailsScreen(viewModel, onNavigateBack)` — no navigation callbacks
- `MatchDetailsContent` already has `onOpenTeamDetails: (Int) -> Unit` and `onOpenPlayerDetails: (Int) -> Unit` parameters threaded to `ViewWithTable` and `ViewWithoutTable`
- In `MatchDetailsContent`, both lambdas are currently hardcoded as `{}` with TODO comments:
  ```kotlin
  onOpenTeamDetails = {
      // TODO Handle team click
  },
  onOpenPlayerDetails = {
      // TODO Handle player click
  },
  ```
- `MatchDetailsTeamTable` already calls `onPlayerPress(pws.player.id)` on player cell click
- `TeamPart` (the team logo/name block in `TopView`) already calls `onOpenTeamDetails(matchTeam.id)` on click
- `App.kt` calls `MatchDetailsScreen(viewModel, onNavigateBack)` with no navigation lambdas

**Changes needed:**
1. Add `onNavigateToPlayer: (Int) -> Unit` and `onNavigateToTeam: (Int) -> Unit` to `MatchDetailsScreen` signature
2. Pass them through to `MatchDetailsContent`
3. In `MatchDetailsContent`, replace TODO lambdas with the real callbacks
4. In `App.kt`, pass `onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) }` and `onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) }` to `MatchDetailsScreen`

### NAV-02: Standings → TeamDetails

**Status: NOT yet wired (1 TODO in live code)**

`StandingsScreen.kt` currently:
- Signature: `StandingsScreen(viewModel, onNavigateBack)` — no navigation callback
- `StandingsContent` calls `CompetitionItem(competition, onOpenTeamDetails = { /* TODO */ })`
- `CompetitionItem` already accepts `onOpenTeamDetails: (teamId: Int) -> Unit` and passes `standing.team.id` to it via `StandingItem`'s `onClick`
- `StandingItem` already has `.clickable(onClick = onClick)` — the visual click affordance is already present
- `App.kt` calls `StandingsScreen(viewModel, onNavigateBack)` with no navigation lambda

**Changes needed:**
1. Add `onNavigateToTeam: (Int) -> Unit` to `StandingsScreen` signature
2. Thread it through `StandingsContent` signature
3. In `StandingsContent`, replace TODO with `onOpenTeamDetails = onNavigateToTeam`
4. In `App.kt`, pass `onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) }` to `StandingsScreen`

### NAV-03: AllTimeLeaders → PlayerDetails

**Status: ALREADY WIRED — no changes needed**

Full wiring exists:
- `AllTimeLeadersScreen(openPlayerDetails: (Int) -> Unit, onNavigateBack: () -> Unit)`
- In screen: `onPlayerClick = { openPlayerDetails.invoke(it.id) }` where `it` is `SearchItem.Player`
- `LeaderItem` calls `onOpenPlayerDetails(leader.player)` where `leader.player` is `SearchItem.Player`
- `AllTimeLeadersContent` passes `onPlayerClick` to `LeaderItem`
- `App.kt` already has: `openPlayerDetails = { navController.navigate(Screen.PlayerDetails(playerId = it)) }`

### NAV-04: Cross-navigation PlayerDetails ↔ TeamDetails

**Status: ALREADY WIRED — no changes needed**

Full wiring in `App.kt`:
- `PlayerDetailsScreen(viewModel, onNavigateBack, onNavigateToMatch, onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) })`
- `TeamDetailsScreen(viewModel, onNavigateBack, onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) }, onNavigateToMatch)`

### NAV-05: Search → PlayerDetails / TeamDetails

**Status: ALREADY WIRED — no changes needed**

Full wiring chain:
- `SearchListItem` calls `onPlayerClick(searchItem.id)` or `onTeamClick(searchItem.id)` based on `SearchItem` type
- `SearchContent`/`SearchScreen` accept `openTeamDetails: (Int) -> Unit` and `openPlayerDetails: (Int) -> Unit`
- `MainScreen` receives `openTeamDetails` and `openPlayerDetails` from `App.kt` and passes to `SearchScreen`
- `App.kt` wires both to `navController.navigate(Screen.PlayerDetails(...)` and `Screen.TeamDetails(...)`

---

## Standard Stack

### Core (already established in project)
| Component | Pattern | Purpose |
|-----------|---------|---------|
| `androidx.navigation.compose` | `NavHost` + `composable<T>` | Type-safe navigation host |
| `Screen` sealed class | `@Serializable` data classes | Typed route definitions |
| `navController.navigate(Screen.X(...))` | Lambda callbacks | Navigate from App.kt to destination |
| Callback threading | Lambda parameters on composables | Pass navigation from App.kt → Screen → subcomponent |

### Navigation Callback Threading Pattern (established)
```kotlin
// App.kt: source of truth for navController
composable<Screen.SomeScreen> {
    SomeScreen(
        viewModel = ...,
        onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) },
        onNavigateToTeam   = { navController.navigate(Screen.TeamDetails(teamId = it)) },
    )
}

// SomeScreen.kt: forward callbacks to content
@Composable
fun SomeScreen(
    viewModel: SomeViewModel,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    SomeContent(
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToTeam   = onNavigateToTeam,
        ...
    )
}

// SomeContent.kt: pass to components
@Composable
fun SomeContent(
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
    ...
) {
    SubComponent(
        onItemClick = { id -> onNavigateToPlayer(id) }
    )
}
```

---

## Architecture Patterns

### Callback Propagation Chain
```
App.kt (navController owner)
  └─ composable<Screen.X> { ... }
       └─ XScreen(onNavigateToPlayer, onNavigateToTeam)
            └─ XContent(onNavigateToPlayer, onNavigateToTeam)
                 └─ SubComponent(onItemClick)  ← fires the callback
```

All navigation callbacks originate in `App.kt` and propagate downward through composable parameters. ViewModels do NOT trigger navigation directly — they expose state. Navigation is always initiated by UI events.

### MatchDetails-Specific Pattern

`MatchDetailsScreen` feeds into two composables depending on data:
- `ViewWithoutTable` — used when `matchDetails.statsEmpty` is true; uses `onOpenTeamDetails` for team header taps
- `ViewWithTable` — used when stats are present; uses `onOpenTeamDetails` AND `onOpenPlayerDetails`

Both composables already accept these parameters. The fix is purely in `MatchDetailsScreen`/`MatchDetailsContent` — expose the callbacks at the screen function boundary and replace the TODO lambdas.

### Don't Hand-Roll

| Problem | Don't Build | Use Instead |
|---------|-------------|-------------|
| Custom back-stack management | Manual screen stack | `navController.popBackStack()` |
| Click routing by item type | Custom dispatch code | Kotlin `when (searchItem)` with sealed class — already done in `SearchListItem` |
| Route parameter passing | String interpolation | `@Serializable` data class routes — already in `Screen.kt` |

---

## Common Pitfalls

### Pitfall 1: Forgetting Preview Default Parameters
**What goes wrong:** `MatchDetailsContent` and `StandingsContent` have `@Preview` functions that call them with no navigation callbacks. Adding required lambda parameters breaks previews.
**How to avoid:** Previews pass `onNavigateToPlayer = {}` and `onNavigateToTeam = {}` as defaults. Check the existing preview functions and add the empty lambdas there.
**Example in codebase:** `MatchDetailsContentPreview` calls `MatchDetailsContent(viewState, onRetry, onNavigateBack, onSortByStat)` — it must also include the new callbacks.

### Pitfall 2: Threading Through Content Layer
**What goes wrong:** Adding the parameter to `MatchDetailsScreen` but forgetting to add it to `MatchDetailsContent` (the inner `@Composable` function that does the actual rendering and holds the TODO).
**How to avoid:** Both the Screen function and its Content function must have matching signatures. The Screen function calls Content.

### Pitfall 3: MatchDetails Team-Click in ViewWithoutTable
**What goes wrong:** Only wiring player click in `ViewWithTable` and forgetting that `ViewWithoutTable` also has `onOpenTeamDetails = {}` (already hardcoded empty). The team header is clickable in both views.
**How to avoid:** Both `ViewWithTable` and `ViewWithoutTable` call `TopView(matchDetails, onOpenTeamDetails)` — wire the callback in both.

### Pitfall 4: NAV-03/NAV-05 Regression
**What goes wrong:** Assuming NAV-03 and NAV-05 need code changes and accidentally breaking them.
**How to avoid:** These are already working. Verify, don't modify.

---

## Code Examples

### Wiring MatchDetailsScreen (NAV-01)
```kotlin
// Source: existing App.kt pattern
// In App.kt — update MatchDetails composable block:
composable<Screen.MatchDetails> { backStackEntry ->
    val args = backStackEntry.toRoute<Screen.MatchDetails>()
    val viewModel: MatchDetailsViewModel = koinInject(
        parameters = { parametersOf(args.matchId) }
    )
    MatchDetailsScreen(
        viewModel = viewModel,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) },
        onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) },
    )
}

// In MatchDetailsScreen.kt — extend signature:
@Composable
fun MatchDetailsScreen(
    viewModel: MatchDetailsViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    MatchDetailsContent(
        viewState = viewState,
        onRetry = viewModel::retry,
        onSortByStat = viewModel::onSortByStat,
        onNavigateBack = onNavigateBack,
        onNavigateToPlayer = onNavigateToPlayer,
        onNavigateToTeam = onNavigateToTeam,
    )
}

// In MatchDetailsContent — extend signature and replace TODOs:
@Composable
fun MatchDetailsContent(
    viewState: MatchDetailsViewState,
    onRetry: () -> Unit,
    onSortByStat: (StatOption) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (Int) -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    // ...
    viewState.matchDetails.data?.let { matchDetails ->
        if (matchDetails.statsEmpty) {
            ViewWithoutTable(
                matchDetails = matchDetails,
                onOpenTeamDetails = onNavigateToTeam,  // was {}
                middleText = matchDetails.resolveMiddleText()
            )
        } else {
            ViewWithTable(
                matchDetails = matchDetails,
                onOpenTeamDetails = onNavigateToTeam,   // was TODO
                onOpenPlayerDetails = onNavigateToPlayer, // was TODO
                onStatClicked = onSortByStat
            )
        }
    }
}

// In MatchDetailsContentPreview — add empty lambdas:
MatchDetailsContent(
    viewState = ...,
    onRetry = {},
    onNavigateBack = {},
    onSortByStat = {},
    onNavigateToPlayer = {},
    onNavigateToTeam = {},
)
```

### Wiring StandingsScreen (NAV-02)
```kotlin
// Source: existing App.kt pattern
// In App.kt — update Standings composable block:
composable<Screen.Standings> {
    StandingsScreen(
        viewModel = koinViewModel<StandingsViewModel>(),
        onNavigateBack = { navController.popBackStack() },
        onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) },
    )
}

// In StandingsScreen.kt — extend signature:
@Composable
fun StandingsScreen(
    viewModel: StandingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    StandingsContent(
        viewState = viewState,
        onLeagueSelected = viewModel::onLeagueSelected,
        onSeasonSelected = viewModel::onSeasonSelected,
        onRefresh = viewModel::onRefresh,
        onNavigateBack = onNavigateBack,
        onNavigateToTeam = onNavigateToTeam,
    )
}

// In StandingsContent — extend signature and replace TODO:
@Composable
fun StandingsContent(
    viewState: StandingsViewState,
    onLeagueSelected: (League) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onRefresh: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToTeam: (Int) -> Unit,
) {
    // ...
    CompetitionItem(
        competition = competition,
        onOpenTeamDetails = onNavigateToTeam,  // was TODO
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

// In StandingsContentPreview — add empty lambda:
StandingsContent(
    ...,
    onNavigateToTeam = {}
)
```

---

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | None detected — no test files, no test framework config |
| Config file | None |
| Quick run command | Manual UI testing only |
| Full suite command | Manual UI testing only |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| NAV-01 | Tap player in MatchDetails stat table → PlayerDetails opens | manual-only | N/A — no KMP UI test framework configured | ❌ Wave 0 N/A |
| NAV-02 | Tap team in Standings → TeamDetails opens | manual-only | N/A | ❌ Wave 0 N/A |
| NAV-03 | Tap player in AllTimeLeaders → PlayerDetails opens | manual-only | N/A — already wired, verify only | ❌ Wave 0 N/A |
| NAV-04 | Cross-nav PlayerDetails↔TeamDetails | manual-only | N/A — already wired, verify only | ❌ Wave 0 N/A |
| NAV-05 | Tap search result → correct detail screen opens | manual-only | N/A — already wired, verify only | ❌ Wave 0 N/A |

All navigation requirements are UI integration behaviors. No automated test framework is present in the project. Validation is manual (run app on device/simulator, tap each entry point).

### Sampling Rate
- **Per task commit:** Build compiles without errors (Gradle build)
- **Per wave merge:** Run app on device/simulator, test each navigation entry point manually
- **Phase gate:** All 5 navigation entry points work end-to-end before `/gsd:verify-work`

### Wave 0 Gaps
None — no test infrastructure expected for this project. Manual validation is the established verification approach throughout all prior phases.

---

## Open Questions

1. **MatchDetails team-click in `ViewWithoutTable`**
   - What we know: `ViewWithoutTable` calls `TopView(matchDetails, onOpenTeamDetails)` and the `TeamPart` composable inside `TopView` has `.clickable { onOpenTeamDetails(matchTeam.id) }`. The current hardcoded `{}` means team name is visually clickable but does nothing.
   - What's unclear: Does the Flutter app navigate to TeamDetails from the no-stats view (e.g., walkover/postponed games)? CONTEXT.md says "team names in the stat table may also be clickable" — this is the team header, not the stat table.
   - Recommendation: Wire `onNavigateToTeam` in `ViewWithoutTable` as well, since `TeamPart` already has the click handler. This matches Flutter behavior and is zero additional code cost. Claude discretion applies.

2. **Visual affordance for newly-wired elements**
   - What we know: `StandingItem` already has a `.clickable` — it was never wired but looks tappable. `MatchDetailsTeamTable` player cells already use `LeftColumnPlayerCell` with `onClick` — those also look interactive.
   - What's unclear: Do any items look non-clickable but are being made clickable? No new affordances are needed — the existing UI already communicates clickability.
   - Recommendation: No additional affordance work needed. Left as Claude's discretion.

---

## Sources

### Primary (HIGH confidence)
- Direct code inspection of all 9 relevant source files
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt`
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt`
- `MatchDetailsScreen.kt`, `MatchDetailsTeamTable.kt`
- `StandingsScreen.kt`, `CompetitionItem.kt`
- `AllTimeLeadersScreen.kt`, `LeaderItem.kt`
- `SearchScreen.kt`, `SearchListItem.kt`
- `MainScreen.kt`

### Secondary (MEDIUM confidence)
- `05-CONTEXT.md` — user decisions, cross-validated against code
- `STATE.md` — accumulated project decisions confirming existing patterns

---

## Metadata

**Confidence breakdown:**
- Current state of each requirement: HIGH — verified by reading every affected file
- Standard stack: HIGH — reading App.kt and Screen.kt directly
- Pitfalls: HIGH — derived from actual code structure found
- Test infrastructure: HIGH — no test files found in the project

**Research date:** 2026-03-17
**Valid until:** 2026-04-17 (stable — no external dependencies, pure internal wiring)
