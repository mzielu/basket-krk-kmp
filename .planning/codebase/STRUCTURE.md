# Codebase Structure

**Analysis Date:** 2026-03-16

## Directory Layout

```
basket-krk-kmp/
├── composeApp/               # Android/Compose app entry point
│   └── src/
│       └── androidMain/      # Android-specific entry point
│           ├── kotlin/com/mzs/basket_krk/
│           │   ├── MainActivity.kt
│           │   └── BasketKrkApplication.kt
│           └── res/          # Android resources
├── domain/                   # Domain layer (business logic)
│   ├── src/commonMain/       # Shared code across platforms
│   │   └── kotlin/com/mzs/basket_krk/domain/
│   │       ├── base/         # Base classes for use cases
│   │       ├── model/        # Domain entities
│   │       ├── repository/   # Repository interfaces
│   │       ├── service/      # Service interfaces
│   │       ├── usecase/      # Use case implementations
│   │       └── DomainExtensions.kt
│   ├── src/iosMain/          # iOS-specific implementations
│   └── build.gradle.kts
├── data/                     # Data layer (repositories, API)
│   ├── src/commonMain/       # Shared code
│   │   └── kotlin/com/mzs/basket_krk/data/
│   │       ├── di/           # Koin DI configuration
│   │       ├── dto/          # Data transfer objects
│   │       ├── repository/   # Repository implementations
│   │       ├── serializer/   # Custom serialization
│   │       └── service/      # Network service implementations
│   ├── src/androidMain/      # Android-specific implementations
│   ├── src/iosMain/          # iOS-specific implementations
│   └── build.gradle.kts
├── presentation/             # Presentation layer (UI/ViewModels)
│   ├── src/commonMain/       # Shared Compose UI code
│   │   └── kotlin/com/mzs/basket_krk/presentation/
│   │       ├── base/         # Base UI components and pagination
│   │       ├── di/           # Koin DI configuration
│   │       ├── navigation/   # Navigation graph
│   │       ├── screens/      # Screen components
│   │       │   ├── main/     # Main tab screens
│   │       │   │   ├── matches/
│   │       │   │   ├── search/
│   │       │   │   ├── statistics/
│   │       │   │   └── more/
│   │       │   └── matchdetails/
│   │       └── App.kt        # Root composable
│   ├── src/iosMain/          # iOS-specific UI
│   └── build.gradle.kts
├── shared/                   # Shared module (DI coordination)
│   ├── src/commonMain/
│   │   └── kotlin/com/mzs/basket_krk/shared/
│   │       └── di/
│   │           └── KoinHelper.kt
│   └── build.gradle.kts
├── data-utils/               # Test data and utilities
│   ├── src/commonMain/
│   │   └── kotlin/com/mzs/basket_krk/datautils/
│   │       └── *FakeData.kt  # Fake data generators
│   └── build.gradle.kts
├── iosApp/                   # iOS app (native Swift)
│   └── iosApp.xcodeproj/
├── build.gradle.kts          # Root Gradle build file
├── settings.gradle.kts       # Module configuration
├── gradle.properties         # Gradle configuration
├── gradlew, gradlew.bat      # Gradle wrapper
└── README.md
```

## Directory Purposes

**composeApp:**
- Purpose: Entry point for Android app, ties together all layers
- Contains: Activity, Application class, Android resources/manifest
- Key files: `MainActivity.kt`, `BasketKrkApplication.kt`
- Platform: Android-only currently

**domain:**
- Purpose: Pure business logic, framework-agnostic
- Contains: Models, repository/service interfaces, use cases
- Key structure:
  - `model/` - League, Match, Season, Player, Round, Competition, etc.
  - `repository/` - LeagueRepository, MatchRepository, SeasonRepository, SearchRepository
  - `service/` - LeagueService, MatchService, SeasonService, SearchService
  - `usecase/` - GetSeasonsInfoUseCase, GetMatchesUseCase, etc.
  - `base/` - UseCase interfaces, Either extension functions
- Dependencies: Arrow library only (Either type)
- Target: Multiplatform (Android, iOS)

**data:**
- Purpose: Data access implementation, API communication
- Contains: DTOs, API client, repository implementations, network services
- Key structure:
  - `dto/` - Serializable data classes (MatchDto, LeagueDto, PlayerWithStatDto, etc.)
  - `repository/` - Repository implementations wrapping services
  - `service/` - Network service implementations
    - `ApiService.kt` - Low-level HTTP client wrapper (Ktor)
    - `NetworkLeagueService.kt`, `NetworkMatchService.kt`, etc.
    - `HttpClientFactory.kt` - Platform-specific HTTP client creation
  - `di/` - Koin module for data layer dependency injection
  - `serializer/` - Custom serialization logic
- Platform-specific: androidMain/, iosMain/ for HTTP client setup
- Depends on: Domain layer (implements contracts)
- Target: Multiplatform

**presentation:**
- Purpose: UI layer, state management, navigation
- Contains: Compose screens, ViewModels, navigation definitions
- Key structure:
  - `screens/main/` - Main navigation tabs
    - `matches/` - Match list with pagination, season/round selection
    - `search/` - Search functionality with pagination
    - `statistics/` - Statistics tabs
      - `alltimeleaders/` - All-time player leaders with pagination
      - `standings/` - Competition standings
    - `more/` - Additional screens
  - `screens/matchdetails/` - Match details with player stats tables
  - `navigation/` - Screen route definitions (@Serializable sealed class)
  - `base/` - Base components (FullScreenLoader, EmptyView, BasePagingSource)
  - `di/` - Koin module for ViewModels and use cases
  - `App.kt` - Root composable with NavHost
- Platform-specific: iosMain/ for platform-specific UI
- Depends on: Domain layer (uses), Compose, Koin
- Target: Multiplatform

**shared:**
- Purpose: DI initialization coordination
- Contains: Single KoinHelper.kt file
- Responsibility: Initialize Koin with dataModule and presentationModule
- Called from: BasketKrkApplication.onCreate()

**data-utils:**
- Purpose: Shared test utilities and fake data
- Contains: Fake data generators for testing
- Files: LeagueFakeData.kt, MatchFakeData.kt, PlayerFakeData.kt, etc.
- Use case: Support for UI preview and testing

**iosApp:**
- Purpose: iOS app entry point (native Swift)
- Contains: Xcode project configuration
- Uses: Kotlin framework generated from domain/data/presentation modules

## Key File Locations

**Entry Points:**
- `composeApp/src/androidMain/kotlin/com/mzs/basket_krk/MainActivity.kt` - Android app entry
- `composeApp/src/androidMain/kotlin/com/mzs/basket_krk/BasketKrkApplication.kt` - App lifecycle
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` - Compose root

**Configuration:**
- `settings.gradle.kts` - Module includes (composeApp, domain, data, presentation, shared)
- `build.gradle.kts` - Root build plugins
- `gradle.properties` - Gradle settings
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt` - Presentation DI
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/di/DataModule.kt` - Data DI
- `shared/src/commonMain/kotlin/com/mzs/basket_krk/shared/di/KoinHelper.kt` - DI initialization

**Core Logic:**
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/` - Domain models
- `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/` - Use cases
- `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/` - Network services
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/` - UI screens

**Navigation:**
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt` - Route definitions
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/MainTab.kt` - Tab definitions
- `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` - NavHost configuration

**Testing/Data:**
- `data-utils/src/commonMain/kotlin/com/mzs/basket_krk/datautils/` - Fake data generators

## Naming Conventions

**Files:**
- ViewModels: `*ViewModel.kt` (e.g., `MatchesViewModel.kt`, `MainViewModel.kt`)
- Screens: `*Screen.kt` (e.g., `MatchDetailsScreen.kt`, `StandingsScreen.kt`)
- Components: `*Component.kt` or descriptive names (e.g., `MatchDetailsTeamTable.kt`, `CompetitionItem.kt`)
- Models: Entity names (e.g., `Match.kt`, `League.kt`, `Season.kt`)
- DTOs: `*Dto.kt` (e.g., `MatchDto.kt`, `LeagueDetailsDto.kt`)
- Services: `*Service.kt` interfaces, `Network*Service.kt` implementations
- Repositories: `*Repository.kt` interfaces, `*RepositoryImpl.kt` implementations
- Use Cases: `Get*UseCase.kt` (e.g., `GetMatchDetailsUseCase.kt`)
- Pagination: `*PagingSource.kt`, `*PagingSourceFactory.kt`
- DI Modules: `*Module.kt` (e.g., `DataModule.kt`, `PresentationModule.kt`)

**Directories:**
- Feature screens: Lowercase plural (e.g., `matches/`, `statistics/`, `screens/`)
- Packages: lowercase with underscores (e.g., `com.mzs.basket_krk.domain`)
- Layer packages: `model/`, `repository/`, `service/`, `usecase/`, `dto/`
- UI structure: `screens/[feature]/` with optional `components/` and `pagination/` subdirs

## Where to Add New Code

**New Feature (e.g., Player Details Screen):**
- Domain model: `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/PlayerDetails.kt`
- Domain use case: `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetPlayerDetailsUseCase.kt`
- Domain repository: Update `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/PlayerRepository.kt` (interface)
- Data DTO: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/PlayerDetailsDto.kt`
- Data repository impl: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/PlayerRepositoryImpl.kt`
- Network service: Update `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkPlayerService.kt`
- ViewModel: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsViewModel.kt`
- Screen: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/playerdetails/PlayerDetailsScreen.kt`
- Navigation: Add route to `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/navigation/Screen.kt`
- DI: Register in `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/di/PresentationModule.kt`

**New Component/Module:**
- Shared utility: `data-utils/src/commonMain/kotlin/com/mzs/basket_krk/datautils/`
- Base component: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/`
- UI helpers: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/`

**Utilities:**
- Shared helpers in domain: `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/` (e.g., `DomainExtensions.kt`)
- Extension functions: In corresponding layer files or dedicated extension files

## Special Directories

**build/ (Generated):**
- Generated by Gradle build process
- Contains compiled classes and resources
- Committed: No
- Not in version control (.gitignore)

**gradle/ (Gradle wrapper):**
- Contains Gradle wrapper distribution for build consistency
- Committed: Yes (checked in for reproducible builds)

**.gradle/ (Gradle cache):**
- Local Gradle cache
- Committed: No (.gitignore)

**iosApp/iosApp.xcodeproj/ (Xcode project):**
- Generated and configured by Xcode
- Committed: Partially (some files, some in .gitignore)

**.planning/codebase/ (Architecture documentation):**
- New directory for analysis documents
- Committed: Yes
- Contains: ARCHITECTURE.md, STRUCTURE.md, CONVENTIONS.md, TESTING.md, CONCERNS.md

---

*Structure analysis: 2026-03-16*
