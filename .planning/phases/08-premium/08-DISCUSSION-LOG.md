# Phase 8: Premium - Discussion Log

> **Audit trail only.** Do not use as input to planning, research, or execution agents.
> Decisions are captured in CONTEXT.md — this log preserves the alternatives considered.

**Date:** 2026-03-24
**Phase:** 08-premium
**Areas discussed:** IAP library approach, Premium screen layout, All-Time Leaders gating, Premium state management

---

## IAP Library Approach

| Option | Description | Selected |
|--------|-------------|----------|
| expect/actual with native APIs | BillingClient (Android) + StoreKit 2 (iOS). Full control, no third-party dependency. | ✓ |
| KMP wrapper library | Community KMP billing library. Less boilerplate but adds dependency. | |
| RevenueCat SDK | Handles receipt validation, analytics. Adds external service dependency. | |

**User's choice:** expect/actual with native APIs
**Notes:** Matches existing KMP patterns (TournamentProvider, HttpClientFactory)

### Product Type

| Option | Description | Selected |
|--------|-------------|----------|
| Subscription (match Flutter) | Recurring subscription with period display | ✓ |
| One-time purchase (lifetime) | Single purchase grants premium forever | |

**User's choice:** Subscription (match Flutter)

### Receipt Validation

| Option | Description | Selected |
|--------|-------------|----------|
| Local only (match Flutter) | Trust BillingClient/StoreKit purchase status directly | ✓ |
| Server-side validation | Send receipt to backend for verification | |

**User's choice:** Local only (match Flutter)

### StoreKit Version

| Option | Description | Selected |
|--------|-------------|----------|
| StoreKit 2 | Modern Swift async/await API, requires iOS 15+ | ✓ |
| StoreKit 1 (Original) | Supports older iOS, callback-based | |

**User's choice:** StoreKit 2

### Platform Boundary

| Option | Description | Selected |
|--------|-------------|----------|
| Domain interface + Data expect/actual | Interface in domain, implementation in data layer androidMain/iosMain | ✓ |
| Presentation expect/actual | Billing logic in presentation layer | |

**User's choice:** Domain interface + Data expect/actual

### Product IDs

| Option | Description | Selected |
|--------|-------------|----------|
| Same product ID for both | Single 'premium' ID for debug and release | ✓ |

**User's choice:** Same product ID — match Flutter's single `'premium'` ID
**Notes:** User initially mentioned wanting 2 debug/production keys but clarified this was about AdMob APPLICATION_ID (deferred to ads phase), not IAP product IDs.

---

## Premium Screen Layout

| Option | Description | Selected |
|--------|-------------|----------|
| 1:1 Flutter match | Same card with two states, price/period, platform-specific legal text | ✓ |
| Simplified version | Subscribe/active card without legal text | |

**User's choice:** 1:1 Flutter match

### Price Display

| Option | Description | Selected |
|--------|-------------|----------|
| Match Flutter price/period | Platform-specific extraction with basePlanId/subscriptionPeriod parsing | ✓ |
| Price only, no period | Just show price string from store SDK | |

**User's choice:** Match Flutter price/period

### String Resources

**User's choice:** Claude checked — all strings already exist in EN + PL. No additions needed.

### Upsell Dialog

| Option | Description | Selected |
|--------|-------------|----------|
| Include (match Flutter) | Implement premium_dialog_* upsell dialog | |
| Skip for now | Focus on Premium screen and gating only | ✓ |

**User's choice:** Skip for now — deferred to future enhancement

---

## All-Time Leaders Gating

| Option | Description | Selected |
|--------|-------------|----------|
| PagingSource intercept | Check premium in PagingSource before page 3+ load | ✓ |
| UI-level gate | Let Paging3 load normally, gate in LazyColumn | |

**User's choice:** PagingSource intercept

### Navigation Action

| Option | Description | Selected |
|--------|-------------|----------|
| Navigate to Premium screen (match Flutter) | Tap premium indicator → Screen.Premium | ✓ |

**User's choice:** Navigate to Premium screen (match Flutter)

---

## Premium State Management

### Default State

| Option | Description | Selected |
|--------|-------------|----------|
| Match Flutter (generous default) | Premium=true until restorePurchases completes | ✓ |
| Default to false (strict) | Locked until restore confirms subscription | |

**User's choice:** Match Flutter (generous default)

### Observation Mechanism

| Option | Description | Selected |
|--------|-------------|----------|
| StateFlow<Boolean> | MutableStateFlow in service, exposed as StateFlow | ✓ |
| SharedFlow events | MutableSharedFlow for state changes | |

**User's choice:** StateFlow<Boolean>

### Initialization Timing

| Option | Description | Selected |
|--------|-------------|----------|
| Eager (match Flutter) | Initialize at app startup via Koin singleton | ✓ |
| Lazy (on first use) | Initialize when first accessed | |

**User's choice:** Eager (match Flutter)

---

## Claude's Discretion

- expect/actual class structure details
- Koin DI wiring specifics
- PremiumViewModel internal state management
- Premium state injection into PagingSource
- Error handling UI design
- Platform-specific legal text rendering approach

## Deferred Ideas

- Premium upsell dialog (premium_dialog_* strings) — future enhancement
- Debug/release AdMob APPLICATION_ID variant — ads/monetization phase
- Ad banner integration — separate monetization phase
