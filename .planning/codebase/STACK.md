# Technology Stack

**Analysis Date:** 2026-03-16

## Languages

**Primary:**
- Kotlin 2.2.21 - Multiplatform mobile and shared code

**Secondary:**
- XML - Android configuration and resources

## Runtime

**Environment:**
- Android Runtime (API 24-36) - Android 6.0+
- iOS Runtime (ARM64, Simulator) - iOS app compilation via Kotlin/Native

**Package Manager:**
- Gradle 8.x with Gradle Wrapper
- Lockfile: Implicit via Gradle dependency resolution

## Frameworks

**Core:**
- Jetbrains Compose Multiplatform 1.9.3 - UI framework for Android and iOS
- Kotlin Multiplatform (KMP) 2.2.21 - Cross-platform code sharing

**HTTP & Networking:**
- Ktor Client 3.3.3 - HTTP client with cross-platform support
  - OkHttp engine (Android)
  - Darwin engine (iOS)

**Dependency Injection:**
- Koin 4.1.1 - Service locator and DI framework

**Functional Programming:**
- Arrow 2.2.0 - Functional programming library for error handling

**Image Loading:**
- Coil 3.3.0 - Image loading library for Compose
- Coil Ktor integration - Network image loading

**Navigation:**
- Jetbrains Navigation Compose 2.9.1 - Type-safe navigation

**Date & Time:**
- Kotlinx Datetime 0.7.1 - Platform-independent date/time

**Logging:**
- Kermit 2.0.8 - Multiplatform logging

**Pagination:**
- AndroidX Paging 3.4.0-beta01 - Paging library for lists

**UI Components:**
- Material Icons Extended 1.7.3 - Material Design icons
- AndroidX Lifecycle Compose 2.9.6 - ViewModel integration
- AndroidX Activity Compose 1.12.0 - Activity integration for Android

**Testing:**
- Kotlin Test Framework - Multiplatform testing
- JUnit 4.13.2 - Unit testing framework
- AndroidX Test Espresso 3.7.0 - Android UI testing
- AndroidX Test Ext JUnit 1.3.0 - JUnit extensions

## Key Dependencies

**Critical:**
- Ktor Client Core 3.3.3 - HTTP requests and API communication
- Kotlin Stdlib 2.2.21 - Core Kotlin runtime

**Infrastructure:**
- AndroidX Core KTX 1.17.0 - Android utility functions
- AndroidX AppCompat 1.7.1 - Android backward compatibility

## Configuration

**Environment:**
- Gradle properties: `gradle.properties` configures JVM heap and Kotlin style
- Android SDK: Min API 24, Target API 36, Compile SDK 36
- Kotlin code style: Official style enforced

**Build:**
- Root config: `build.gradle.kts`
- Module-specific: `composeApp/build.gradle.kts`, `data/build.gradle.kts`, `domain/build.gradle.kts`, `presentation/build.gradle.kts`, `shared/build.gradle.kts`, `data-utils/build.gradle.kts`
- Version management: `gradle/libs.versions.toml`
- Plugin management: Centralized via `settings.gradle.kts`

**Build Types:**
- Debug: Debuggable, includes debug suffix
- Release: Minified with ProGuard, resource shrinking enabled

## Platform Requirements

**Development:**
- Kotlin 2.2.21
- Android SDK (API 24 minimum)
- Gradle 8.x
- JVM 11+ (target compatibility)
- Xcode for iOS development
- macOS for iOS builds

**Production:**
- Android: Targets API 24-36 devices
- iOS: ARM64 and Simulator support (iosArm64, iosSimulatorArm64)

## Module Architecture

**composeApp** - Main Android application entry point
- `MainActivity` - Launches the app
- `BasketKrkApplication` - Android application class
- Targets: Android only

**shared** - Shared iOS framework
- Compiles presentation and data modules
- Targets: iosArm64, iosSimulatorArm64

**presentation** - UI and ViewModels
- Compose UI components
- Navigation logic
- Targets: Android, iOS, common

**data** - Network and data access layer
- Ktor HTTP client and API service
- DTOs for serialization
- Repository implementations
- Targets: Android, iOS with platform-specific HTTP engines

**domain** - Business logic and models
- Use cases
- Domain models
- Targets: Common (all platforms)

**data-utils** - Utility functions for data layer
- Common utilities
- Targets: Android, iOS, common

---

*Stack analysis: 2026-03-16*
