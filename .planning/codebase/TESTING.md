# Testing Patterns

**Analysis Date:** 2026-03-16

## Test Framework Setup

**Status:** Infrastructure configured, no test implementations yet

**Test Runner:**
- Framework: kotlin.test (Kotlin multiplatform testing)
- Version: 2.2.21 (matches Kotlin version in `gradle/libs.versions.toml`)
- Config: No dedicated test configuration file (uses default Gradle test configuration)

**Unit Test Assertion Library:**
- kotlin.test built-in assertions
- JUnit4 (4.13.2) for Android device tests

**Additional Test Dependencies:**
- `androidx.test.ext.junit` (1.3.0) - AndroidX JUnit extensions for device tests
- `androidx.test.runner` (1.7.0) - AndroidX test runner for Android instrumented tests
- `androidx.test.espresso` (3.7.0) - UI testing (optional, configured but not used)

## Test Structure

**Test Source Sets (configured but not populated):**
- `commonTest` - Shared tests across all platforms
- `androidDeviceTest` - Android-specific instrumented tests
- `androidHostTest` - Android local JVM tests

**Configuration from build.gradle.kts (data module example):**
```kotlin
kotlin {
    androidLibrary {
        withHostTestBuilder { }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }
    }
}
```

## Modules with Test Infrastructure

**All main modules have test structure configured:**
- `data/build.gradle.kts` - Data layer tests
- `domain/build.gradle.kts` - Domain layer tests
- `data-utils/build.gradle.kts` - Data utilities tests
- `composeApp/build.gradle.kts` - Presentation tests

**No test files currently exist** in the codebase. When adding tests, create them in:
- `[module]/src/commonTest/kotlin/...` for shared tests
- `[module]/src/androidDeviceTest/kotlin/...` for Android instrumented tests

## Recommended Testing Strategy

**By Layer:**

**Domain Layer Tests (`domain/src/commonTest/kotlin/...`):**
- Test UseCase implementations
- Test domain extensions and helper functions
- Test error handling with Either/Failure patterns
- No mocking needed for business logic tests

**Data Layer Tests (`data/src/commonTest/kotlin/...`):**
- Test Repository implementations
- Test NetworkService implementations
- Mock ApiService (HTTP client)
- Test DTO to domain model mapping (toDomain() functions)

**Presentation Layer Tests (`presentation/src/commonTest/kotlin/...`):**
- Test ViewModel state management
- Test state flows and state updates
- Mock UseCase dependencies
- Test Compose composable UI logic (structure, not rendering)

**Example Test Structure (for when tests are implemented):**

```kotlin
// Domain UseCase Test
class GetMatchesUseCaseTest {
    private val mockRepository = mockk<MatchRepository>()
    private val useCase = GetMatchesUseCase(mockRepository)

    @Test
    fun `invoke calls repository with correct parameters`() = runTest {
        val input = GetMatchesUseCase.Input(roundId = 1, page = 0, pageSize = 15)
        val expected = Either.Right(PageableData(emptyList(), null))

        coEvery { mockRepository.getMatches(1, 0) } returns expected

        val result = useCase(input)

        assertEquals(expected, result)
        coVerify { mockRepository.getMatches(1, 0) }
    }
}

// Repository Test
class MatchRepositoryImplTest {
    private val mockService = mockk<MatchService>()
    private val repository = MatchRepositoryImpl(mockService)

    @Test
    fun `getMatchDetails sorts teams by stat`() = runTest {
        val matchDetails = createTestMatchDetails()
        coEvery { mockService.getMatchDetails(1) } returns Either.Right(matchDetails)

        val result = repository.getMatchDetails(1)

        assertTrue(result.isRight())
    }
}

// ViewModel Test
class MatchDetailsViewModelTest {
    private val mockUseCase = mockk<GetMatchDetails>()
    private val viewModel = MatchDetailsViewModel(matchId = 1, getMatchDetails = mockUseCase)

    @Test
    fun `initial state triggers fetch`() = runTest {
        coVerify { mockUseCase(any()) }
    }

    @Test
    fun `successful fetch updates view state`() = runTest {
        val matchDetails = createTestMatchDetails()
        coEvery { mockUseCase(any()) } returns Either.Right(matchDetails)

        val state = viewModel.viewState.first()

        assertEquals(matchDetails, state.matchDetails.data)
    }
}
```

## Test Data Utilities

**Fake Data Module:** `data-utils/src/commonMain/kotlin/com/mzs/basket_krk/datautils/`

Available fake data factories for testing:
- `MatchFakeData.kt` - Create test Match instances
- `PlayerFakeData.kt` - Create test Player instances
- `LeagueFakeData.kt` - Create test League instances
- `SeasonFakeData.kt` - Create test Season instances
- `StatFakeData.kt` - Create test Stat instances
- `SearchFakeData.kt` - Create test SearchItem instances

These should be used to create test data in unit tests instead of manually constructing domain models.

## Test Execution

**Run Commands (when tests are implemented):**

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :data:test
./gradlew :domain:test
./gradlew :presentation:test

# Run Android device tests
./gradlew connectedAndroidTest

# Run specific test class
./gradlew :domain:test --tests "GetMatchesUseCaseTest"

# Run with coverage
./gradlew test --info
```

## Code Coverage

**Status:** Not enforced currently

**When implementing coverage:**
- Use built-in Gradle test reports
- View coverage: Check `[module]/build/reports/tests/test/index.html`
- Target minimum coverage after test implementation

## Current Test Gaps

**Untested Areas:**
- All UseCase implementations
- All Repository implementations
- All ViewModel state management
- All DTO to domain model conversions
- Network service error handling
- Pagination logic
- Compose UI components (structure/state)

**Why Critical:**
- Error handling paths not validated
- Business logic transformations not verified
- State flow updates not guaranteed to work correctly
- Network error scenarios not tested

## Error Handling Tests

**Pattern to test (from EitherExtensions.kt):**

When tests are implemented, verify:
- `Either.catchWithError { }` properly wraps exceptions as Failure
- `.onSuspendSuccess { }` executes only on success
- `.onSuspendGeneralError { }` executes only on error
- Error types (UnknownError, ApiError, NetworkConnectionError, etc.) are properly set

**Example:**
```kotlin
@Test
fun `catchWithError wraps throwable as UnknownError`() = runTest {
    val result = Either.catchWithError<String> {
        throw RuntimeException("test")
    }

    assertTrue(result.isLeft())
    assertTrue(result.leftOrNull() is Failure.UnknownError)
}
```

## Mock Dependencies

**Recommended Mocking Library:** MockK
- Not yet included in dependencies
- Should be added to `gradle/libs.versions.toml` when implementing tests
- Usage: `mockk<Interface>()` for creating mocks
- Suspend function support: `coEvery { }` and `coVerify { }`

**What to Mock:**
- Repository interfaces
- Service interfaces
- UseCase dependencies
- NetworkService (when testing Repository)

**What NOT to Mock:**
- Domain models (use fake data utilities instead)
- Data transfer logic (verify actual conversions)
- Sealed classes like Failure (create actual instances)

## State Flow Testing

**Testing Pattern for StateFlow:**

```kotlin
// Verify state updates
val state: MatchDetailsViewState = viewModel.viewState.first()
assertEquals(expectedValue, state.matchDetails.data)

// Test state flow emissions
viewModel.viewState.test {
    awaitItem() // initial state
    viewModel.retry()
    awaitItem() // loading state
    awaitItem() // loaded state
}
```

Requires `kotlinx.coroutines.flow.test` package when advanced flow testing needed.

## Integration Testing

**Approach (when needed):**
- Test actual Repository + Service + API integration
- Use test doubles for HTTP client (avoid real API calls)
- Verify end-to-end data transformation from API DTOs to domain models

**Example Integration Test:**
```kotlin
@Test
fun `MatchRepository getMatches returns properly mapped data`() = runTest {
    val mockApiService = mockk<ApiService>()
    val service = NetworkMatchService(mockApiService)
    val repository = MatchRepositoryImpl(service)

    coEvery {
        mockApiService.get<MatchesListDto>("/round/1/?page=0")
    } returns testMatchListDto()

    val result = repository.getMatches(1, 0)

    assertTrue(result.isRight())
    result.fold(
        ifLeft = { fail("Should not have error") },
        ifRight = { data ->
            assertEquals(expectedMatches, data.data)
        }
    )
}
```

## Continuous Integration

**CI Pipeline:** Not configured (see INTEGRATIONS.md)

When implementing CI:
- Run all tests on pull requests
- Run android device tests on emulator
- Fail builds on test failures
- Generate coverage reports

---

*Testing analysis: 2026-03-16*
