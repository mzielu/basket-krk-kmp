# Phase 3: Team Data Layer - Context

**Gathered:** 2026-03-17
**Status:** Ready for planning

<domain>
## Phase Boundary

Build DTOs, Ktor service endpoints, repository, use cases, and Koin DI wiring for all 4 team API endpoints. Create the TeamDetails screen shell with 3-tab navigation, team info header with logo and W-L record. Each tab shows a loading state confirming the data pipeline works end-to-end.

This phase does NOT implement the full tab content (results list, roster table, records filtering) — that's Phase 4. This phase also completes the `Screen.TeamDetails` route stub created in Phase 2.

</domain>

<decisions>
## Implementation Decisions

### Tab loading strategy
- Same as Phase 1: fetch tab data on tab selection, not all at once on screen open
- Cache tab data once loaded — data persists until user leaves TeamDetails screen entirely
- No refetch on tab switch-back within the same screen session

### Team header layout
- Match Flutter exactly: team logo, team name, list of seasons played
- Below team name: league name, W-L record, and +/- point differential for selected season
- Team logo loaded via Coil (same as PlayerDetails)
- Seasons sorted descending (most recent first) — same as Phase 1

### Season default behavior
- Same as Phase 1: always default to the team's most recent season
- No context-aware season selection from navigation source

### W-L record display
- Match Flutter: show league name, then "W-L" format (e.g., "12-3"), then point differential (e.g., "+145")
- W-L record comes from the team results API response for the selected season
- Point differential calculated from results data

### Claude's Discretion
- Exact header layout spacing and typography
- How to display seasons list (chips, dropdown, or inline text — match PlayerDetails approach)
- Loading indicator style per tab
- Error state design per tab
- How to handle W-L display when no season results are loaded yet

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (migration reference)
- `~/Documents/Development/flutter/basket_krk/lib/data/datasource/team_remote_datasource.dart` — API endpoint paths and parameter patterns
- `~/Documents/Development/flutter/basket_krk/lib/data/model/team/details/team_details_dto.dart` — DTO field mapping
- `~/Documents/Development/flutter/basket_krk/lib/data/model/team/results/team_result_list_dto.dart` — Results DTO structure (includes league info + W-L)
- `~/Documents/Development/flutter/basket_krk/lib/data/model/other/player_with_stat_list_dto.dart` — Roster DTO structure
- `~/Documents/Development/flutter/basket_krk/lib/data/model/records/team_record_list_dto.dart` — Team records DTO structure
- `~/Documents/Development/flutter/basket_krk/lib/presentation/teams/details/team_details_screen.dart` — Screen shell and header layout reference

### Existing KMP patterns (follow these — mirror Phase 1 approach)
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkPlayerService.kt` — Network service pattern (Phase 1 created this)
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/PlayerRepositoryImpl.kt` — Repository pattern
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerDetailsUseCase.kt` — Use case pattern with season sort
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt` — ViewModel with per-tab ViewStateData
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt` — Screen with tabs (match this structure)

### Existing reusable models
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Team.kt` — Team model already created in Phase 1 (id, name, logoUrl)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Stat.kt` — Stat model (reuse for roster stats)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Season.kt` — Season model (reuse)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/League.kt` — League model (reuse)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerWithStat.kt` — PlayerWithStat model (reuse for roster)
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` — Screen.TeamDetails stub already exists from Phase 2

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `Team` model (id, name, logoUrl): Already created in Phase 1 — reuse for team details
- `PlayerWithStat`: Player + stat data — reuse for roster data
- `Stat` data class: Full stat model — reuse for roster stats
- `Season`, `League`: Reuse for season/league references
- `ViewStateData<T>`: Loading/error/data wrapper — use for each tab's state
- `BasketKrkImage`: Coil image loader — use for team logo
- `Screen.TeamDetails(teamId: Int)`: Navigation stub already exists from Phase 2

### Established Patterns (from Phase 1)
- **Network service**: `Either.catchWithError { apiService.get<Dto>(path).toDomain() }`
- **DTO mapping**: `fun XxxDto.toDomain(): DomainModel` extension functions
- **Use case**: Interface + implementation with `SuspendInOutUseCase<Input, Output>`
- **ViewModel**: MutableStateFlow + ViewStateData wrapper per tab
- **DI registration**: Services in DataModule, use cases + ViewModels in PresentationModule

### Integration Points
- `Screen.kt` — `Screen.TeamDetails` already exists as stub, just wire it to actual screen
- `App.kt` — Replace `onNavigateToTeam` TODO with actual `navController.navigate(Screen.TeamDetails(teamId = it))`
- `DataModule.kt` — Register TeamService and TeamRepository
- `PresentationModule.kt` — Register team use cases and TeamDetailsViewModel

</code_context>

<specifics>
## Specific Ideas

- Mirror Phase 1 structure exactly — same layer pattern (domain models → DTOs → service → repository → use cases → ViewModel → screen)
- API endpoints: `/team/{id}/`, `/team/{id}/results?season_id={id}`, `/team/{id}/players?season_id={id}`, `/team/{id}/records?cat={category}`
- W-L record and point differential come from the results endpoint response (Flutter wraps this in TeamResultList with league info)
- The team records endpoint uses a `cat` parameter for category filtering — encode this in the service/repository

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 03-team-data-layer*
*Context gathered: 2026-03-17*
