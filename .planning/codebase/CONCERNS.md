# Codebase Concerns

**Analysis Date:** 2026-03-16

## Tech Debt

**Unimplemented Navigation Routes:**
- Issue: Multiple TODOs in navigation that block feature completion
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` (lines 51, 53, 81), `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsScreen.kt` (lines 122, 125)
- Impact: Player details, team details screens cannot be accessed; partial navigation implementation creates dead-end screens
- Fix approach: Implement remaining navigation routes and associated screens; create player details and team details screens with corresponding ViewModels

**Overly Large Composable Functions:**
- Issue: Single-responsibility principle violated; complex UI logic mixed with business logic
- Files:
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsScreen.kt` (429 lines)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/standings/components/CompetitionItem.kt` (208 lines)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/BasketKrkStyles.kt` (278 lines)
- Impact: Difficult to test, maintain, and reuse; increased cognitive complexity; harder to track state changes
- Fix approach: Extract smaller, composable functions; move style definitions to separate constants file; use composition patterns for complex layouts

**Nullable Type Handling in ViewModels:**
- Issue: Multiple ViewModels rely on null checks after lazy initialization
- Files:
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/MatchesViewModel.kt` (line 100: `if (::pagingSource.isInitialized)`)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersViewModel.kt` (line 36: `private lateinit var pagingSource`)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/search/SearchViewModel.kt` (lines 42, 71)
- Impact: Risk of UninitializedPropertyAccessException if pagingFlow accessed before initialization; defensive coding necessary
- Fix approach: Use lazy delegation consistently or initialize pagingSource in init block; consider using Paging 3 properly without lateinit

## Known Bugs

**Serializer Type Mismatch Risk:**
- Symptoms: Potential runtime serialization failures when SearchResultDto contains unknown type field value
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/serializer/SearchResultDtoSerializer.kt` (lines 37-38)
- Trigger: API response with unrecognized "type" value in SearchResultDto
- Workaround: None currently; will cause IllegalArgumentException crash
- Fix approach: Add fallback handling for unknown types; log and return default SearchItem instead of throwing

**Image Loading with Hardcoded Domain:**
- Symptoms: All images fail to load if basketkrk.pl domain changes or is unavailable
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/ui/BasketKrkImage.kt` (line 19)
- Trigger: Domain migration, API changes, network issues
- Workaround: Manual URL override would require code change
- Fix approach: Make base URL configurable via dependency injection; add fallback placeholder images

## Security Considerations

**HTTP Client Header Hardcoding:**
- Risk: "OS", "APP-VERSION" headers hardcoded may cause issues with API versioning or multi-platform support
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt` (lines 29-31)
- Current mitigation: Hardcoded values are OS-specific (though set to "android" for all)
- Recommendations: Make headers configurable per platform; use actual OS detection; implement version tracking system

**Error Throwing in Serializer:**
- Risk: Custom serializer throws RuntimeException directly, which is untyped and harder to handle gracefully
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/serializer/SearchResultDtoSerializer.kt` (lines 19, 24, 38, 45)
- Current mitigation: Errors are caught in Either.catchWithError, but exception type is overly generic
- Recommendations: Define custom serialization exceptions; map to proper Failure types; provide better error messages

## Performance Bottlenecks

**Inefficient State Updates in Paging:**
- Problem: MatchesViewModel, SearchViewModel, and AllTimeLeadersViewModel all use lazy delegate for pagingFlow which may trigger multiple paging source recreations
- Files:
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/MatchesViewModel.kt` (lines 71-83)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/search/SearchViewModel.kt` (lines 55-68)
- Cause: flatMapLatest creates new Pager instances on every state change, potentially discarding cached pages
- Improvement path: Implement proper PagingSource invalidation strategy; cache Pager instance; use stateIn() properly

**Sorting on Full Dataset:**
- Problem: MatchDetailsViewModel sorts entire player lists in memory without pagination or caching
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsViewModel.kt` (lines 54-70)
- Cause: sortTeam() is called on full lists; no incremental updates or memoization
- Improvement path: Memoize sorting results; implement incremental sort updates; add index-based lookup

**Uncached Flow Emissions:**
- Problem: viewState flows emit constantly even when values don't change meaningfully
- Files: Multiple ViewModels update viewState without distinctUntilChanged() in all cases
- Cause: MutableStateFlow updates trigger collectors even for non-meaningful changes
- Improvement path: Apply distinctUntilChanged() to all public state flows; implement value-based equality for data classes

## Fragile Areas

**Pagination State Management:**
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/MatchesViewModel.kt`, `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/search/SearchViewModel.kt`
- Why fragile: pagingSource is mutable, lateinit, and shared between multiple coroutine scopes; invalidate() calls are scattered without synchronization
- Safe modification: Add thread safety guards; use Mutex for pagingSource access; consolidate invalidation logic into dedicated method
- Test coverage: No unit tests for pagination logic; no tests for edge cases (rapid tab switches, back navigation during loading)

**ViewModel Initialization with Dependencies:**
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/matchdetails/MatchDetailsViewModel.kt`, `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/App.kt` (koinInject with parametersOf)
- Why fragile: Koin dependency injection with parameters can fail silently; no validation that matchId parameter is valid
- Safe modification: Add parameter validation in ViewModel constructor; use factory pattern for ViewModel creation; add logging for DI failures
- Test coverage: No tests for invalid matchId scenarios; no tests for Koin injection failure handling

**Network Error Handling Chain:**
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt`, multiple ViewModels consuming Either<Failure, T>
- Why fragile: Error handling is inconsistent; some errors logged but not all; no retry strategy for transient failures
- Safe modification: Implement exponential backoff for network errors; add comprehensive error logging; distinguish between recoverable/unrecoverable errors
- Test coverage: No tests for error scenarios; no tests for specific Failure types; no integration tests for actual HTTP errors

**View State with Nullable Data:**
- Files: All ViewModels with `matchDetails: ViewStateData<MatchDetails?>`, `leagueDetails: LeagueDetails?`
- Why fragile: Multiple nullability layers (ViewStateData<T?>) make null checking error-prone
- Safe modification: Remove unnecessary inner nullability; use ViewStateData<MatchDetails> only; represent "no data" via separate state
- Test coverage: No tests for null states; no tests for loading -> null data transitions

## Scaling Limits

**Hard-Coded Page Size:**
- Current capacity: 15 items per page (matches, search results, all-time leaders)
- Limit: May be inefficient for different screen sizes or network conditions
- Scaling path: Make page size configurable; implement adaptive pagination based on device memory; add user preference for items per page
- Files:
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/MatchesViewModel.kt` (line 43)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/search/SearchViewModel.kt` (line 29)
  - `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/statistics/alltimeleaders/AllTimeLeadersViewModel.kt` (implied)

**In-Memory Sorting of Large Result Sets:**
- Current capacity: Full team/player stats loaded into memory for sorting
- Limit: Will struggle with 100+ player match stats
- Scaling path: Implement server-side sorting; add virtual scrolling for large lists; paginate stat results

**Single HttpClient Instance:**
- Current capacity: One shared HttpClient for all network requests
- Limit: No request-level configuration override; no circuit breaker pattern
- Scaling path: Implement request-level timeouts; add circuit breaker; use OkHttp interceptors for advanced features

## Dependencies at Risk

**Direct Throwable Usage:**
- Risk: `Either<Throwable, T>` pattern requires catching generic Throwable instead of specific Failure subtypes
- Impact: Type safety lost; can't pattern match on specific errors cleanly
- Migration plan: Replace with `Either<Failure, T>` throughout; update PagingSource interface to use Failure instead of Throwable
- Files: `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/main/matches/pagination/MatchesPagingSource.kt` (line 15), `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/base/BasePagingSource.kt` (line 36)

## Test Coverage Gaps

**Missing ViewModel Tests:**
- What's not tested: All ViewModel logic including state updates, error handling, data fetching
- Files: All ViewModels in `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/`
- Risk: Regression in data loading, state management, or error recovery could go undetected
- Priority: High - ViewModels are critical business logic

**Missing Repository/Service Tests:**
- What's not tested: NetworkMatchService, NetworkSeasonService, NetworkLeagueService, data transformation from DTO to domain models
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/Network*.kt`, `data/src/commonMain/kotlin/com/mzs/basket_krk/data/repository/`
- Risk: API contract changes or serialization issues not caught until runtime
- Priority: High - Data layer is critical for app functionality

**Missing Composable UI Tests:**
- What's not tested: All UI components and screens; layout, state rendering, user interactions
- Files: All screens in `presentation/src/commonMain/kotlin/com/mzs/basket_krk/presentation/screens/`
- Risk: UI crashes, layout issues, or state display bugs released to users
- Priority: Medium - Covered by manual testing but no automated tests

**Missing Serializer Tests:**
- What's not tested: SearchResultDtoSerializer handling of all type variants; edge cases in custom deserialization
- Files: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/serializer/SearchResultDtoSerializer.kt`
- Risk: Unknown API response types cause crashes in production
- Priority: High - Custom serialization is error-prone

**Missing Integration Tests:**
- What's not tested: End-to-end flows (select season -> load matches -> display details), error recovery scenarios, pagination across multiple pages
- Risk: Integration bugs between layers go unnoticed; user workflows fail in production
- Priority: Medium - Would catch most real-world issues

---

*Concerns audit: 2026-03-16*
