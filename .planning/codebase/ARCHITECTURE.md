# Architecture

**Analysis Date:** 2026-03-16

## Pattern Overview

**Overall:** Clean Architecture with strict layering - Domain/Data/Presentation separation combined with Kotlin Multiplatform (KMP) for cross-platform support.

**Key Characteristics:**
- Repository pattern for data access abstraction
- Use case pattern with functional composition using Arrow's Either
- ViewModel-based UI state management with MVI/MVVM pattern
- Dependency injection via Koin
- Multiplatform support (Android/iOS) with platform-specific implementations
- Error handling via Arrow's Either monadic type

## Layers

**Domain Layer:**
- Purpose: Defines business logic, models, and contracts independent of any framework
- Location: `/domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/`
- Contains: Models, repositories (interfaces), services (interfaces), use cases
- Depends on: Arrow library only
- Used by: Data layer (implements contracts) and Presentation layer (uses use cases)
- Key files:
  - `model/` - Domain entities (League, Match, Season, Player stats, etc.)
  - `repository/` - Repository interfaces (LeagueRepository, MatchRepository, SeasonRepository, SearchRepository)
  - `service/` - Service interfaces (LeagueService, MatchService, SeasonService, SearchService)
  - `usecase/` - Use case implementations that orchestrate repository calls
  - `base/` - Abstract base classes for use cases and error handling utilities

**Data Layer:**
- Purpose: Implements data access, API communication, and data transformation
- Location: `/data/src/commonMain/kotlin/com/mzs/basket_krk/data/`
- Contains: DTO classes, API service, repository implementations, network services
- Depends on: Domain layer (implements contracts), Ktor for HTTP, Arrow for error handling
- Used by: Presentation layer through repositories
- Platform-specific: `androidMain/` and `iosMain/` for platform-specific HTTP client setup
- Key files:
  - `dto/` - Data Transfer Objects (MatchDto, LeagueDto, PlayerWithStatDto, etc.)
  - `repository/` - Repository implementations wrapping services
  - `service/ApiService.kt` - Low-level HTTP client wrapper (Ktor-based)
  - `service/Network*Service.kt` - Network service implementations (LeagueService, MatchService, etc.)
  - `serializer/` - Custom serialization/deserialization logic
  - `di/DataModule.kt` - Koin DI configuration for data layer

**Presentation Layer:**
- Purpose: UI components, state management, and navigation
- Location: `/presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/`
- Contains: ViewModels, Screens (Compose), navigation, UI components
- Depends on: Domain layer (use cases), Jetpack/Compose, Koin for DI
- Used by: ComposeApp module (entry point)
- Platform-specific: `iosMain/` for platform-specific UI (ViewControllers)
- Key files:
  - `screens/main/` - Main tab screens (Matches, Search, Statistics, More)
  - `screens/matchdetails/` - Match details screen with player stats
  - `screens/main/statistics/standings/` - League standings/competitions
  - `screens/main/statistics/alltimeleaders/` - All-time player leaders with pagination
  - `navigation/` - Navigation graph and route definitions
  - `di/PresentationModule.kt` - Koin configuration for ViewModels and use cases
  - `base/` - Base components, UI utilities, and pagination source base

**ComposeApp (Entry Point):**
- Purpose: Platform-specific entry point that initializes Koin and renders the UI
- Location: `/composeApp/src/`
- Contains: MainActivity (Android), Application class
- Platform-specific:
  - `androidMain/MainActivity.kt` - Android Activity entry point
  - `androidMain/BasketKrkApplication.kt` - Application class that initializes Koin

**Shared DI Module:**
- Purpose: Coordinates initialization of all other DI modules
- Location: `/shared/src/commonMain/kotlin/com/mzs/basket_krk/shared/di/`
- File: `KoinHelper.kt`
- Initializes: dataModule, presentationModule
- Called from: BasketKrkApplication

**Data Utilities:**
- Purpose: Shared test data and utilities
- Location: `/data-utils/src/commonMain/kotlin/com/mzs/basket_krk/datautils/`
- Contains: Fake data generators for testing (LeagueFakeData, MatchFakeData, etc.)

## Data Flow

**Fetch Match Details Flow:**

1. User clicks match in Matches list
2. Navigation calls MatchDetailsScreen with matchId parameter
3. MatchDetailsViewModel instantiated with matchId via Koin
4. ViewModel calls GetMatchDetailsUseCase
5. UseCase invokes MatchRepository.getMatchDetails(matchId)
6. MatchRepository delegates to MatchService (from data layer)
7. MatchService (NetworkMatchService) calls ApiService.get<MatchDetailsDto>()
8. ApiService makes HTTP request to backend (http://130.61.230.255:8000/)
9. Response deserialized to MatchDetailsDto
10. MatchService transforms DTO to domain model MatchDetails
11. Result wrapped in Arrow Either<Failure, MatchDetails> and returned up the chain
12. ViewModel receives Either, extracts value or error
13. ViewState updated with MatchDetails
14. Compose UI recomposes with match data and player stats tables

**Pagination Flow (All-Time Leaders):**

1. AllTimeLeadersScreen rendered with LazyColumn
2. Paging data sourced from AllTimeLeadersPagingSource
3. PagingSource creation delegated to AllTimeLeadersPagingSourceFactory
4. load(params: LoadParams) called by Paging library
5. PagingSource calls GetAllTimeLeadersUseCase with page number
6. UseCase calls LeagueRepository.getAllTimeLeaders()
7. Repository calls LeagueService.getAllTimeLeaders()
8. Network call to API, response paginated results in PageableData<AllTimeLeader>
9. Paging library handles result emission and caching
10. UI updates incrementally as pages load

**State Management:**

- ViewModels hold MutableStateFlow<*ViewState> for observable state
- Effects/side effects emitted via MutableSharedFlow<*Effect>
- Reactive chains via flatMapLatest when dependencies change (e.g., season selection changes rounds)
- Loading states tracked in ViewState (fullScreenLoading, error)

## Key Abstractions

**Repository Pattern:**
- Purpose: Abstract data source details from business logic
- Examples: `domain/repository/LeagueRepository.kt`, `data/repository/LeagueRepositoryImpl.kt`
- Pattern: Interface in domain layer, implementation in data layer

**Use Case Pattern:**
- Purpose: Encapsulate business logic and orchestrate repository calls
- Examples: `domain/usecase/GetSeasonsInfoUseCase.kt`, `GetMatchDetailsUseCase.kt`
- Pattern: Interface (e.g., GetSeasonsInfo) + class extending interface with dependency injection
- Base interfaces: `SuspendOutUseCase<Output>`, `SuspendInOutUseCase<Input, Output>` defined in `domain/base/UseCase.kt`

**Service Pattern:**
- Purpose: Handle API communication and data transformation
- Domain Services: Abstract interfaces (`domain/service/LeagueService.kt`)
- Network Services: Implementations (`data/service/NetworkLeagueService.kt`)
- Pattern: Domain defines contract, data layer provides network implementation

**ViewModel with State + Effects:**
- Purpose: Manage UI state and communicate UI events
- Examples: `presentation/screens/main/matches/MatchesViewModel.kt`
- Pattern: Single MutableStateFlow for state, MutableSharedFlow for effects
- State classes: immutable data classes marked with @Immutable
- Effect classes: sealed classes for different event types

**Either Monad for Error Handling:**
- Purpose: Type-safe error handling without exceptions
- Pattern: All use cases return Either<Failure, T>
- Extension functions in `domain/base/EitherExtensions.kt`
- Usage: onSuspendSuccess/onSuspendGeneralError for reactive handling

**Pagination Source Factory:**
- Purpose: Decouple pagination logic from business logic
- Examples: `presentation/screens/main/matches/pagination/MatchesPagingSourceFactory.kt`
- Pattern: Factory creates PagingSource instances, injected into ViewModel

## Entry Points

**Android Entry Point:**
- Location: `composeApp/src/androidMain/kotlin/com/mzs/basket_krk/MainActivity.kt`
- Triggers: App launch on Android
- Responsibilities: Set edge-to-edge layout, call Compose setContent with App()

**Application Initialization:**
- Location: `composeApp/src/androidMain/kotlin/com/mzs/basket_krk/BasketKrkApplication.kt`
- Triggers: Application process creation
- Responsibilities: Initialize Koin via initKoin()/startKoin with androidContext

**DI Initialization:**
- Location: `shared/src/commonMain/kotlin/com/mzs/basket_krk/shared/di/KoinHelper.kt`
- Function: initKoin(appDeclaration)
- Responsibilities: Start Koin, load dataModule and presentationModule

**Compose App Root:**
- Location: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt`
- Triggers: Called from MainActivity.setContent
- Responsibilities: Setup NavHost, define all navigation routes, instantiate ViewModels

**Navigation Routes:**
- Defined in: `presentation/navigation/Screen.kt` (sealed class with @Serializable)
- Routes: Main, Settings, MatchDetails(matchId), AllTimeLeaders, Standings
- Entry points: Each route has corresponding Composable in App.kt NavHost

## Error Handling

**Strategy:** Functional error handling with Arrow Either monad

**Patterns:**

1. **Return Type Wrapper:**
   - All repository/service methods return `Either<Failure, T>`
   - Success: `Either.Right(value)`
   - Failure: `Either.Left(failure)`

2. **Failure Sealed Class:**
   - Located: `domain/model/Failure.kt`
   - Types: Network errors, parsing errors, unknown errors
   - Logged via: Kermit logger in ViewModels

3. **Extension Functions:**
   - `onSuspendSuccess()` - execute block if Right
   - `onSuspendGeneralError()` - execute block if Left with Throwable
   - `always()` - execute block regardless of result
   - Located: `domain/base/EitherExtensions.kt`

4. **ViewModel Error Handling:**
   - Errors stored in ViewState: `error: Failure?`
   - UI renders error via FullScreenLoader or custom error display
   - Example: `MatchesViewModel.kt` line 134-137

## Cross-Cutting Concerns

**Logging:**
- Framework: Kermit (co.touchlab.kermit.Logger)
- Usage: VM errors logged in catch blocks
- Example: `Logger.e("Error when fetching data", error)`

**Validation:**
- Mostly implicit (API returns valid data)
- UI validation: Season selection, round selection in MatchesViewModel
- Model validation: Data class constraints

**Authentication:**
- Not detected in current codebase
- API endpoint is public (130.61.230.255:8000)

**Serialization:**
- Framework: Kotlin serialization (@Serializable annotations)
- DTOs: Located in `data/dto/`
- Custom serializers: `data/serializer/`

---

*Architecture analysis: 2026-03-16*
