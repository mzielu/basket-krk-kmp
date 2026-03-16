# Phase 1: Player Data Layer - Research

**Researched:** 2026-03-16
**Domain:** Kotlin Multiplatform / Compose Multiplatform — data layer (DTOs, service, repository, use cases, DI) + screen shell with 3-tab navigation
**Confidence:** HIGH — all findings are verified directly from the existing KMP codebase and the Flutter source being migrated

---

<user_constraints>
## User Constraints (from CONTEXT.md)

### Locked Decisions
- Fetch tab data on tab selection, not all at once on screen open (matches Flutter behavior)
- Cache tab data once loaded — data persists until user leaves PlayerDetails screen entirely
- No refetch on tab switch-back within the same screen session
- Match Flutter exactly for player header: player name, current team name with logo, list of seasons played
- Team logo loaded via Coil (already in project for other screens)
- Seasons sorted descending (most recent first) — same as Flutter's GetPlayerDetailsUseCase
- Always default to the player's most recent season
- No context-aware season selection from navigation source (keep it simple, match Flutter)
- 1:1 migration from Flutter — match the same API contract and behavior

### Claude's Discretion
- Exact header layout spacing and typography
- How to display seasons list (chips, dropdown, or inline text)
- Loading indicator style per tab (spinner vs skeleton)
- Error state design per tab

### Deferred Ideas (OUT OF SCOPE)
None — discussion stayed within phase scope
</user_constraints>

---

<phase_requirements>
## Phase Requirements

| ID | Description | Research Support |
|----|-------------|-----------------|
| PLYR-01 | User can view player info header showing name, current team, and list of seasons played | PlayerDetailsDto (fn, ln, t, seasons), GetPlayerDetails use case with season sort, BasketKrkImage for team logo |
| PLYR-02 | User can navigate between 3 tabs: Game Logs, Stats, Records | PrimaryTabRow pattern from MatchDetailsScreen, per-tab ViewStateData loading state, on-demand fetch in ViewModel |
</phase_requirements>

---

## Summary

This phase builds the full data pipeline for player details: 4 new Ktor service endpoints, 4 new DTOs with `toDomain()` mappings, a `PlayerService` domain interface, `PlayerRepository` interface + `PlayerRepositoryImpl`, 4 use cases, Koin DI wiring, and a `PlayerDetailsScreen` shell with 3 lazily-loaded tabs.

The project already has every structural pattern needed — `NetworkMatchService`, `MatchRepositoryImpl`, `GetMatchDetailsUseCase`, and `MatchDetailsViewModel` are exact templates to follow. `StatDto`/`SeasonDto`/`LeagueDto` and their `toDomain()` functions already exist and are fully reusable. The only new domain models required are `PlayerDetails`, `PlayerLogByTeam`, `PlayerLog`, `PlayerStat`, `PlayerRecord`, and `PlayerRecordType` — and a new `TeamDto` / `Team` pair (no `Team` domain model exists yet in KMP).

The Flutter source files provide exact API field names (abbreviated: `fn`, `ln`, `t`, `s`, `lg`, etc.) and the exact mapper logic (especially `PlayerRecordsDtoMapper` — the records endpoint returns a flat DTO of slash-delimited strings that must be split into a `List<PlayerRecord>`).

**Primary recommendation:** Follow the `NetworkMatchService` → `MatchRepositoryImpl` → `GetMatchDetailsUseCase` → `MatchDetailsViewModel` chain exactly, replacing Match with Player concepts. Reuse all existing DTOs (`StatDto`, `SeasonDto`, `LeagueDto`) and domain models (`Stat`, `Season`, `League`). Create `Team` domain model and `TeamDto` as new shared building blocks.

---

## Standard Stack

### Core (already in project — no new dependencies)
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| kotlinx.serialization | existing | DTO deserialization via `@Serializable` | Used on all existing DTOs |
| Ktor (via `ApiService`) | existing | HTTP GET via `apiService.get<Dto>(path)` | All existing network services use this |
| Arrow Either | existing | `Either<Failure, T>` + `Either.catchWithError` | All services and repositories use this |
| Koin | existing | DI registration in `dataModule` / `presentationModule` | All existing modules use this |
| Coil3 | existing | `BasketKrkImage` composable wrapping `AsyncImage` | Already used for team/player logos |
| Compose Navigation | existing | `@Serializable Screen` sealed class + `composable<>` in `App.kt` | Pattern established by `Screen.MatchDetails` |
| androidx.lifecycle ViewModel | existing | `ViewModel` base class + `viewModelScope` | All ViewModels extend this |
| Compose Material3 | existing | `PrimaryTabRow`, `Tab`, `Scaffold`, `HorizontalDivider` | Used in `MatchDetailsScreen` |

### No New Dependencies Required
All libraries needed for this phase are already declared in the project. No `build.gradle` changes required.

---

## Architecture Patterns

### Recommended Project Structure — New Files

```
data/src/commonMain/kotlin/com/mzs/basket_krk/data/
├── dto/
│   ├── TeamDto.kt                  # NEW — shared team DTO (reused by player + team phases)
│   ├── PlayerDetailsDto.kt         # NEW
│   ├── PlayerLogListDto.kt         # NEW — wraps List<PlayerLogByTeamDto>
│   ├── PlayerLogByTeamDto.kt       # NEW
│   ├── PlayerLogDto.kt             # NEW — individual game log entry
│   ├── PlayerStatListDto.kt        # NEW — wraps List<PlayerStatDto>
│   ├── PlayerStatDto.kt            # NEW
│   └── PlayerRecordsDto.kt         # NEW — flat slash-delimited record strings
├── service/
│   └── NetworkPlayerService.kt     # NEW
└── repository/
    └── PlayerRepositoryImpl.kt     # NEW

domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/
├── model/
│   ├── Team.kt                     # NEW — id, name, logoUrl (no shortName needed for player header)
│   ├── PlayerDetails.kt            # NEW — id, firstName, lastName, seasons, team?
│   ├── PlayerLogList.kt            # NEW — wraps List<PlayerLogByTeam>
│   ├── PlayerLogByTeam.kt          # NEW — team + List<PlayerLog>
│   ├── PlayerLog.kt                # NEW — id, opponent, pts, stat, type, date
│   ├── PlayerStat.kt               # NEW — season, team, league, stat
│   ├── PlayerRecord.kt             # NEW — recordType, value, times, matchId, date
│   └── PlayerRecordType.kt         # NEW — enum: PTS, REB, AST, STL, BLK, EFF, FGM, FGA, FG3M, FG3A, FTM, FTA
├── service/
│   └── PlayerService.kt            # NEW — interface
└── repository/
    └── PlayerRepository.kt         # NEW — interface
    usecase/
    ├── GetPlayerDetailsUseCase.kt  # NEW
    ├── GetPlayerGameLogsUseCase.kt # NEW
    ├── GetPlayerStatsUseCase.kt    # NEW
    └── GetPlayerRecordsUseCase.kt  # NEW

presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/
├── navigation/
│   └── Screen.kt                   # MODIFY — add PlayerDetails(playerId: Int)
├── App.kt                          # MODIFY — add composable<Screen.PlayerDetails>
├── di/
│   └── PresentationModule.kt       # MODIFY — add use cases + PlayerDetailsViewModel
└── screens/
    └── playerdetails/
        ├── PlayerDetailsViewModel.kt
        └── PlayerDetailsScreen.kt

data/src/commonMain/kotlin/.../data/di/
└── DataModule.kt                   # MODIFY — add PlayerService + PlayerRepository
```

### Pattern 1: DTO with toDomain() Extension
**What:** `@Serializable data class` in `data/dto/`, with a top-level `fun XxxDto.toDomain(): DomainModel` in the same file.
**When to use:** Every API response object.
**Example:**
```kotlin
// Source: data/src/commonMain/kotlin/.../data/dto/StatDto.kt (existing)
@Serializable
data class PlayerDetailsDto(
    val id: Int,
    val fn: String,       // firstName — abbreviated API field
    val ln: String,       // lastName — abbreviated API field
    val seasons: List<SeasonDto>,
    val t: TeamDto? = null  // current team — nullable
)

fun PlayerDetailsDto.toDomain() = PlayerDetails(
    id = id,
    firstName = fn,
    lastName = ln,
    seasons = seasons.map { it.toDomain() },
    team = t?.toDomain()
)
```

### Pattern 2: Network Service
**What:** Class implementing domain service interface, using `Either.catchWithError { apiService.get<Dto>(path).toDomain() }`.
**When to use:** One method per API endpoint.
**Example:**
```kotlin
// Source: data/src/commonMain/kotlin/.../data/service/NetworkMatchService.kt (existing)
class NetworkPlayerService(private val apiService: ApiService) : PlayerService {
    override suspend fun getPlayerDetails(playerId: Int): Either<Failure, PlayerDetails> {
        return Either.catchWithError {
            apiService.get<PlayerDetailsDto>("/player/$playerId/").toDomain()
        }
    }

    override suspend fun getPlayerGameLogs(playerId: Int, seasonId: Int): Either<Failure, PlayerLogList> {
        return Either.catchWithError {
            apiService.get<PlayerLogListDto>("/player/$playerId/logs?season_id=$seasonId").toDomain()
        }
    }

    override suspend fun getPlayerStats(playerId: Int): Either<Failure, List<PlayerStat>> {
        return Either.catchWithError {
            apiService.get<PlayerStatListDto>("/player/$playerId/stats/").toDomain()
        }
    }

    override suspend fun getPlayerRecords(playerId: Int): Either<Failure, List<PlayerRecord>> {
        return Either.catchWithError {
            apiService.get<PlayerRecordsDto>("/player/$playerId/records/").toDomain()
        }
    }
}
```

### Pattern 3: Repository with business logic transforms
**What:** `XxxRepositoryImpl` delegates to service and applies domain transforms (e.g., season sorting).
**When to use:** Season sort belongs here (or in use case — see Flutter source: it's in the use case).
**Season sort lives in the use case, matching Flutter's `GetPlayerDetailsUseCase`:**
```kotlin
// Source: flutter/basket_krk/.../get_player_details_usecase.dart (Flutter reference)
class GetPlayerDetailsUseCase(private val playerRepository: PlayerRepository) : GetPlayerDetails {
    override suspend fun invoke(input: Input): Either<Failure, PlayerDetails> {
        return playerRepository.getPlayerDetails(input.playerId).map { details ->
            details.copy(seasons = details.seasons.sortedByDescending { it.num })
        }
    }
    data class Input(val playerId: Int)
}
```

### Pattern 4: ViewModel with per-tab ViewStateData
**What:** One `MutableStateFlow<PlayerDetailsViewState>` with separate `ViewStateData<T?>` per concern. Tab data fetched on demand and cached (null means "not yet fetched").
**When to use:** Multi-tab screens with independent lazy loading.
**Example:**
```kotlin
// Source: derived from MatchDetailsViewModel.kt (existing) + Flutter PlayerDetailsBloc
@Immutable
data class PlayerDetailsViewState(
    val playerDetails: ViewStateData<PlayerDetails?> = ViewStateData(null),
    val gameLogs: ViewStateData<PlayerLogList?> = ViewStateData(null),
    val stats: ViewStateData<List<PlayerStat>?> = ViewStateData(null),
    val records: ViewStateData<List<PlayerRecord>?> = ViewStateData(null),
    val selectedSeason: Season? = null,
)
```
Cache check before fetch (replicates Flutter's `shouldFetchLogs` logic):
```kotlin
fun fetchGameLogsIfNeeded(seasonId: Int) {
    val current = _viewState.value.gameLogs
    if (current.data != null && !current.isError) return  // already loaded, skip
    viewModelScope.launch {
        _viewState.update { it.copy(gameLogs = it.gameLogs.loading()) }
        getPlayerGameLogs(GetPlayerGameLogsUseCase.Input(playerId, seasonId))
            .onSuspendSuccess { logs ->
                _viewState.update { it.copy(gameLogs = it.gameLogs.data(logs)) }
            }
            .onSuspendGeneralError { error ->
                _viewState.update { it.copy(gameLogs = it.gameLogs.error(error)) }
            }
    }
}
```

### Pattern 5: Screen shell with PrimaryTabRow
**What:** Compose screen collecting viewState via `collectAsState()`, passing lambdas down to content. Tab state is local `remember { mutableStateOf(0) }`. Tab selection triggers ViewModel fetch call.
**When to use:** 3-tab PlayerDetails screen.
**Key:** Use `LaunchedEffect(selectedTab)` or direct onClick to trigger ViewModel `onTabSelected(index)`.
```kotlin
// Source: MatchDetailsScreen.kt (existing) — tab pattern to follow
var selectedTabIndex by remember { mutableStateOf(0) }
// In Tab onClick:
onClick = {
    selectedTabIndex = i
    viewModel.onTabSelected(i)
}
```

### Pattern 6: PlayerRecordsDto — Flat slash-delimited strings
**What:** The records endpoint returns a flat DTO where each field is a string like `"42/3/1234/2024-01-15"` (value/times/matchId/date). Must be split in `toDomain()`.
**Why critical:** This is the biggest migration gotcha — it looks like a simple DTO but requires non-trivial parsing logic.
**Example:**
```kotlin
// Source: flutter/basket_krk/.../player_records_dto_mapper.dart (Flutter reference)
@Serializable
data class PlayerRecordsDto(
    val m1: String,   // FTM record: "value/times/matchId/date"
    val a1: String,   // FTA
    val fgm: String,  // FGM
    val fga: String,  // FGA
    val m3: String,   // FG3M
    val a3: String,   // FG3A
    val pt: String,   // PTS
    val a: String,    // AST
    val r: String,    // REB
    val b: String,    // BLK
    val s: String,    // STL
    val eff: String   // EFF
)

fun PlayerRecordsDto.toDomain(): List<PlayerRecord> {
    val raw = listOf(
        PlayerRecordType.PTS to pt,
        PlayerRecordType.REB to r,
        PlayerRecordType.AST to a,
        PlayerRecordType.STL to s,
        PlayerRecordType.BLK to b,
        PlayerRecordType.EFF to eff,
        PlayerRecordType.FGM to fgm,
        PlayerRecordType.FGA to fga,
        PlayerRecordType.FG3M to m3,
        PlayerRecordType.FG3A to a3,
        PlayerRecordType.FTM to m1,
        PlayerRecordType.FTA to a1
    )
    return raw.mapNotNull { (type, rawValue) ->
        val parts = rawValue.split("/")
        val value = parts[0].toIntOrNull() ?: 0
        if (value <= 0) null   // Flutter filters out zero-value records
        else PlayerRecord(
            recordType = type,
            value = value,
            times = parts[1].toInt(),
            matchId = parts[2].toInt(),
            date = parts[3]
        )
    }
}
```

### Pattern 7: Koin viewModel with parametersOf
**What:** ViewModels needing constructor params (like `playerId`) use `viewModel { (id: Int) -> Vm(id, get()) }` in module and `koinInject(parameters = { parametersOf(args.playerId) })` in App.kt.
**When to use:** PlayerDetailsViewModel needs `playerId` from navigation args.
**Example:**
```kotlin
// Source: PresentationModule.kt + App.kt (existing — MatchDetailsViewModel pattern)
// In PresentationModule.kt:
viewModel { (playerId: Int) -> PlayerDetailsViewModel(playerId, get(), get(), get(), get()) }
// In App.kt:
val viewModel: PlayerDetailsViewModel = koinInject(parameters = { parametersOf(args.playerId) })
```

### Anti-Patterns to Avoid

- **Fetching all 4 endpoints on screen open:** Flutter fetches main info first, then each tab data on first tab entry. Replicate this.
- **Putting season sort in the repository:** Flutter puts it in `GetPlayerDetailsUseCase`. Follow Flutter.
- **Re-fetching on tab switch-back:** Check `current.data != null && !current.isError` before launching fetch.
- **Creating a new `TeamDto`/`Team` pair that duplicates `SearchItem.Team`:** The `Team` domain model needed here (`id`, `name`, `logoUrl`) is richer than `SearchItem.Team` — create a standalone `Team` data class in domain. This will be shared with Phase 3 (TeamDetails).
- **Using `SerialName` annotation unnecessarily:** Existing DTOs use field names that match API exactly (e.g., `fn`, `ln`, `t`). Do the same — keep abbreviated names in the DTO, use descriptive names only in domain model.

---

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| HTTP deserialization | Custom JSON parser | `apiService.get<Dto>(path)` with `@Serializable` DTO | Already handles all Ktor/kotlinx-serialization plumbing |
| Error wrapping | Custom try/catch | `Either.catchWithError { }` | Converts any exception to `Failure.UnknownError`, handles `Failure` subclasses correctly |
| Loading/error/data state | Custom state sealed class | `ViewStateData<T>` | Already has `loading()`, `data()`, `error()`, `isLoading`, `isError` — tested across all screens |
| Network image loading | Coil setup from scratch | `BasketKrkImage(logoUrl, contentDescription, modifier)` | Already handles base URL prepending (`https://www.basketkrk.pl/`) and crossfade |
| Tab UI | Custom tab implementation | `PrimaryTabRow` + `Tab` (Material3) | Established pattern in `MatchDetailsScreen` |

**Key insight:** The entire plumbing stack exists. This phase is about correctly wiring new player-specific types into existing infrastructure, not building infrastructure.

---

## Common Pitfalls

### Pitfall 1: PlayerRecordsDto is a flat string-split DTO, not a typed list
**What goes wrong:** Treating `PlayerRecordsDto` as straightforward typed fields. Each field is a `String` like `"42/3/1234/2024-01-15"` representing `value/times/matchId/date`.
**Why it happens:** The DTO name sounds like it contains records, but the API encodes all 12 record types as slash-delimited strings in one flat object.
**How to avoid:** Implement the `toDomain()` split logic (see Pattern 6 above) and filter out zero-value records as Flutter does.
**Warning signs:** If records display empty or crash with `NumberFormatException` — check split logic and index assumptions.

### Pitfall 2: Tab data uses `gameLogsSelectedSeason` (from playerDetails) as the season ID
**What goes wrong:** Attempting to fetch game logs before `playerDetails` is loaded (no season ID available yet).
**Why it happens:** Game logs require `season_id` parameter. The season list comes from the `getPlayerDetails` response. Tabs are shown only after `playerDetails` loads successfully (Flutter shows full-screen loader until then).
**How to avoid:** Only trigger tab fetches after `playerDetails` state transitions to `.data()` with a non-null result. Default to `seasons.first()` (index 0 after descending sort = most recent).
**Warning signs:** `NullPointerException` or empty season ID on game logs fetch.

### Pitfall 3: Missing `Team` domain model — `SearchItem.Team` is not a substitute
**What goes wrong:** Using `SearchItem.Team` (has `logoPath: String?`) for the player header's current team, or trying to reuse `MatchDetailsTeam` (has too many fields).
**Why it happens:** There is no standalone `Team` data class in the KMP domain yet.
**How to avoid:** Create `Team(id: Int, name: String, logoUrl: String)` in the domain module. This will also be used by Phase 3 (TeamDetails).
**Warning signs:** Compiler errors when mapping `TeamDto.toDomain()` to existing types.

### Pitfall 4: `PlayerStatDto.s` is season number (not season ID), `PlayerStatDto.t` is team
**What goes wrong:** Confusing the field names. In `PlayerStatDto`: `s` = season number (Int), `t` = TeamDto, `lg` = LeagueDto, `stat` = StatDto.
**Why it happens:** Abbreviated field names collide with stat field names from `StatDto` (which also has `s` = steals).
**How to avoid:** Read Flutter's `player_stat_dto.dart` carefully. In `PlayerStatDto`: `s` means season, not steals.
**Warning signs:** Seasons showing wrong numbers or stat data mixed up with season data.

### Pitfall 5: Navigation wiring requires both Screen.kt and App.kt changes
**What goes wrong:** Adding `Screen.PlayerDetails` but forgetting to add the `composable<Screen.PlayerDetails>` block in `App.kt`, or vice versa.
**Why it happens:** Navigation is split across two files. `App.kt` already has TODO comments for `openPlayerDetails`.
**How to avoid:** In `App.kt`, replace the `openPlayerDetails = { // TODO }` lambda with `navController.navigate(Screen.PlayerDetails(playerId = it))` and add the `composable<Screen.PlayerDetails>` block.

### Pitfall 6: `PlayerLogListDto` and `PlayerStatListDto` wrap the data in a `data` field
**What goes wrong:** Expecting the API to return a bare JSON array. Both `/logs` and `/stats/` endpoints return `{ "data": [...] }` (following Flutter's `PlayerLogListDto.data` and `PlayerStatListDto.data`).
**How to avoid:** Use a list-wrapper DTO: `@Serializable data class PlayerLogListDto(val data: List<PlayerLogByTeamDto>)`.

---

## Code Examples

### Full API endpoint list (verified from Flutter datasource)
```
GET /player/{id}/                            → PlayerDetailsDto
GET /player/{id}/logs?season_id={seasonId}   → PlayerLogListDto
GET /player/{id}/stats/                      → PlayerStatListDto
GET /player/{id}/records/                    → PlayerRecordsDto
```

### TeamDto (new — needed for PlayerDetailsDto and PlayerLogByTeamDto)
```kotlin
// Source: flutter/basket_krk/.../team_dto.dart (migrated)
@Serializable
data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String
)

fun TeamDto.toDomain() = Team(id = id, name = name, logoUrl = logo)
```

### PlayerLogByTeamDto (nested DTO structure)
```kotlin
// Source: flutter/basket_krk/.../player_log_by_team_dto.dart (migrated)
@Serializable
data class PlayerLogByTeamDto(
    val t: TeamDto,              // team
    val logs: List<PlayerLogDto>
)

@Serializable
data class PlayerLogDto(
    val id: Int,
    val opp: MatchTeamDto,       // opponent — reuse existing MatchTeamDto
    val pts: Int,
    val stat: StatDto,           // reuse existing StatDto
    val type: String,
    val date: String
)
```

### PlayerStatDto
```kotlin
// Source: flutter/basket_krk/.../player_stat_dto.dart (migrated)
@Serializable
data class PlayerStatDto(
    val s: Int,         // season number (NOT steals — that's in StatDto)
    val t: TeamDto,     // team
    val lg: LeagueDto,  // reuse existing LeagueDto
    val stat: StatDto   // reuse existing StatDto
)
```

### Koin DI registration (pattern from existing modules)
```kotlin
// DataModule.kt additions:
single<PlayerService> { NetworkPlayerService(get()) }
single<PlayerRepository> { PlayerRepositoryImpl(get()) }

// PresentationModule.kt additions:
single<GetPlayerDetails> { GetPlayerDetailsUseCase(get()) }
single<GetPlayerGameLogs> { GetPlayerGameLogsUseCase(get()) }
single<GetPlayerStats> { GetPlayerStatsUseCase(get()) }
single<GetPlayerRecords> { GetPlayerRecordsUseCase(get()) }
viewModel { (playerId: Int) -> PlayerDetailsViewModel(playerId, get(), get(), get(), get()) }
```

### Screen.kt addition
```kotlin
// Source: Screen.kt (existing pattern — Screen.MatchDetails)
@Serializable
data class PlayerDetails(val playerId: Int) : Screen()
```

### Tab trigger pattern in ViewModel
```kotlin
// Source: derived from Flutter PlayerDetailsBloc + existing MatchDetailsViewModel
fun onTabSelected(index: Int) {
    val season = _viewState.value.playerDetails.data?.seasons?.firstOrNull() ?: return
    when (index) {
        0 -> fetchGameLogsIfNeeded(season.id)
        1 -> fetchStatsIfNeeded()
        2 -> fetchRecordsIfNeeded()
    }
}
```

---

## State of the Art

| Old Approach | Current Approach | Notes |
|---|---|---|
| Flutter BLoC events/states | KMP ViewModel + StateFlow | Direct equivalent — BLoC events become ViewModel functions, BLoC states become `ViewStateData` fields |
| Flutter `Either<Failure, T>` (dartz) | KMP `Either<Failure, T>` (arrow-kt) | Same pattern, different library — `fold`, `map`, `mapLeft` work identically |
| Flutter `freeze` code generation for DTOs | KMP `@Serializable` data classes | Simpler — no code generation needed, kotlinx.serialization handles all JSON |

---

## Open Questions

1. **Does `PlayerLogDto.opp` use `MatchTeamDto` (with `s_name`) or a simpler team DTO?**
   - What we know: Flutter's `player_log_dto.dart` uses `MatchTeamDto` (id, name, s_name, logo, pts)
   - What's unclear: The KMP `MatchTeamDto` already exists. Use it directly or create a simpler opponent DTO?
   - Recommendation: Reuse `MatchTeamDto` directly — it already has `toDomain()` and all fields. `PlayerLog.opponent` domain model can be `MatchTeam`.

2. **Should `Team` domain model go in `domain/model/` (shared) or be scoped to player?**
   - What we know: Phase 3 (TeamDetails) will need the same `Team` model.
   - Recommendation: Create `Team(id, name, logoUrl)` as a shared domain model now. Add it to `domain/model/Team.kt`.

---

## Validation Architecture

### Test Framework
| Property | Value |
|----------|-------|
| Framework | None detected in KMP project |
| Config file | None — Wave 0 gap |
| Quick run command | N/A until framework installed |
| Full suite command | N/A until framework installed |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| PLYR-01 | PlayerDetails header renders name, team, seasons count from live data | Smoke (manual) | Manual — requires device/emulator | N/A |
| PLYR-01 | Seasons sorted descending in use case | Unit | `./gradlew :domain:test` (once framework added) | Wave 0 gap |
| PLYR-01 | PlayerRecordsDto slash-split parsing | Unit | `./gradlew :data:test` (once framework added) | Wave 0 gap |
| PLYR-02 | Three tabs open without crash given player ID | Smoke (manual) | Manual — requires device/emulator | N/A |
| PLYR-02 | Each tab shows loading state on first entry | Smoke (manual) | Manual — requires device/emulator | N/A |
| PLYR-02 | No re-fetch on tab switch-back | Unit (ViewModel) | `./gradlew :presentation:test` (once framework added) | Wave 0 gap |

### Sampling Rate
- **Per task commit:** Manual smoke test — open PlayerDetails, verify no crash, verify loading states appear
- **Per wave merge:** Full manual checklist: header renders, all 3 tabs load, season sort correct, records parse correctly
- **Phase gate:** All success criteria green before `/gsd:verify-work`

### Wave 0 Gaps
- No test framework detected in the KMP project. Unit tests for DTO parsing and ViewModel logic would require adding `kotlin.test` or JUnit to the relevant Gradle modules.
- Given no existing test infrastructure, validation for this phase is manual smoke testing on device/emulator.
- Recommendation: Accept manual validation for Phase 1. Defer test framework setup to a dedicated infrastructure task if needed.

---

## Sources

### Primary (HIGH confidence)
All findings are directly read from source files — no external lookups required.

- `data/src/commonMain/.../service/NetworkMatchService.kt` — Network service pattern (copy for PlayerService)
- `data/src/commonMain/.../dto/MatchDetailsTeamDto.kt` — DTO + toDomain() pattern
- `data/src/commonMain/.../dto/StatDto.kt` — Existing StatDto fully reusable (matches Flutter StatDto 1:1)
- `data/src/commonMain/.../dto/SeasonDto.kt` — Existing SeasonDto fully reusable
- `data/src/commonMain/.../dto/LeagueDto.kt` — Existing LeagueDto fully reusable
- `data/src/commonMain/.../repository/MatchRepositoryImpl.kt` — Repository implementation pattern
- `domain/src/commonMain/.../usecase/GetMatchDetailsUseCase.kt` — Use case pattern (interface + impl + Input)
- `domain/src/commonMain/.../base/UseCase.kt` — `SuspendInOutUseCase` interface definition
- `domain/src/commonMain/.../base/EitherExtensions.kt` — `Either.catchWithError`, `onSuspendSuccess`, `onSuspendGeneralError`
- `presentation/src/commonMain/.../base/ViewStateData.kt` — `ViewStateData` wrapper
- `presentation/src/commonMain/.../screens/matchdetails/MatchDetailsViewModel.kt` — ViewModel + ViewState pattern
- `presentation/src/commonMain/.../screens/matchdetails/MatchDetailsScreen.kt` — PrimaryTabRow + tab content pattern
- `presentation/src/commonMain/.../navigation/Screen.kt` — Navigation sealed class
- `presentation/src/commonMain/.../App.kt` — composable navigation + koinInject with parametersOf
- `data/src/commonMain/.../di/DataModule.kt` — Koin module pattern
- `presentation/src/commonMain/.../di/PresentationModule.kt` — Koin viewModel with params
- `presentation/src/commonMain/.../base/ui/BasketKrkImage.kt` — Coil image wrapper

- `flutter/.../player_remote_datasource.dart` — API endpoint paths and parameters (verified source of truth)
- `flutter/.../player_details_dto.dart` — Abbreviated field names: `fn`, `ln`, `t`, `seasons`
- `flutter/.../player_log_list_dto.dart` — Wrapper `data` field pattern
- `flutter/.../player_log_by_team_dto.dart` — `t` (team) + `logs` structure
- `flutter/.../player_log_dto.dart` — `opp` (MatchTeamDto), `stat`, `pts`, `type`, `date`
- `flutter/.../player_stat_list_dto.dart` — Wrapper `data` field pattern
- `flutter/.../player_stat_dto.dart` — `s` (season num), `t` (team), `lg` (league), `stat`
- `flutter/.../player_records_dto.dart` — Flat slash-delimited string fields
- `flutter/.../player_records_dto_mapper.dart` — **Critical** — exact split/parse logic for records
- `flutter/.../player_details_dto_mapper.dart` — `fn`→firstName, `ln`→lastName, `height: 12` (hardcoded, ignore)
- `flutter/.../player_details_bloc.dart` — Tab-on-demand fetch logic + cache check (`shouldFetchLogs`)
- `flutter/.../player_details_screen.dart` — Header layout, tab structure, loading states
- `flutter/.../get_player_details_usecase.dart` — Season sort: `sortedByDescending { it.num }`

### Secondary (MEDIUM confidence)
None required — all information sourced from project code directly.

---

## Metadata

**Confidence breakdown:**
- Standard stack: HIGH — verified from existing DTOs, services, modules
- Architecture: HIGH — exact patterns exist in MatchDetails implementation; Flutter source confirms API contract
- Pitfalls: HIGH — identified from direct comparison of Flutter source with KMP patterns; records DTO pitfall is confirmed by reading both Flutter mapper and DTO source

**Research date:** 2026-03-16
**Valid until:** 2026-06-16 (stable — no external dependencies; only invalidated by project refactors)
