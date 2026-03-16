# Phase 1: Player Data Layer - Context

**Gathered:** 2026-03-16
**Status:** Ready for planning

<domain>
## Phase Boundary

Build DTOs, Ktor service endpoints, repository, use cases, and Koin DI wiring for all 4 player API endpoints. Create the PlayerDetails screen shell with 3-tab navigation and info header. Each tab shows a loading state confirming the data pipeline works end-to-end.

This phase does NOT implement the full tab content (scrollable tables, filtering, sorting) — that's Phase 2.

</domain>

<decisions>
## Implementation Decisions

### Tab loading strategy
- Fetch tab data on tab selection, not all at once on screen open (matches Flutter behavior)
- Cache tab data once loaded — data persists until user leaves PlayerDetails screen entirely
- No refetch on tab switch-back within the same screen session

### Player header layout
- Match Flutter exactly: player name, current team name with logo, list of seasons played
- Team logo loaded via Coil (already in project for other screens)
- Seasons sorted descending (most recent first) — same as Flutter's GetPlayerDetailsUseCase

### Season default behavior
- Always default to the player's most recent season
- No context-aware season selection from navigation source (keep it simple, match Flutter)

### Claude's Discretion
- Exact header layout spacing and typography
- How to display seasons list (chips, dropdown, or inline text)
- Loading indicator style per tab (spinner vs skeleton)
- Error state design per tab

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Flutter source (migration reference)
- `~/Documents/Development/flutter/basket_krk/lib/data/datasource/player_remote_datasource.dart` — API endpoint paths and parameter patterns
- `~/Documents/Development/flutter/basket_krk/lib/data/model/player/player_details_dto.dart` — DTO field mapping (abbreviated JSON fields: fn, ln, t, etc.)
- `~/Documents/Development/flutter/basket_krk/lib/data/model/player/logs/player_log_list_dto.dart` — Game logs DTO structure
- `~/Documents/Development/flutter/basket_krk/lib/data/model/player/stats/player_stat_list_dto.dart` — Player stats DTO structure
- `~/Documents/Development/flutter/basket_krk/lib/data/model/player/records/player_records_dto.dart` — Player records DTO structure
- `~/Documents/Development/flutter/basket_krk/lib/domain/model/player/player_details.dart` — Domain model structure
- `~/Documents/Development/flutter/basket_krk/lib/presentation/players/details/player_details_screen.dart` — Screen shell and tab layout reference

### Existing KMP patterns (follow these)
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkMatchService.kt` — Network service pattern to follow
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/MatchDetailsTeamDto.kt` — DTO + toDomain() pattern
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetMatchDetailsUseCase.kt` — Use case pattern
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsViewModel.kt` — ViewModel with ViewStateData pattern
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsScreen.kt` — Screen with tabs reference

### Existing reusable models
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerShort.kt` — Existing player model (id + name)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerWithStat.kt` — Player + stat association
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Stat.kt` — Stat model (reuse for game logs and stats)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Season.kt` — Season model (reuse)
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/SearchItem.kt` — SearchItem.Player and SearchItem.Team (reuse for team reference)

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- `PlayerShort` (id, name): Can represent player identity in game logs and stats
- `PlayerWithStat`: Already pairs player with stat data — reuse for roster and stats
- `Stat` data class: Full basketball stat model already exists with all fields (pts, ast, reb, etc.)
- `Season` model: Reuse for season dropdown/selection
- `SearchItem.Team`: Has id, name, logoPath — can represent current team in header
- `ViewStateData<T>`: Loading/error/data wrapper — use for each tab's state
- Coil image loading: Already configured in project for network images

### Established Patterns
- **Network service**: `Either.catchWithError { apiService.get<Dto>(path).toDomain() }` — follow this
- **DTO mapping**: Extension function `fun XxxDto.toDomain(): DomainModel` on DTO classes
- **Use case**: Interface + implementation with `SuspendInOutUseCase<Input, Output>`
- **ViewModel**: MutableStateFlow + ViewStateData wrapper per data concern
- **DI registration**: Services in DataModule, use cases + ViewModels in PresentationModule

### Integration Points
- `Screen.kt` — Add `PlayerDetails(playerId: Int)` route
- `App.kt` — Add `composable<Screen.PlayerDetails>` navigation entry
- `DataModule.kt` — Register PlayerService and PlayerRepository
- `PresentationModule.kt` — Register use cases and PlayerDetailsViewModel

</code_context>

<specifics>
## Specific Ideas

- 1:1 migration from Flutter — match the same API contract and behavior
- API endpoints: `/player/{id}/`, `/player/{id}/logs?season_id={id}`, `/player/{id}/stats/`, `/player/{id}/records/`
- Flutter uses abbreviated JSON fields (fn=firstname, ln=lastname, t=team) — DTO must match API response exactly
- Flutter sorts seasons descending in GetPlayerDetailsUseCase — replicate this

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope

</deferred>

---

*Phase: 01-player-data-layer*
*Context gathered: 2026-03-16*
