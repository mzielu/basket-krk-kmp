---
phase: 03-team-data-layer
plan: 01
subsystem: api
tags: [kotlin, kmp, ktor, kotlinx-serialization, arrow-core, either, domain-model, dto, repository]

# Dependency graph
requires:
  - phase: 01-player-data-layer
    provides: PlayerShort, PlayerWithStat, MatchTeam, MatchStatus, MatchType, Season, League domain models and DTOs as reuse base
  - phase: 02-playerdetails-screen
    provides: PlayerDetails screen and use case patterns established

provides:
  - TeamDetails domain model (id, name, logoUrl, seasons, league?)
  - TeamResult domain model (id, opponent, points, date, status, type)
  - TeamResultList wrapper domain model
  - TeamRecord domain model with nullable ats/sNum/matchId fields
  - TeamRecordStatOption enum (9 stat categories with apiKey)
  - TeamRecordRange enum (ALL_TIME/SEASON/MATCH) with buildRecordCategory helper
  - TeamDetailsDto, TeamResultDto, TeamResultListDto, TeamRosterDto, TeamRecordDto, TeamRecordListDto with toDomain() mappers
  - TeamService interface (4 suspend methods) in domain/service
  - TeamRepository interface (4 suspend methods) in domain/repository
  - NetworkTeamService implementing 4 API endpoints with Either.catchWithError
  - TeamRepositoryImpl delegating all 4 methods to TeamService

affects: [03-team-data-layer Plan 02 (TeamDetailsScreen, ViewModel, use cases, DI)]

# Tech tracking
tech-stack:
  added: []
  patterns:
    - API -> DTO -> toDomain() -> Domain Model -> Repository pipeline
    - Either.catchWithError wrapping all API calls for type-safe error handling
    - Pure delegation pattern in RepositoryImpl (no transformation, delegates to Service)
    - PlayerInRecordDto uses fn/ln fields (separate first/last name) distinct from PlayerShortDto (combined name field)

key-files:
  created:
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamDetails.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamResult.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamResultList.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecord.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordStatOption.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/TeamRecordRange.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/service/TeamService.kt
    - domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/TeamRepository.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamDetailsDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamResultDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamResultListDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamRosterDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamRecordDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/TeamRecordListDto.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkTeamService.kt
    - data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/TeamRepositoryImpl.kt
  modified: []

key-decisions:
  - "TeamRecord.league in TeamDetails is nullable (last_league API field can be null)"
  - "PlayerInRecordDto uses fn/ln fields (full name parts) NOT PlayerShortDto — API sends separate first/last name for records endpoints"
  - "TeamDto imported from SearchResultDto.kt for optional t field in PlayerInRecordDto — avoids redeclaration conflict (same pattern as Phase 1 player data layer decision)"
  - "buildRecordCategory top-level function in TeamRecordRange.kt composes stat+range apiKey with underscore separator"

patterns-established:
  - "Team data pipeline mirrors Player data pipeline structure exactly (Service -> RepositoryImpl -> Repository interface)"
  - "DTOs with toDomain() mappers as extension functions in same file"
  - "Nullable DTO fields with default null for optional API response fields"

requirements-completed: [TEAM-01, TEAM-02, TEAM-03]

# Metrics
duration: 2min
completed: 2026-03-16
---

# Phase 3 Plan 1: Team Data Layer Summary

**Complete team API-to-domain pipeline: 6 domain models, 6 DTOs with toDomain() mappers, TeamService/TeamRepository interfaces, NetworkTeamService with 4 API endpoints, and TeamRepositoryImpl using Either.catchWithError**

## Performance

- **Duration:** 2 min
- **Started:** 2026-03-16T23:33:02Z
- **Completed:** 2026-03-16T23:34:32Z
- **Tasks:** 3
- **Files modified:** 16

## Accomplishments
- 6 domain models created: TeamDetails, TeamResult, TeamResultList, TeamRecord, TeamRecordStatOption (9 variants), TeamRecordRange (3 variants with buildRecordCategory helper)
- 6 DTOs with toDomain() mappers covering all 4 API endpoints, reusing existing SeasonDto, LeagueDto, MatchTeamDto, PlayerWithStatDto
- TeamService and TeamRepository interfaces defined in domain with 4 suspend methods each
- NetworkTeamService implements all 4 API endpoints (/team/{id}/, /results, /players, /records) using Either.catchWithError
- TeamRepositoryImpl delegates all 4 methods to TeamService (pure delegation, matching PlayerRepositoryImpl pattern)
- Full compilation verified: :data:compileCommonMainKotlinMetadata BUILD SUCCESSFUL

## Task Commits

Each task was committed atomically:

1. **Task 1: Create domain models and enums for team data** - `d815536` (feat)
2. **Task 2: Create DTOs with toDomain() mappers and TeamService/TeamRepository interfaces** - `8cc6b8d` (feat)
3. **Task 3: Create NetworkTeamService and TeamRepositoryImpl** - `65710c6` (feat)

**Plan metadata:** (docs commit follows)

## Files Created/Modified
- `domain/.../model/TeamDetails.kt` - TeamDetails data class (id, name, logoUrl, seasons, league?)
- `domain/.../model/TeamResult.kt` - TeamResult with MatchTeam opponent, MatchStatus/MatchType enums
- `domain/.../model/TeamResultList.kt` - TeamResultList wrapper (data, league)
- `domain/.../model/TeamRecord.kt` - TeamRecord (player, value, position, games, nullable ats/sNum/matchId)
- `domain/.../model/TeamRecordStatOption.kt` - Enum with 9 stat categories (PTS, AST, REB, STL, BLK, EFF, FT, FG, FG3)
- `domain/.../model/TeamRecordRange.kt` - Enum with 3 ranges + buildRecordCategory helper function
- `domain/.../service/TeamService.kt` - Interface with 4 suspend methods
- `domain/.../repository/TeamRepository.kt` - Interface mirroring TeamService
- `data/.../dto/TeamDetailsDto.kt` - Serializable DTO with last_league nullable, toDomain() mapper
- `data/.../dto/TeamResultDto.kt` - Serializable DTO using MatchTeamDto for opponent
- `data/.../dto/TeamResultListDto.kt` - Envelope DTO with lg field mapping to league
- `data/.../dto/TeamRosterDto.kt` - Envelope DTO wrapping List<PlayerWithStatDto>
- `data/.../dto/TeamRecordDto.kt` - DTO + PlayerInRecordDto (fn/ln format), toDomain() mapper
- `data/.../dto/TeamRecordListDto.kt` - Envelope DTO for records list
- `data/.../service/NetworkTeamService.kt` - Implements TeamService, 4 endpoint methods
- `data/.../repository/TeamRepositoryImpl.kt` - Pure delegation to TeamService

## Decisions Made
- TeamDetails.league is nullable — the API field `last_league` may be absent from the response
- PlayerInRecordDto uses fn/ln (separate first/last name parts) rather than PlayerShortDto — records API returns full player shape distinct from simple name format
- TeamDto from SearchResultDto.kt reused for the optional `t` field in PlayerInRecordDto — maintains the existing pattern from Phase 1 (avoids TeamDto redeclaration)
- buildRecordCategory placed as top-level function in TeamRecordRange.kt — composites stat apiKey and range apiKey with underscore (e.g., "pts_t" for PTS All-Time)

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

None.

## User Setup Required

None - no external service configuration required.

## Next Phase Readiness
- Complete team data pipeline ready: domain models, DTOs, service interface, repository interface, and implementations all compiled
- Plan 02 can proceed: TeamDetailsScreen, TeamDetailsViewModel, use cases (GetTeamDetails, GetTeamResults, GetTeamRoster, GetTeamRecords), and DI bindings
- TeamRepository and TeamService interfaces provide clean injection points for use cases

---
*Phase: 03-team-data-layer*
*Completed: 2026-03-16*
