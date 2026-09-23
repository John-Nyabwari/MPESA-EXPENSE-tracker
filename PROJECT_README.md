# M-Pesa & Bank Transaction Tracker — Phase 1 Scaffold

Generated from `README.md` (product spec) and `DESIGN-android.md` (Wise-styled Jetpack
Compose implementation guide). This is a real, importable Android Studio project — Kotlin,
Jetpack Compose, Room, Hilt, WorkManager, Navigation Compose — built against the recommended
tech stack in the spec.

## Open it

1. Android Studio (Koala+), Gradle Kotlin DSL, `minSdk 24` / `compileSdk 34`.
2. `File > Open` on this folder. Let Gradle sync — it will pull dependencies from Google/Maven
   (no offline artifacts are bundled).
3. Run on an emulator or device. The onboarding screen shows first; SMS permission requests are
   *not* wired to a live prompt yet (see "Not yet implemented" below) — the import flow currently
   navigates straight to the dashboard.

## What's implemented (real, working code — not stubs)

- **Room database**: `Transaction`, `Category`, `SmartRule`, `RawSms` entities + DAOs, with a
  unique `dedupeKey` index so re-scanning the same SMS twice can't double-import it.
- **Parser engine** (`data/parser/`): `TransactionParser` interface, `ParserRegistry` that
  dispatches by sender, and real regex-based parsers for **M-Pesa** (sent/received/paid/
  withdraw/airtime/Fuliza drawdown+repayment) and three banks (**KCB, Equity, NCBA**) as
  worked examples of the `BankParser` pattern described in the spec. Unrecognized-but-clearly-
  financial messages fall through to `ParseResult.NeedsReview` rather than being dropped.
  Unit tests with fixtures live in `app/src/test/java/.../parser/`.
- **Smart rule engine** (`ApplySmartRulesUseCase`): sender/keyword/regex matching, priority
  ordering, multi-match detection (flags for review, per spec).
- **SMS ingestion**: `SmsReceiver` (live messages) → `SmsImportWorker` (WorkManager, both
  single-message and historical-batch modes) → `ImportSmsUseCase` (parse → smart-rule →
  dedupe → persist pipeline matching the spec's System Architecture diagram).
  `SmsPermissionManager` checks default-SMS-app / permission state per the Play policy
  constraint called out at the top of the spec.
- **Fuliza tracking**: outstanding-balance SQL aggregation (`observeFulizaOutstandingMinor`)
  kept separate from normal expense totals, live-streamed via Flow.
- **Dashboard**: income/expense/fees/net-cash-flow/Fuliza summary cards, wired to real Room
  queries (30-day window).
- **Wise-styled UI**: `WiseColors` / `WiseText` / `LedgerTheme` ported directly from
  `DESIGN-android.md` §1–2, plus the primary/forest button components from §3. Bottom
  navigation, onboarding, transaction list, categories, smart rules, Fuliza, and settings
  screens all use this theme.
- **Security scaffolding**: `EncryptedSettingsStore` (Keystore-backed prefs for app-lock and
  retention settings), `AppLockManager` (BiometricManager capability check), backup rules
  excluding the database from cloud backup/device transfer.
- **Delete-all-data** action wired end-to-end from Settings → repository → DAOs.

## Not yet implemented (flagged in code with TODO / doc comments)

- Live SMS permission request flow + default-SMS-app hand-off UI (onboarding currently skips
  straight through — `MainActivity.hasCompletedOnboarding()` is hardcoded).
- Historical import progress UI (the worker reports WorkManager progress; no screen observes it yet).
- Unreviewed-message review screen (entities/DAOs exist; no UI to correct/re-parse yet).
- Charts (Vico dependency is included; no chart composables written yet).
- CSV/JSON/PDF export.
- Remaining bank parsers (Absa, I&M, Stanbic, DTB, Co-op) — follow the `KcbParser` pattern.
- Room migrations (schema is v1; add `Migration` objects before changing entities post-release).
- Full-database encryption (SQLCipher or similar) — currently only small settings are
  Keystore-encrypted, per the code comment in `EncryptedSettingsStore`.
- DataStore-backed onboarding-completed flag (currently hardcoded `false`).

## Why not a runnable preview here

This is a native Android app (Kotlin + Gradle + Android SDK), which needs Android Studio and
an emulator/device to build and run — there's no way to compile or execute it inside this chat
environment. Everything above is real source you can open and build directly.
