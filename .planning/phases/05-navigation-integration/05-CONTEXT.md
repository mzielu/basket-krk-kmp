# Phase 5: Navigation Integration - Context

**Gathered:** 2026-03-17
**Status:** Ready for planning

<domain>
## Phase Boundary

Wire all remaining navigation entry points from existing screens to PlayerDetails and TeamDetails. This is a wiring-only phase — no new screens or data layers.

Cross-navigation between PlayerDetails and TeamDetails (NAV-04) is already implemented in Phases 2 and 4. This phase handles the remaining 4 entry points: MatchDetails, Standings, AllTimeLeaders, and Search.

</domain>

<decisions>
## Implementation Decisions

### MatchDetails navigation (NAV-01)
- Match Flutter: player names in the stat table are clickable → opens PlayerDetails
- Team names in the stat table may also be clickable → opens TeamDetails (match Flutter behavior)
- The `MatchDetailsTeamTable` component already has an `onPlayerClick` callback — wire it to navigate

### Standings navigation (NAV-02)
- Match Flutter: team name/entry in standings table is clickable → opens TeamDetails

### AllTimeLeaders navigation (NAV-03)
- Match Flutter: tapping a player entry in the all-time leaders list opens PlayerDetails
- The `LeaderItem` component may already have a click handler — wire it to navigate

### Search navigation (NAV-05)
- Match Flutter: tap player search result → PlayerDetails, tap team search result → TeamDetails
- `SearchItem` sealed class already distinguishes `Player` and `Team` — use type to determine destination

### Cross-navigation (NAV-04)
- Already implemented:
  - PlayerDetails stats tab → TeamDetails (Phase 2, wired via `onNavigateToTeam`)
  - TeamDetails roster → PlayerDetails (Phase 4, wired via `onNavigateToPlayer`)
- Verify these still work correctly after Phase 5 changes

### Claude's Discretion
- Whether to add visual click affordance (ripple, underline) to newly-clickable elements
- How to handle any existing click handlers that may conflict

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Existing screens to modify
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsScreen.kt` — MatchDetails screen, needs player/team click wiring
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/components/MatchDetailsTeamTable.kt` — Stat table with player click callback
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/standings/StandingsScreen.kt` — Standings, needs team click
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersScreen.kt` — All-time leaders, needs player click
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/search/SearchScreen.kt` — Search results, needs player/team click

### Navigation infrastructure
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` — Route definitions (PlayerDetails, TeamDetails already exist)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` — NavHost with all route entries

### Flutter reference
- `~/Documents/Development/flutter/basket_krk/lib/presentation/matches/details/match_details_screen.dart` — Player/team click handling in Flutter
- `~/Documents/Development/flutter/basket_krk/lib/presentation/main/search/search_screen.dart` — Search result navigation in Flutter

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `Screen.PlayerDetails(playerId: Int)` and `Screen.TeamDetails(teamId: Int)` — routes already exist
- `navController.navigate()` — standard navigation pattern used throughout App.kt
- `MatchDetailsTeamTable` has `onPlayerClick` callback — may already be partially wired
- `SearchItem.Player` and `SearchItem.Team` — type discrimination for search navigation

### Established Patterns
- Navigation callbacks flow from App.kt → Screen composable → content → sub-components
- Click handlers as lambda parameters on composable functions
- `navController.navigate(Screen.PlayerDetails(playerId = id))` pattern

### Integration Points
- `App.kt` — Add/update navigation callbacks for MatchDetails, Standings, AllTimeLeaders, Search screens
- Each screen's composable — Accept and thread navigation callbacks
- Each screen's ViewModel — May need to expose player/team IDs for navigation

</code_context>

<specifics>
## Specific Ideas

- Match Flutter navigation behavior exactly for all 4 remaining entry points
- All navigation uses the same `navController.navigate(Screen.X(...))` pattern
- Search navigation determined by `SearchItem` sealed class type (Player vs Team)

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 05-navigation-integration*
*Context gathered: 2026-03-17*
