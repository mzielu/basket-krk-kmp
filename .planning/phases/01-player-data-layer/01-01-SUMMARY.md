---
phase: 01-player-data-layer
plan: 01
subsystem: api
tags: [kotlin-multiplatform, ktor, kotlinx-serialization, arrow, either, domain-model, dto, repository]

# Dependency graph
requires: []
provides:
  - Team domain model (id, name, logoUrl)
  - PlayerDetails, PlayerLogList, PlayerLogByTeam, PlayerLog, PlayerStat, PlayerRecord, PlayerRecordType domain models
  - PlayerService interface (4 suspend methods returning Either<Failure, T>)
  - PlayerRepository interface mirroring PlayerService
  - PlayerDetailsDto, PlayerLogListDto, PlayerLogByTeamDto, PlayerLogDto, PlayerStatListDto, PlayerStatDto, PlayerRecordsDto with toDomain() mappers
  - NetworkPlayerService implements PlayerService with 4 API endpoints
  - PlayerRepositoryImpl implements PlayerRepository delegating to PlayerService
affects:
  - 01-02 (use cases consuming PlayerRepository)
  - 01-03 (ViewModel / UI consuming use cases)
  - Phase 3 (team data layer may reuse Team domain model)

# Tech tracking
tech-stack:
  added: []
  patterns:
    - Either.catchWithError wrapping all network calls
    - toDomain() extension functions on DTOs for domain mapping
    - toTeam() disambiguator when multiple toDomain() overloads would conflict
    - Repository-delegates-to-service pattern (no transforms at repository level)

key-files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Team.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerDetails.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerLogList.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerLogByTeam.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerLog.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerStat.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerRecord.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerRecordType.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/service/PlayerService.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/PlayerRepository.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerDetailsDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerLogListDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerLogByTeamDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerLogDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerStatListDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerStatDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerRecordsDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkPlayerService.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/PlayerRepositoryImpl.kt
  modified:
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/SearchResultDto.kt

key-decisions:
  - "TeamDto already exists in SearchResultDto.kt mapping to SearchItem.Team; added toTeam() extension to map to new Team domain model instead of creating a conflicting standalone TeamDto.kt"
  - "Team.logoUrl is non-nullable String; TeamDto.logo is nullable String? — toTeam() uses orEmpty() fallback for safety"
  - "PlayerStat.season is Int (season number), not Season object — matches Flutter PlayerStatDto.s field semantics"
  - "PlayerRecordsDto uses slash-delimited strings (value/times/matchId/date); zero-value records filtered out matching Flutter PlayerRecordsDtoMapper behavior"

patterns-established:
  - "toTeam() disambiguator: when toDomain() return type conflicts due to multiple domain targets, use descriptive mapper name"
  - "PlayerRecordsDto slash-split pattern: mapNotNull + split('/') + toIntOrNull() for safe parsing of slash-delimited record strings"

requirements-completed:
  - PLYR-01

# Metrics
duration: 18min
completed: 2026-03-16
---

# Phase 1 Plan 01: Player Data Layer Foundation Summary

**8 domain models + 7 DTOs + NetworkPlayerService + PlayerRepositoryImpl establishing complete player data pipeline with Either<Failure, T> error handling**

## Performance

- **Duration:** ~18 min
- **Started:** 2026-03-16T16:00:00Z
- **Completed:** 2026-03-16T16:18:00Z
- **Tasks:** 3
- **Files modified:** 20 (19 created, 1 modified)

## Accomplishments
- 8 domain models: Team, PlayerDetails, PlayerLogList, PlayerLogByTeam, PlayerLog, PlayerStat, PlayerRecord, PlayerRecordType
- 7 DTOs with toDomain()/toTeam() mappers using abbreviated API field names matching Flutter reference
- NetworkPlayerService implements all 4 API endpoints with Either.catchWithError pattern
- PlayerRepositoryImpl cleanly delegates to service with no transforms

## Task Commits

Each task was committed atomically:

1. **Task 1: Create domain models and interfaces** - `370ab40` (feat)
2. **Task 2: Create DTOs with toDomain() mappers** - `23d1947` (feat)
3. **Task 3: Create NetworkPlayerService and PlayerRepositoryImpl** - `e48a055` (feat)

## Files Created/Modified
- `domain/.../model/Team.kt` - Shared team domain model (id, name, logoUrl)
- `domain/.../model/PlayerDetails.kt` - Player header with seasons and optional current team
- `domain/.../model/PlayerLogList.kt` - Wrapper for grouped game logs by team
- `domain/.../model/PlayerLogByTeam.kt` - Team grouping containing list of PlayerLog
- `domain/.../model/PlayerLog.kt` - Individual game log using existing MatchTeam and Stat
- `domain/.../model/PlayerStat.kt` - Aggregated per-season/team/league stat using existing League and Stat
- `domain/.../model/PlayerRecord.kt` - Single record (best performance) entry
- `domain/.../model/PlayerRecordType.kt` - Enum of 12 record categories
- `domain/.../service/PlayerService.kt` - Domain interface with 4 suspend methods
- `domain/.../repository/PlayerRepository.kt` - Repository interface mirroring service
- `data/.../dto/PlayerDetailsDto.kt` - Abbreviated fields (fn, ln, t) with toDomain()
- `data/.../dto/PlayerLogListDto.kt` - Wraps data array, maps to PlayerLogList
- `data/.../dto/PlayerLogByTeamDto.kt` - Uses TeamDto.toTeam() for team mapping
- `data/.../dto/PlayerLogDto.kt` - Reuses existing MatchTeamDto (opp) and StatDto
- `data/.../dto/PlayerStatListDto.kt` - Wraps data array, maps to List<PlayerStat>
- `data/.../dto/PlayerStatDto.kt` - s=season number, t=team, lg=league, stat=stats
- `data/.../dto/PlayerRecordsDto.kt` - Slash-delimited strings, filters zero-value records
- `data/.../service/NetworkPlayerService.kt` - 4 API calls with correct paths and catchWithError
- `data/.../repository/PlayerRepositoryImpl.kt` - Delegates all 4 methods to service
- `data/.../dto/SearchResultDto.kt` - Added toTeam() extension to map TeamDto to Team domain model

## Decisions Made
- Used `toTeam()` extension instead of a standalone `TeamDto.kt` because `TeamDto` already existed in `SearchResultDto.kt` mapping to `SearchItem.Team`. Creating a duplicate would cause "Redeclaration" and "Conflicting overloads" compilation errors.
- `Team.logoUrl` is non-nullable but `TeamDto.logo` is nullable — used `orEmpty()` as safe fallback since logo is a URL string and empty string is a safe default.
- `PlayerStat.season` is `Int` (season number) not a `Season` object — matches the Flutter `PlayerStatDto.s` field which is an integer representing the season number.

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Resolved TeamDto redeclaration conflict**
- **Found during:** Task 2 (Create DTOs with toDomain() mappers)
- **Issue:** Plan specified creating a standalone `TeamDto.kt` but `TeamDto` was already declared in `SearchResultDto.kt` with `toDomain()` returning `SearchItem.Team`. Creating a duplicate caused Redeclaration and Conflicting overloads compilation errors.
- **Fix:** Deleted standalone `TeamDto.kt`; added `toTeam()` extension function to `SearchResultDto.kt` that maps `TeamDto` to the new `Team` domain model. Updated all new player DTOs to call `toTeam()` instead of `toDomain()`.
- **Files modified:** `SearchResultDto.kt` (added import + toTeam()), `PlayerDetailsDto.kt`, `PlayerLogByTeamDto.kt`, `PlayerStatDto.kt`
- **Verification:** `./gradlew :data:compileCommonMainKotlinMetadata` passes successfully
- **Committed in:** `23d1947` (Task 2 commit)

---

**Total deviations:** 1 auto-fixed (Rule 1 - naming conflict / compilation error)
**Impact on plan:** Auto-fix necessary for compilation correctness. Functionally equivalent to plan intent. No scope creep.

## Issues Encountered
- `TeamDto` already existed in `SearchResultDto.kt` — discovered during first compile attempt. Fixed by using `toTeam()` disambiguating mapper name.

## User Setup Required
None - no external service configuration required.

## Next Phase Readiness
- All domain models, DTOs, service and repository interfaces are in place
- Ready for Plan 02: use cases that consume PlayerRepository
- NetworkPlayerService and PlayerRepositoryImpl ready for DI registration

---
*Phase: 01-player-data-layer*
*Completed: 2026-03-16*

## Self-Check: PASSED

All 20 expected files present. All 3 task commits verified (370ab40, 23d1947, e48a055).
