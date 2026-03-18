# Phase 6: Season Leaders - Context

**Gathered:** 2026-03-18
**Status:** Ready for planning

<domain>
## Phase Boundary

Build the Season Leaders feature end-to-end: data layer (DTO, domain model, service, repository, use case) and presentation layer (ViewModel, screen with 3 filter dropdowns, leader list with player navigation). Add a navigation entry point as a clickable item on the existing Statistics tab.

This phase does NOT modify AllTimeLeaders or other existing screens.

</domain>

<decisions>
## Implementation Decisions

### Leader item layout
- Reuse the existing AllTimeLeaders `LeaderItem` composable (or extend it)
- Shows: position, team logo, player name, stat value
- Additional info: made/attempts for shooting stats (FT%, FG%, 3FG%), games played for counting stats (PTS, AST, REB, STL, BLK)
- Tapping a leader entry navigates to PlayerDetails (SLDR-06)

### Filter dropdowns layout
- Match Flutter: 3 dropdowns above the leaders list
- Season dropdown (narrow), League dropdown, Category dropdown
- All use `DropdownFormField` component (same as matches screen and other filters)
- Cascading behavior: changing season updates available leagues; changing league or category updates leaders list

### Entry point
- Clickable item on the existing Statistics tab (not a separate entry screen)
- Tapping "Season Leaders" item navigates to the full Season Leaders screen
- This means adding a navigation item to the statistics section, alongside AllTimeLeaders and Standings

### Default selections
- Match Flutter: default to most recent season, first available league, PTS category

### Claude's Discretion
- Exact LeaderItem extension for additional info display (inline text, subtitle, or separate column)
- How to add the "Season Leaders" item in the Statistics tab layout
- Loading/error state design
- Empty state when no leaders for selected filters

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (migration reference)
- `~/Documents/Development/flutter/basket_krk/lib/presentation/stats/stats_screen.dart` — Season Leaders screen layout, dropdowns, leader list
- `~/Documents/Development/flutter/basket_krk/lib/presentation/stats/stats_bloc.dart` — BLoC events for season/league/category changes
- `~/Documents/Development/flutter/basket_krk/lib/presentation/stats/model/stat_leader_option.dart` — 8 stat category options
- `~/Documents/Development/flutter/basket_krk/lib/domain/model/stat/league_leader.dart` — LeagueLeader domain model
- `~/Documents/Development/flutter/basket_krk/lib/data/datasource/league_remote_datasource.dart` — API endpoint for leaders

### Existing KMP components (reuse these)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersScreen.kt` — AllTimeLeaders screen pattern and LeaderItem composable
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/DropdownFormField.kt` — Filter dropdown component
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/standings/StandingsScreen.kt` — Season/League cascading filter pattern (already implemented)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/LeagueLeader.kt` — LeagueLeader model may already exist

### Existing reusable models
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Season.kt` — Season model
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/League.kt` — League model
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ViewStateData.kt` — Loading/error/data wrapper

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `LeaderItem` composable from AllTimeLeaders — reuse or extend for season leaders
- `DropdownFormField` — reuse for all 3 filter dropdowns
- `StandingsViewModel` already has season/league cascading pattern — follow same approach
- `Season`, `League` models — already exist
- `LeagueLeader` model — may already exist from existing KMP code
- `ViewStateData<T>` — for loading/error/data states

### Established Patterns
- MVVM + StateFlow for ViewModel
- `DropdownFormField` for filter dropdowns
- Lambda callbacks for navigation
- `Either.catchWithError` for API calls
- Koin DI registration in DataModule + PresentationModule

### Integration Points
- Statistics tab — add "Season Leaders" clickable item
- `Screen.kt` — add `SeasonLeaders` route
- `App.kt` — add `composable<Screen.SeasonLeaders>` entry
- `DataModule.kt` — register season leaders service/repository
- `PresentationModule.kt` — register use case and ViewModel

</code_context>

<specifics>
## Specific Ideas

- 1:1 migration from Flutter — match same API contract and behavior
- 8 stat categories: PTS, AST, REB, STL, BLK, FT%, FG%, 3FG%
- API endpoint likely: league leaders endpoint with leagueId + category parameter
- Cascading dropdowns: season → leagues → leaders (same pattern as Standings screen)
- Entry point: item in Statistics tab, not a separate entry screen

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 06-season-leaders*
*Context gathered: 2026-03-18*
