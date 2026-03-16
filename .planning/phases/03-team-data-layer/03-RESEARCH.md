# Phase 3: Team Data Layer - Research

**Researched:** 2026-03-17
**Domain:** Kotlin Multiplatform / Compose Multiplatform — data layer + screen shell, mirroring Phase 1 (Player)
**Confidence:** HIGH

---

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions

**Phase boundary:** Build DTOs, Ktor service endpoints, repository, use cases, and Koin DI wiring for all 4 team API endpoints. Create the TeamDetails screen shell with 3-tab navigation, team info header with logo and W-L record. Each tab shows a loading state confirming the data pipeline works end-to-end. This phase does NOT implement full tab content (results list, roster table, records filtering) — that is Phase 4.

**Tab loading strategy:** Same as Phase 1 — fetch tab data on tab selection, not all at once on screen open. Cache tab data once loaded (data persists until user leaves TeamDetails screen). No refetch on tab switch-back within the same screen session.

**Team header layout:** Match Flutter exactly: team logo, team name, list of seasons played. Below team name: league name, W-L record, and +/- point differential for selected season. Team logo loaded via Coil (same as PlayerDetails). Seasons sorted descending (most recent first) — same as Phase 1.

**Season default behavior:** Same as Phase 1 — always default to the team's most recent season. No context-aware season selection from navigation source.

**W-L record display:** Match Flutter: show league name, then "W-L" format (e.g., "12-3"), then point differential (e.g., "+145"). W-L record comes from the team results API response for the selected season. Point differential calculated from results data.

### Claude's Discretion

- Exact header layout spacing and typography
- How to display seasons list (chips, dropdown, or inline text — match PlayerDetails approach)
- Loading indicator style per tab
- Error state design per tab
- How to handle W-L display when no season results are loaded yet

### Deferred Ideas (OUT OF SCOPE)

None — discussion stayed within phase scope.
</user_constraints>

---

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| TEAM-01 | User can view team info header showing name, logo, seasons played | TeamDetailsDto maps to new TeamDetails domain model; BasketKrkImage for logo; seasons list rendered as joined text (matches PlayerDetails approach) |
| TEAM-02 | User can see team W-L record and point differential for selected season | TeamResultListDto includes results + league; W-L/+/- computed in ViewModel from TeamResult list filtering on FINISHED/WALKOVER + REGULAR_SEASON entries; displayed in header below team name |
| TEAM-03 | User can navigate between 3 tabs: Results, Roster, Records | PrimaryTabRow with 3 tabs (same as PlayerDetailsScreen); per-tab ViewStateData in TeamDetailsViewState; each tab fetches on selection, caches in-session |
</phase_requirements>

---

## Summary

Phase 3 is a direct structural mirror of Phase 1 (Player Data Layer). The codebase already has every supporting element in place: Arrow Either error handling, Ktor ApiService, Koin DI modules, ViewStateData wrapper, BasketKrkImage for Coil-loaded logos, Season/League/PlayerWithStat/MatchStatus/MatchType domain models, and the exact tab + header layout pattern used in PlayerDetailsScreen.

The Flutter source gives exact API paths, JSON field names, and domain model shapes for all four endpoints. The most nuanced mapping is the team results list: the `TeamResultListDto` carries both a `data: List<TeamResultDto>` and a `lg: LeagueDto`. The W-L record and point differential are computed client-side from the results list (not a dedicated API field), filtering for FINISHED + REGULAR_SEASON entries — matching Flutter's `_getWinsLost` and `_getPlusMinus` helpers.

The team records endpoint uses a composite `cat` parameter of the form `"{stat}_{range}"` (e.g., `"pts_t"` for PTS All-Time, `"ast_m"` for AST Match). This is a new enum pattern not yet in the KMP codebase. New enums `TeamRecordStatOption` and `TeamRecordRange` must be created in the domain layer; a helper function builds the `cat` string for the service call.

**Primary recommendation:** Follow the Phase 1 file-by-file structure exactly. Create 4 new domain models, 5 new DTOs, 1 service interface + impl, 1 repository interface + impl, 4 use case interface + impl pairs, 1 ViewModel + ViewState, and 1 Screen composable with 3 stub tab composables. Wire DI in DataModule + PresentationModule, then fix the 2 TODO navigation stubs in App.kt.

---

## Standard Stack

### Core (all already in project — no new dependencies needed)

| Component | Version | Purpose | Why Standard |
|-----------|---------|---------|--------------|
| Ktor client (via ApiService) | existing | HTTP requests | Established in Phase 1 — all services use `apiService.get<Dto>(path)` |
| kotlinx.serialization | existing | DTO deserialization | All existing DTOs use `@Serializable` |
| Arrow Either (`arrow.core`) | existing | Typed error propagation | Project-wide — `Either<Failure, T>` throughout |
| Koin | existing | DI registration | DataModule + PresentationModule pattern established |
| Coil 3 (`coil3.compose`) | existing | Logo image loading | `BasketKrkImage` composable already handles URL prefixing |
| Compose Material3 | existing | PrimaryTabRow, Scaffold, Text | Same tab pattern as PlayerDetailsScreen |
| ViewModel + StateFlow | existing | MVVM presentation | `MutableStateFlow<ViewState>` + `viewModelScope.launch` |

### No New Dependencies Required

All libraries needed for Phase 3 are already declared and used in Phase 1 and Phase 2.

---

## Architecture Patterns

### Recommended Project Structure

Follow the Phase 1 / PlayerDetails file layout exactly:

```
domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/
├── model/
│   ├── TeamDetails.kt          # NEW: id, name, logoUrl, seasons, league
│   ├── TeamResult.kt           # NEW: id, opponent (MatchTeam), points, date, status, type
│   ├── TeamResultList.kt       # NEW: data (List<TeamResult>), league (League)
│   ├── TeamRecord.kt           # NEW: player (PlayerShort), value, position, games, ats?, sNum?, matchId?
│   ├── TeamRecordStatOption.kt # NEW: enum PTS/AST/REB/STL/BLK/EFF/FT/FG/FG3 with apiKey
│   └── TeamRecordRange.kt      # NEW: enum ALL_TIME/SEASON/MATCH with apiKey
├── service/
│   └── TeamService.kt          # NEW: interface with 4 suspend fun methods
├── repository/
│   └── TeamRepository.kt       # NEW: interface mirroring TeamService
└── usecase/
    ├── GetTeamDetailsUseCase.kt       # NEW: seasonSort + interface
    ├── GetTeamResultsUseCase.kt       # NEW: interface
    ├── GetTeamRosterUseCase.kt        # NEW: interface
    └── GetTeamRecordsUseCase.kt       # NEW: interface

data/src/commonMain/kotlin/com/mzs/basket_krk/data/
├── dto/
│   ├── TeamDetailsDto.kt        # NEW: @Serializable + toDomain()
│   ├── TeamResultDto.kt         # NEW: @Serializable + toDomain()
│   ├── TeamResultListDto.kt     # NEW: @Serializable + toDomain()
│   └── TeamRecordDto.kt         # NEW: @Serializable + toDomain()
│   # TeamRecordListDto is just List<TeamRecordDto> wrapper — see note below
├── service/
│   └── NetworkTeamService.kt    # NEW: implements TeamService
├── repository/
│   └── TeamRepositoryImpl.kt    # NEW: implements TeamRepository
└── di/
    └── DataModule.kt            # EDIT: add TeamService + TeamRepository bindings

presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/
├── screens/
│   └── teamdetails/
│       ├── TeamDetailsViewModel.kt            # NEW
│       ├── TeamDetailsScreen.kt               # NEW: screen shell + header
│       └── components/
│           ├── TeamResultsTab.kt              # NEW: stub (loading placeholder)
│           ├── TeamRosterTab.kt               # NEW: stub (loading placeholder)
│           └── TeamRecordsTab.kt              # NEW: stub (loading placeholder)
├── di/
│   └── PresentationModule.kt    # EDIT: add 4 use cases + TeamDetailsViewModel
└── App.kt                       # EDIT: fix 2 TODO navigation stubs
```

### Pattern 1: DTO + Domain Model (established)

All DTOs use `@Serializable` data class + `fun XxxDto.toDomain()` extension in the same file.

```kotlin
// data/src/commonMain/.../data/dto/TeamDetailsDto.kt
@Serializable
data class TeamDetailsDto(
    val id: Int,
    val name: String,
    val logo: String,
    val seasons: List<SeasonDto>,
    val last_league: LeagueDto? = null
)

fun TeamDetailsDto.toDomain() = TeamDetails(
    id = id,
    name = name,
    logoUrl = logo,
    seasons = seasons.map { it.toDomain() },
    league = last_league?.toDomain()
)
```

### Pattern 2: Network Service (established)

```kotlin
// data/src/commonMain/.../data/service/NetworkTeamService.kt
class NetworkTeamService(private val apiService: ApiService) : TeamService {
    override suspend fun getTeamDetails(teamId: Int): Either<Failure, TeamDetails> =
        Either.catchWithError { apiService.get<TeamDetailsDto>("/team/$teamId/").toDomain() }

    override suspend fun getTeamResults(teamId: Int, seasonId: Int): Either<Failure, TeamResultList> =
        Either.catchWithError { apiService.get<TeamResultListDto>("/team/$teamId/results?season_id=$seasonId").toDomain() }

    override suspend fun getTeamRoster(teamId: Int, seasonId: Int): Either<Failure, List<PlayerWithStat>> =
        Either.catchWithError { apiService.get<TeamRosterDto>("/team/$teamId/players?season_id=$seasonId").toDomain() }

    override suspend fun getTeamRecords(teamId: Int, category: String): Either<Failure, List<TeamRecord>> =
        Either.catchWithError { apiService.get<TeamRecordListDto>("/team/$teamId/records?cat=$category").toDomain() }
}
```

### Pattern 3: Repository (thin delegation — established)

```kotlin
class TeamRepositoryImpl(private val teamService: TeamService) : TeamRepository {
    override suspend fun getTeamDetails(teamId: Int) = teamService.getTeamDetails(teamId)
    override suspend fun getTeamResults(teamId: Int, seasonId: Int) = teamService.getTeamResults(teamId, seasonId)
    override suspend fun getTeamRoster(teamId: Int, seasonId: Int) = teamService.getTeamRoster(teamId, seasonId)
    override suspend fun getTeamRecords(teamId: Int, category: String) = teamService.getTeamRecords(teamId, category)
}
```

### Pattern 4: Use Case with Season Sort (established)

`GetTeamDetailsUseCase` must sort seasons descending — matches Phase 1 `GetPlayerDetailsUseCase` exactly:

```kotlin
class GetTeamDetailsUseCase(private val teamRepository: TeamRepository) : GetTeamDetails {
    override suspend fun invoke(input: Input): Either<Failure, TeamDetails> =
        teamRepository.getTeamDetails(input.teamId).map { details ->
            details.copy(seasons = details.seasons.sortedByDescending { it.num })
        }
    data class Input(val teamId: Int)
}
```

### Pattern 5: ViewModel with Per-Tab ViewStateData (established)

```kotlin
// Mirror PlayerDetailsViewModel exactly
class TeamDetailsViewModel(
    private val teamId: Int,
    private val getTeamDetails: GetTeamDetails,
    private val getTeamResults: GetTeamResults,
    private val getTeamRoster: GetTeamRoster,
    private val getTeamRecords: GetTeamRecords
) : ViewModel() {
    // init -> fetchTeamDetails() -> on success auto-fetch tab 0 (Results)
    // onTabSelected(0) -> fetchResultsIfNeeded(season.id)
    // onTabSelected(1) -> fetchRosterIfNeeded(season.id)
    // onTabSelected(2) -> fetchRecordsIfNeeded(default category)
    // cache check: current.data != null && !current.isError -> return (skip fetch)
}

@Immutable
data class TeamDetailsViewState(
    val teamDetails: ViewStateData<TeamDetails?> = ViewStateData(null),
    val results: ViewStateData<TeamResultList?> = ViewStateData(null),
    val roster: ViewStateData<List<PlayerWithStat>?> = ViewStateData(null),
    val records: ViewStateData<List<TeamRecord>?> = ViewStateData(null),
    val selectedSeason: Season? = null,
    val selectedRecordStatOption: TeamRecordStatOption = TeamRecordStatOption.PTS,
    val selectedRecordRange: TeamRecordRange = TeamRecordRange.ALL_TIME,
)
```

### Pattern 6: Screen Shell (matches PlayerDetailsScreen)

```kotlin
// Scaffold + ActionBar (title = team name)
// Box: loading/error/content switch on teamDetails state
// Content: Column { Header; HorizontalDivider; PrimaryTabRow(3 tabs); Box(tab content) }
// Header: Row { BasketKrkImage(logo, 64dp); Column { name, seasons text, W-L info } }
// Tab content: each tab Box { when loading/error/data -> FullScreenLoader/ErrorView/StubText }
```

### Pattern 7: W-L and Point Differential Computation

The W-L and +/- are NOT returned by the API as ready-to-display strings. They are computed client-side from the results list, filtering on two conditions:

```kotlin
// In ViewModel or use case — mirror Flutter's _getWinsLost / _getPlusMinus
private fun computeWinsLosses(results: List<TeamResult>): Pair<Int, Int> {
    val ended = results.filter {
        (it.status == MatchStatus.FINISHED || it.status == MatchStatus.WALKOVER)
        && it.type == MatchType.REGULAR_SEASON
    }
    val wins = ended.count { it.points > it.opponent.points }
    val losses = ended.count { it.opponent.points > it.points }
    return wins to losses
}

private fun computePlusMinus(results: List<TeamResult>): Int {
    val ended = results.filter {
        (it.status == MatchStatus.FINISHED || it.status == MatchStatus.WALKOVER)
        && it.type == MatchType.REGULAR_SEASON
    }
    return ended.sumOf { it.points } - ended.sumOf { it.opponent.points }
}
```

Display format: `"${wins}-${losses}"` and `"${if (pm >= 0) "+" else ""}${pm}"`.

These computations are best placed in the ViewModel (inline when processing results), or as a helper in the domain model. The W-L display in the header requires the results to be loaded; before results load, the header shows the team name/logo/seasons without W-L values (show "-" or hide the row).

### Pattern 8: Team Records `cat` Parameter

The records endpoint uses a composite category string: `"{stat}_{range}"`. New enums with `apiKey` properties:

```kotlin
// domain model
enum class TeamRecordStatOption(val apiKey: String) {
    PTS("pts"), AST("ast"), REB("reb"), STL("stl"), BLK("blk"),
    EFF("eff"), FT("ft"), FG("fg"), FG3("fg3")
}

enum class TeamRecordRange(val apiKey: String) {
    ALL_TIME("t"), SEASON("s"), MATCH("m")
}

// Helper — use in ViewModel when calling GetTeamRecords
fun buildRecordCategory(stat: TeamRecordStatOption, range: TeamRecordRange): String =
    "${stat.apiKey}_${range.apiKey}"

// Default first call: "pts_t" (PTS, All-Time)
```

### Anti-Patterns to Avoid

- **Fetching all tabs on screen open:** Only fetch the first tab on init (after team details load). Fetch other tabs lazily on tab selection.
- **Re-fetching on tab switch-back:** Cache check is `current.data != null && !current.isError`. Only re-fetch if no data yet or data is in error state.
- **Hardcoding W-L as API field:** The API returns raw results — W-L must be computed from the list.
- **New TeamDto for team records player field:** `TeamRecordDto.player` uses the full `PlayerDto` (id, fn, ln, t?), NOT `PlayerShortDto`. Map to `PlayerShort` (id + full name "fn ln") for simplicity, as done for `PlayerWithStat`. Confirm the exact field names in the API response.
- **Ignoring `last_league` nullability:** `TeamDetailsDto.last_league` is nullable. Map to `League?` in domain model.

---

## API Endpoints

All 4 endpoints confirmed from Flutter source (`team_remote_datasource.dart`):

| Method | Path | Parameters | Returns |
|--------|------|-----------|---------|
| GET | `/team/{teamId}/` | none | TeamDetailsDto |
| GET | `/team/{teamId}/results` | `season_id={id}` | TeamResultListDto |
| GET | `/team/{teamId}/players` | `season_id={id}` | TeamRosterDto (wraps `List<PlayerWithStatDto>`) |
| GET | `/team/{teamId}/records` | `cat={stat}_{range}` | TeamRecordListDto (wraps `List<TeamRecordDto>`) |

---

## DTO Field Mapping

### TeamDetailsDto → TeamDetails

| DTO field | Type | Domain field | Notes |
|-----------|------|-------------|-------|
| `id` | Int | `id` | direct |
| `name` | String | `name` | direct |
| `logo` | String | `logoUrl` | BasketKrkImage adds base URL prefix |
| `seasons` | `List<SeasonDto>` | `seasons` | each `.toDomain()` |
| `last_league` | `LeagueDto?` | `league: League?` | nullable, reuse existing `LeagueDto.toDomain()` |

### TeamResultListDto → TeamResultList

| DTO field | Type | Domain field | Notes |
|-----------|------|-------------|-------|
| `data` | `List<TeamResultDto>` | `data` | each `.toDomain()` |
| `lg` | `LeagueDto` | `league` | reuse existing `LeagueDto.toDomain()` |

### TeamResultDto → TeamResult

| DTO field | Type | Domain field | Notes |
|-----------|------|-------------|-------|
| `id` | Int | `id` | direct |
| `opp` | `MatchTeamDto` | `opponent: MatchTeam` | reuse existing `MatchTeamDto.toDomain()` |
| `pts` | Int | `points` | direct |
| `date` | String | `date` | direct |
| `status` | String | `status: MatchStatus` | `MatchStatus.fromKey(status)` — existing enum |
| `type` | String | `type: MatchType` | `MatchType.fromKey(type)` — existing enum |

### TeamRosterDto → List\<PlayerWithStat\>

The roster endpoint wraps `List<PlayerWithStatDto>` in a `data` field:

| DTO field | Type | Domain field | Notes |
|-----------|------|-------------|-------|
| `data` | `List<PlayerWithStatDto>` | direct list | each `.toDomain()` — reuses EXISTING `PlayerWithStatDto.toDomain()` |

A new `TeamRosterDto` is needed as the envelope:
```kotlin
@Serializable
data class TeamRosterDto(val data: List<PlayerWithStatDto>)
fun TeamRosterDto.toDomain(): List<PlayerWithStat> = data.map { it.toDomain() }
```

### TeamRecordDto → TeamRecord

| DTO field | Type | Domain field | Notes |
|-----------|------|-------------|-------|
| `player` | `PlayerDto` (id, fn, ln, t?) | `player: PlayerShort` | Map to PlayerShort(id, "$fn $ln") |
| `value` | Int | `value` | direct |
| `position` | Int | `position` | direct |
| `games` | Int | `games` | direct |
| `ats` | `Int?` | `ats: Int?` | nullable |
| `s_num` | `Int?` | `sNum: Int?` | nullable, rename to camelCase |
| `match_id` | `Int?` | `matchId: Int?` | nullable, rename to camelCase |

`TeamRecordListDto` wraps the list with a `data` field (same pattern as roster):
```kotlin
@Serializable
data class TeamRecordListDto(val data: List<TeamRecordDto>)
fun TeamRecordListDto.toDomain(): List<TeamRecord> = data.map { it.toDomain() }
```

**Critical note:** `TeamRecordDto.player` uses the same full `PlayerDto` shape (id, fn, ln, t?) as the existing `PlayerDetailsDto.t` field, NOT the abbreviated `PlayerShortDto` shape. A new inline `PlayerDto` for this context is needed OR reuse the existing `PlayerDetailsDto` team reference pattern. The safest approach is a new file-local `PlayerFullDto` or simply read id/fn/ln directly.

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| Image loading with URL prefix | Custom image component | `BasketKrkImage` (existing) | Already handles `https://www.basketkrk.pl/` prefix |
| Error state UI | Custom error composable | `ErrorView` (existing) | Established pattern with retry callback |
| Loading state UI | Custom loader | `FullScreenLoader` (existing) | Used in all screens |
| Either error propagation | Custom Result | `Either.catchWithError` (existing) | Handles all Throwable to Failure conversion |
| HTTP client setup | Manual Ktor config | `ApiService` (existing singleton) | Already configured and injected via Koin |
| Tab layout | Custom tab | `PrimaryTabRow` + `Tab` (Material3) | Exact pattern from PlayerDetailsScreen |
| Season sort logic | Custom comparator | `.sortedByDescending { it.num }` | Already used in GetPlayerDetailsUseCase |

---

## Common Pitfalls

### Pitfall 1: W-L Displayed Before Results Load

**What goes wrong:** Header renders with empty/wrong W-L because results haven't been fetched yet when team details first loads.
**Why it happens:** Team details load on init; results only load when the user enters tab 0 (after details succeed). Header is built from `teamDetails` state, but W-L data comes from `results` state.
**How to avoid:** Keep W-L display separate from the team details header. Add `winsLosses: Pair<Int,Int>?` and `pointDifferential: Int?` (nullable) to `TeamDetailsViewState`. Populate them when `results` successfully loads. Display "-" or omit the W-L row until results are available.
**Warning signs:** Showing "0-0" or crashing when accessing `results.data` from header composable.

### Pitfall 2: TeamRecordDto.player Field vs PlayerShortDto

**What goes wrong:** Using `PlayerShortDto` (id, name) for `TeamRecordDto.player` — the API sends the full player shape (id, fn, ln, t?).
**Why it happens:** The roster uses `PlayerWithStatDto.player: PlayerShortDto`, but records use a full `PlayerDto` (same as `PlayerDetailsDto`).
**How to avoid:** Define a separate `PlayerFullDto` (or inline `PlayerInRecordDto`) with `id`, `fn`, `ln`, and optional team. Map to `PlayerShort(id, "$fn $ln")` for domain — consistent with how PlayerDetails maps.
**Warning signs:** JSON parse failure (`MissingFieldException` for `fn`/`ln`) if the wrong DTO is used.

### Pitfall 3: `cat` Parameter Format

**What goes wrong:** Calling `/team/{id}/records?cat=pts` instead of `/team/{id}/records?cat=pts_t`.
**Why it happens:** The parameter encodes BOTH stat category AND range. The range suffix is easy to omit.
**How to avoid:** Always build the category through `buildRecordCategory(stat, range)`. Never hardcode a raw stat name.
**Warning signs:** API returning empty list or 400 error for records endpoint.

### Pitfall 4: App.kt Navigation Not Wired

**What goes wrong:** TeamDetails screen is registered in DI but App.kt still has the TODO stub for team navigation — screen never appears.
**Why it happens:** Phase 2 left two TODOs: `openTeamDetails = { /* TODO */ }` and `onNavigateToTeam = { /* TODO Phase 3 */ }`.
**How to avoid:** As part of the navigation task, fix BOTH stubs in App.kt (from MainScreen and from PlayerDetailsScreen) to use `navController.navigate(Screen.TeamDetails(teamId = it))`. Also add the `composable<Screen.TeamDetails>` block.
**Warning signs:** Pressing a team link navigates nowhere; screen is implemented but not reachable.

### Pitfall 5: Shared MatchTeamDto Already Exists

**What goes wrong:** Creating a duplicate `OpponentDto` when the existing `MatchTeamDto` covers the same JSON shape.
**Why it happens:** `TeamResultDto.opp` has the same shape as `MatchTeamDto` (id, name, s_name, logo, pts).
**How to avoid:** Reuse `MatchTeamDto` and `MatchTeamDto.toDomain()` — no new DTO needed for the opponent field.
**Warning signs:** Two DTOs with identical `@Serializable` structures causing redundancy.

---

## Code Examples

### TeamDetails Domain Model

```kotlin
// domain/src/commonMain/.../domain/model/TeamDetails.kt
package com.mzs.basket_krk.domain.model

data class TeamDetails(
    val id: Int,
    val name: String,
    val logoUrl: String,
    val seasons: List<Season>,
    val league: League?
)
```

### TeamResult and TeamResultList Domain Models

```kotlin
// domain/src/commonMain/.../domain/model/TeamResult.kt
data class TeamResult(
    val id: Int,
    val opponent: MatchTeam,
    val points: Int,
    val date: String,
    val status: MatchStatus,
    val type: MatchType
)

// domain/src/commonMain/.../domain/model/TeamResultList.kt
data class TeamResultList(
    val data: List<TeamResult>,
    val league: League
)
```

### TeamRecord Domain Model

```kotlin
// domain/src/commonMain/.../domain/model/TeamRecord.kt
data class TeamRecord(
    val player: PlayerShort,
    val value: Int,
    val position: Int,
    val games: Int,
    val ats: Int?,
    val sNum: Int?,
    val matchId: Int?
)
```

### TeamRecordStatOption and TeamRecordRange Enums

```kotlin
// domain/src/commonMain/.../domain/model/TeamRecordStatOption.kt
enum class TeamRecordStatOption(val apiKey: String) {
    PTS("pts"), AST("ast"), REB("reb"), STL("stl"), BLK("blk"),
    EFF("eff"), FT("ft"), FG("fg"), FG3("fg3")
}

// domain/src/commonMain/.../domain/model/TeamRecordRange.kt
enum class TeamRecordRange(val apiKey: String) {
    ALL_TIME("t"), SEASON("s"), MATCH("m")
}

// helper function (can live in either domain model file or ViewModel)
fun buildRecordCategory(stat: TeamRecordStatOption, range: TeamRecordRange): String =
    "${stat.apiKey}_${range.apiKey}"
```

### TeamResultListDto with toDomain

```kotlin
// data/src/commonMain/.../data/dto/TeamResultListDto.kt
@Serializable
data class TeamResultListDto(
    val data: List<TeamResultDto>,
    val lg: LeagueDto
)

fun TeamResultListDto.toDomain() = TeamResultList(
    data = data.map { it.toDomain() },
    league = lg.toDomain()
)
```

### TeamResultDto with toDomain

```kotlin
// data/src/commonMain/.../data/dto/TeamResultDto.kt
@Serializable
data class TeamResultDto(
    val id: Int,
    val opp: MatchTeamDto,
    val pts: Int,
    val date: String,
    val status: String,
    val type: String
)

fun TeamResultDto.toDomain() = TeamResult(
    id = id,
    opponent = opp.toDomain(),
    points = pts,
    date = date,
    status = MatchStatus.fromKey(status),
    type = MatchType.fromKey(type)
)
```

### TeamRecordDto with toDomain

```kotlin
// data/src/commonMain/.../data/dto/TeamRecordDto.kt
@Serializable
data class TeamRecordDto(
    val player: PlayerInRecordDto,
    val value: Int,
    val position: Int,
    val games: Int,
    val ats: Int? = null,
    val s_num: Int? = null,
    val match_id: Int? = null
)

@Serializable
data class PlayerInRecordDto(
    val id: Int,
    val fn: String,
    val ln: String
)

fun TeamRecordDto.toDomain() = TeamRecord(
    player = PlayerShort(id = player.id, name = "${player.fn} ${player.ln}"),
    value = value,
    position = position,
    games = games,
    ats = ats,
    sNum = s_num,
    matchId = match_id
)
```

### DI Registration

```kotlin
// DataModule.kt additions:
single<TeamService> { NetworkTeamService(get()) }
single<TeamRepository> { TeamRepositoryImpl(get()) }

// PresentationModule.kt additions:
single<GetTeamDetails> { GetTeamDetailsUseCase(get()) }
single<GetTeamResults> { GetTeamResultsUseCase(get()) }
single<GetTeamRoster> { GetTeamRosterUseCase(get()) }
single<GetTeamRecords> { GetTeamRecordsUseCase(get()) }
viewModel { (teamId: Int) -> TeamDetailsViewModel(teamId, get(), get(), get(), get()) }
```

### App.kt Navigation Stubs to Fix

```kotlin
// Fix 1: MainScreen callback
openTeamDetails = {
    navController.navigate(Screen.TeamDetails(teamId = it))
},

// Fix 2: PlayerDetailsScreen callback
onNavigateToTeam = { navController.navigate(Screen.TeamDetails(teamId = it)) },

// Fix 3: New composable block
composable<Screen.TeamDetails> { backStackEntry ->
    val args = backStackEntry.toRoute<Screen.TeamDetails>()
    val viewModel: TeamDetailsViewModel = koinInject(
        parameters = { parametersOf(args.teamId) }
    )
    TeamDetailsScreen(
        viewModel = viewModel,
        onNavigateBack = { navController.popBackStack() },
        onNavigateToPlayer = { navController.navigate(Screen.PlayerDetails(playerId = it)) },
        onNavigateToMatch = { navController.navigate(Screen.MatchDetails(matchId = it)) },
    )
}
```

---

## State of the Art

| Old Approach | Current Approach | Impact |
|--------------|-----------------|--------|
| Flutter BLoC event/state | KMP ViewModel + StateFlow | Direct 1:1 mapping of state fields; BLoC events become ViewModel functions |
| Flutter freezed DTOs | KMP @Serializable data classes | Same structural semantics; no code gen needed in KMP |
| Flutter service locator (get_it) | Koin single/viewModel DSL | Same DI concept; already fully configured |

---

## Open Questions

1. **PlayerInRecordDto: does it include the team field?**
   - What we know: Flutter's `PlayerDto` has an optional team (`t: TeamDto?`). TeamRecordDto uses `PlayerDto`.
   - What's unclear: Whether the API actually sends `t` in the records player payload. It may be null or absent.
   - Recommendation: Make the `t` field optional with `= null` default in `PlayerInRecordDto`. If unused, it parses silently.

2. **TeamRosterDto: exact response wrapper field name**
   - What we know: Flutter's `PlayerWithStatListDto` uses `data: List<PlayerWithStatDto>`.
   - What's unclear: Whether the KMP API returns the same `data` field name (consistent with player roster endpoint).
   - Recommendation: Use `data` as the field name (matches all other list DTOs in the codebase). If wrong, a JSON parse error will pinpoint it immediately.

3. **W-L display location: header vs. results tab only**
   - What we know: Flutter shows W-L inside the Results tab toolbar, not in the main header. CONTEXT.md says "below team name: league name, W-L record, and +/- point differential for selected season."
   - What's unclear: Whether the CONTEXT.md describes header content or was describing the Flutter Results tab layout by mistake.
   - Recommendation: Follow CONTEXT.md literally — show W-L in the header below team name (after results load). This is the locked decision. Show "-" placeholder until results are loaded.

---

## Validation Architecture

### Test Framework

| Property | Value |
|----------|-------|
| Framework | None detected — no test files or test config found in the project |
| Config file | None — Wave 0 gap |
| Quick run command | N/A |
| Full suite command | N/A |

### Phase Requirements → Test Map

| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| TEAM-01 | Team info header renders from live API data (name, logo, seasons) | manual-only | N/A | N/A |
| TEAM-02 | W-L record and point differential display for selected season | manual-only | N/A | N/A |
| TEAM-03 | TeamDetails screen shell with 3 tabs opens without crashing | manual-only | N/A | N/A |

**Manual-only justification:** No test infrastructure exists in the KMP project. All phases to date have relied on build compilation (`./gradlew build`) and manual run verification as the validation gate. Unit tests for DTO mapping or ViewModel logic could be added as Wave 0 gaps, but would require setting up a test framework (KotlinTest or JUnit4/5 for commonTest) first.

### Wave 0 Gaps

- [ ] No test framework configured — if Nyquist validation is required, add `kotlin.test` to `commonTest` dependencies and create a `commonTest` source set
- [ ] No test files exist for any domain model mapping or ViewModel logic

*(If no test infrastructure is desired: "None — project uses manual device testing as the validation gate. ./gradlew build is the automated gate.")*

---

## Sources

### Primary (HIGH confidence)

- Flutter source code (read directly from filesystem) — exact API paths, DTO shapes, field names, domain model structure, W-L computation logic, and `cat` parameter format
- Existing KMP source files (read directly from filesystem) — established patterns for service/repository/use case/ViewModel/DI/screen structure, all confirmed from Phases 1 and 2

### Secondary (MEDIUM confidence)

- Pattern inference from PlayerDetailsScreen → TeamDetailsScreen structural parity (all patterns verified in existing code)

### Tertiary (LOW confidence)

- Open questions about exact API response shapes for roster and records player field (verified from Flutter DTOs but not from live API)

---

## Metadata

**Confidence breakdown:**

- Standard stack: HIGH — all libraries already in use; no new dependencies
- Architecture: HIGH — every pattern is present and verified in the Phase 1 codebase
- API field mapping: HIGH — verified from Flutter DTO source files
- W-L computation: HIGH — verified from Flutter screen source (`_getWinsLost`, `_getPlusMinus`)
- `cat` parameter: HIGH — verified from Flutter `TeamRecordCategoryMapper`
- Open question items: LOW — need live API verification for PlayerInRecordDto team field

**Research date:** 2026-03-17
**Valid until:** 2026-06-17 (stable KMP + Flutter API — not fast-moving)
