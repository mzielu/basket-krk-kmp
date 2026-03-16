# External Integrations

**Analysis Date:** 2026-03-16

## APIs & External Services

**Basketball Data API:**
- Service: Custom REST API for basketball league/tournament data
  - Base URL: `http://130.61.230.255:8000/`
  - SDK/Client: Ktor Client 3.3.3
  - Auth: Custom headers (tournament ID, OS type, app version)
  - Implementation: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/ApiService.kt`

**API Endpoints:**
- `/season/` - Fetch seasons information
- `/league/` - Fetch league information
- `/season/{seasonId}/rounds/` - Get rounds for a season
- `/season/{seasonId}/leagues/` - Get leagues for a season
- `/round/{roundId}/?page={page}` - Get matches in a round with pagination
- `/match/{matchId}` - Get match details
- `/league/{leagueId}/` - Get league details
- `/league/{leagueId}/avg_stats?cat={statOption}` - Get league average statistics
- `/stats/all_time?cat={category}&page={page}` - Get all-time statistics
- `/search/?filter={filter}&page={page}` - Search functionality

## Data Storage

**Databases:**
- Not detected - Application appears to be read-only API consumer

**File Storage:**
- Local filesystem only - No explicit cloud storage integration

**Caching:**
- Not explicitly configured - Likely in-memory via View Model state

## Authentication & Identity

**Auth Provider:**
- Custom - Token-less authentication via request headers

**Implementation:**
- `HttpClientFactory.kt` configures default headers:
  - `TRNMT`: Tournament type (default: "mba")
  - `OS`: Operating system type (default: "android")
  - `APP-VERSION`: Application version number (default: "100")
- No OAuth, API keys, or JWT tokens detected
- Location: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.kt`

## Monitoring & Observability

**Error Tracking:**
- Custom error handling via `Failure` domain models
- Error responses parsed from API: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/dto/ErrorDto.kt`

**Logs:**
- Kermit 2.0.8 for multiplatform logging
- Location: `presentation/build.gradle.kts` imports Kermit
- No centralized logging service configured

**Error Types Handled:**
- `no_data` - Throws `Failure.NoDataAvailableError`
- `old_version` - Throws `Failure.OldVersionError`
- Generic API errors mapped to `Failure.ApiError`
- Unknown HTTP errors mapped to `Failure.UnknownError`

## CI/CD & Deployment

**Hosting:**
- Custom backend API at `http://130.61.230.255:8000/` (IP-based, appears to be self-hosted)

**CI Pipeline:**
- Not detected - No GitHub Actions, GitLab CI, or Jenkins configuration found

**App Distribution:**
- Android: Via APK/AAB (ProGuard minified and resource-shrunk for release)
- iOS: Via Xcode framework compilation

## Network Configuration

**HTTP Configuration:**
- Ktor ContentNegotiation with Kotlinx JSON serialization
- JSON serialization ignores unknown keys (`ignoreUnknownKeys = true`)
- Default Content-Type: `application/json`

**Android Network Security:**
- Cleartext traffic permitted for: `130.61.230.255` only
- Config: `composeApp/src/androidMain/res/xml/network_security_config.xml`
- Allows HTTP (non-HTTPS) communication with the backend IP

**Platform-Specific HTTP Engines:**
- Android: OkHttp engine (`io.ktor:ktor-client-okhttp:3.3.3`)
- iOS: Darwin engine (`io.ktor:ktor-client-darwin:3.3.3`)
- Configuration: `data/src/androidMain/kotlin/com/mzs/basket_krk/data/service/HttpClientFactory.android.kt` and `.ios.kt`

## Dependency Injection

**Framework:** Koin 4.1.1

**Modules:**
- `ApiService` - HTTP client and API communication
- `HttpClientFactory` - Creates Ktor HttpClient instances
- Location: `data/src/commonMain/kotlin/com/mzs/basket_krk/data/service/`

**Integration Points:**
- Presentation layer injects `ApiService` into repositories
- UI layer injects ViewModels via Koin Compose integration

## Image Loading

**Framework:** Coil 3.3.0

**Integration:**
- Ktor network plugin for image fetching
- Package: `io.coil-kt.coil3:coil-network-ktor3:3.3.0`
- Handles image loading across Android and iOS

## Environment Configuration

**Required env vars:**
- None explicitly required - API URL and headers are hardcoded

**Hardcoded Configuration:**
- Base API URL: `http://130.61.230.255:8000/`
- Tournament type: "mba"
- OS: "android" (hardcoded but could be platform-specific)
- App version: "100"

**Future Configuration Candidates:**
- `BASE_URL` should be moved to environment variables for different build variants
- Tournament type could be configurable
- App version should be dynamic from build metadata

## Webhooks & Callbacks

**Incoming:**
- Not detected

**Outgoing:**
- Not detected

## Platform-Specific Notes

**Android:**
- Uses `local.properties` for SDK path configuration
- ProGuard obfuscation enabled in release builds
- Network security config restricts cleartext to specific IP

**iOS:**
- Framework compilation via Kotlin/Native
- Three build targets: `iosX64`, `iosArm64`, `iosSimulatorArm64`
- Frameworks: `MobileShared.framework` (presentation), `dataKit.framework` (data), `domainKit.framework` (domain)

---

*Integration audit: 2026-03-16*
