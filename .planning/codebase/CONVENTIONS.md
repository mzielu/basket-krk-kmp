# Coding Conventions

**Analysis Date:** 2026-03-16

## Language & Code Style

**Language:** Kotlin 2.2.21

**Code Style:** Official Kotlin style (configured via `kotlin.code.style=official` in `gradle.properties`)

**IDE Formatting:** IntelliJ IDEA with built-in Kotlin formatter

## Naming Patterns

**Files:**
- Data Transfer Objects (DTOs): `[Name]Dto.kt` - Example: `MatchesListDto.kt` (located at `data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/MatchesListDto.kt`)
- Domain models: `[Name].kt` - Example: `Season.kt` (located at `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Season.kt`)
- Repository implementations: `[Name]RepositoryImpl.kt` - Example: `MatchRepositoryImpl.kt` (located at `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/MatchRepositoryImpl.kt`)
- Repository interfaces: `[Name]Repository.kt` - Example: `MatchRepository.kt` (located at `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/MatchRepository.kt`)
- Network services: `Network[Name]Service.kt` - Example: `NetworkMatchService.kt` (located at `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/NetworkMatchService.kt`)
- Use cases: `Get[Name]UseCase.kt` - Example: `GetMatchesUseCase.kt` (located at `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/GetMatchesUseCase.kt`)
- ViewModels: `[Name]ViewModel.kt` - Example: `MatchDetailsViewModel.kt` (located at `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsViewModel.kt`)
- Screens: `[Name]Screen.kt` - Example: `MatchDetailsScreen.kt` (located at `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsScreen.kt`)
- Components: `[Name]Item.kt` or `[Name]Table.kt` - Example: `MatchDetailsTeamTable.kt` (located at `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/components/MatchDetailsTeamTable.kt`)

**Classes/Types:**
- Class names: PascalCase - Example: `MatchDetailsViewModel`, `MatchRepositoryImpl`
- Data classes: PascalCase with `data class` keyword - Example: `Season(val id: Int, val num: Int)`
- Sealed classes: PascalCase with `sealed class` keyword - Example: `sealed class Failure : Throwable()`
- Interface names: PascalCase, no "I" prefix - Example: `MatchRepository`, `GetMatches`

**Functions:**
- Function names: camelCase - Example: `getMatches()`, `getMatchDetails()`
- Composable functions: PascalCase (convention for @Composable) - Example: `MatchDetailsScreen()`, `MatchListItem()`
- Extension functions: camelCase - Example: `.toDomain()`, `.sortTeam()`, `.onSuspendSuccess()`
- Private/internal functions: camelCase with leading underscore for state flow backing fields - Example: `_viewState` (MutableStateFlow), `fetchMatchDetails()`

**Variables:**
- Local variables: camelCase - Example: `selectedRound`, `matchDetails`, `errorMessage`
- Constants: UPPER_SNAKE_CASE - Example: `PAGE_SIZE = 15` (in `MatchesViewModel.kt`)
- Immutable values: camelCase - Example: `val seasons: List<Season>`
- State flows: `val` for public exposed as `StateFlow`, private backing field `_viewState: MutableStateFlow` - Example in `MatchDetailsViewModel.kt`:
  ```kotlin
  private val _viewState: MutableStateFlow<MatchDetailsViewState> = MutableStateFlow(...)
  val viewState: StateFlow<MatchDetailsViewState> = _viewState.asStateFlow()
  ```

**Package Names:**
- Package structure: `com.mzs.basket_krk.[module].[layer]` - Examples:
  - `com.mzs.basket_krk.data.dto` - Data layer DTOs
  - `com.mzs.basket_krk.data.repository` - Repository implementations
  - `com.mzs.basket_krk.domain.model` - Domain models
  - `com.mzs.basket_krk.domain.repository` - Repository interfaces
  - `com.mzs.basket_krk.presentation.screens.main.matches` - Presentation screens

## Code Organization

**Class Member Order:**
1. Companion object and constants
2. Constructor parameters (for primary constructor)
3. Private properties/state flows
4. Public properties/state flows
5. init block
6. Operator functions (invoke for use cases)
7. Public methods
8. Private methods

**Example from MatchDetailsViewModel.kt:**
```kotlin
class MatchDetailsViewModel(
    private val matchId: Int,
    private val getMatchDetails: GetMatchDetails
) : ViewModel() {
    private val _viewState: MutableStateFlow<MatchDetailsViewState> = MutableStateFlow(...)
    val viewState: StateFlow<MatchDetailsViewState> = _viewState.asStateFlow()

    init {
        fetchMatchDetails()
    }

    fun retry() { ... }
    private fun fetchMatchDetails() { ... }
}
```

## Import Organization

**Order:**
1. Kotlin stdlib and ktx imports
2. AndroidX/compose imports
3. Third-party library imports (arrow, koin, ktor, etc.)
4. Project imports (domain, data, presentation modules)
5. Resource imports (generated)

**Example from NetworkMatchService.kt:**
```kotlin
package com.mzs.basket_krk.data.service

import arrow.core.Either
import com.mzs.basket_krk.data.dto.MatchDetailsDto
import com.mzs.basket_krk.data.dto.MatchesListDto
import com.mzs.basket_krk.data.dto.toDomain
import com.mzs.basket_krk.domain.base.catchWithError
import com.mzs.basket_krk.domain.model.Failure
...
```

## Error Handling

**Pattern:** Arrow's `Either<Failure, T>` for error handling

**Failure Types (sealed class):** Located at `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/model/Failure.kt`
- `Failure.UnknownError(throwable: Throwable)` - Unexpected exceptions
- `Failure.OldVersionError` - API version mismatch
- `Failure.NoDataAvailableError` - No data returned
- `Failure.ApiError(errorType: String)` - API-specific errors
- `Failure.NetworkConnectionError` - Network failures

**Catch Pattern:** Use `Either.catchWithError { }` for wrapping operations that throw exceptions
```kotlin
// From NetworkMatchService.kt
override suspend fun getMatches(roundId: Int, page: Int): Either<Failure, PageableData<Match>> {
    return Either.catchWithError {
        apiService.get<MatchesListDto>("/round/$roundId/?page=$page").toDomain()
    }
}
```

**Handling Errors:** Use extension functions in `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/base/EitherExtensions.kt`
```kotlin
getMatchDetails(input = GetMatchDetailsUseCase.Input(matchId = matchId))
    .onSuspendSuccess { details ->
        _viewState.update { it.copy(matchDetails = it.matchDetails.data(details)) }
    }.onSuspendGeneralError { error ->
        Logger.e("Error when fetching match details", error)
        _viewState.update { it.copy(matchDetails = it.matchDetails.error(error)) }
    }
```

**Available Extensions:**
- `.onSuspendSuccess(block: suspend (R) -> Unit)` - Handle success in suspend context
- `.onSuspendGeneralError(block: suspend (L) -> Unit)` - Handle error in suspend context
- `.onSuccess(block: (R) -> Unit)` - Handle success synchronously
- `.onGeneralError(block: (L) -> Unit)` - Handle error synchronously
- `.always(block: () -> Unit)` - Execute regardless of result

## Logging

**Framework:** Kermit (co.touchlab.kermit)

**Usage Pattern:**
```kotlin
Logger.e("Error when fetching match details", error)
Logger.d("Debug message")
Logger.i("Info message")
```

**When to Log:**
- Log errors in catch/error handlers with context about what operation failed
- Use Logger import: `import co.touchlab.kermit.Logger`
- Error logs include the throwable/exception for debugging

## Comments

**When to Comment:**
- Explain WHY code does something, not WHAT it does
- TODO comments for unimplemented features - Example: `// TODO implement page size in backend` (found in multiple UseCase files)
- Non-obvious business logic

**Multi-line Comments:** Use `/* */` for block comments explaining complex logic

**TODO Format:** `// TODO [description of what needs to be done]`

## Data Classes and Models

**Data Class Pattern:**
```kotlin
// DTOs use @Serializable for kotlinx.serialization
@Serializable
data class MatchesListDto(
    val data: List<MatchDto>,
    val next: String? = null
)

// Domain models are plain data classes
data class Season(
    val id: Int,
    val num: Int
)
```

**Mapper Functions:** Use extension functions named `toDomain()` for converting DTOs to domain models
```kotlin
// From MatchesListDto.kt
fun MatchesListDto.toDomain(): PageableData<Match> {
    return PageableData(
        data = data.map { it.toDomain() },
        next = next
    )
}
```

## Architecture Patterns

**Layer Responsibilities:**
- **Domain** (`domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/`) - Business logic, models, interfaces
- **Data** (`data/src/commonMain/kotlin/com/mzs/basket_krk/data/`) - Implementation of repositories, network services
- **Presentation** (`presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/`) - UI, ViewModels, Composables

**Dependency Injection:** Koin DI framework
- Module definitions in each layer's `di/` subdirectory
- Initialized in `shared/src/commonMain/kotlin/com/mzs/basket_krk/shared/di/KoinHelper.kt`

**Use Cases:** Interface-based pattern with concrete implementations
- Base interfaces in `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/base/UseCase.kt`
- Implementation in `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/usecase/`
- Each use case returns `Either<Failure, T>` for error handling

**Repository Pattern:** Interface in domain, implementation in data
- Domain interface: `domain/src/commonMain/kotlin/com/mzs/basket_krk/domain/repository/`
- Data implementation: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/`

**ViewModel Pattern:** MutableStateFlow for state management, public as StateFlow
- State data class with @Immutable annotation
- State updates via `_viewState.update { ... }`
- Effects pattern using SharedFlow for one-time events

## Compose Conventions

**Composable Naming:** PascalCase (standard Compose convention)
- Screens: `[Name]Screen` - Example: `MatchDetailsScreen`
- Components: `[Name]Item`, `[Name]Table`, etc. - Example: `MatchDetailsTeamTable`

**Composable Parameters:**
- `modifier: Modifier = Modifier` as last parameter
- Required parameters before optional ones
- Callbacks before other optional parameters

**State Management in Composables:**
```kotlin
val viewState by viewModel.viewState.collectAsState()
// Use collectAsState() to collect StateFlow in composable context
```

**Preview Annotation:** Use `@Preview` from `org.jetbrains.compose.ui.tooling.preview.Preview`

## Null Safety

**Nullable Types:** Use `Type?` explicitly
- Check with `?.let { }` or `?.also { }`
- Use `?:` Elvis operator for defaults
- Prefer non-null defaults in data classes

## String Resources

**Generated Resources:** Uses Compose Multiplatform Resources
- Import from generated resource files: `import basket_krk.[module].generated.resources.Res`
- Access: `stringResource(Res.string.label_match_about_start)`

## Testing Conventions

**Not Implemented:** The project has testing infrastructure configured (kotlin-test, junit4, androidx.test) in `gradle/libs.versions.toml` but no test files currently exist in the codebase. When tests are added, follow the patterns established by the architecture.

---

*Convention analysis: 2026-03-16*
