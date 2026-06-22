# Gradle Language Adapter

## Goal

Support Java/Gradle repositories through the existing language-adapter and verification-runner pipeline. A Gradle repository should pass preflight, run a fixed allowlisted test command, and continue through the normal patch, verification, commit, push, and Pull Request flow.

## Scope

- Add `JavaGradleLanguageAdapter` for `gradlew`, `build.gradle`, and `build.gradle.kts` detection.
- Prefer `./gradlew test` when a Gradle wrapper exists.
- Fall back to `gradle test` when only Gradle build files exist.
- Register the Gradle adapter in Spring alongside the Maven adapter with deterministic ordering.
- Extend `CommandExecutionGuard` to allow only the fixed Gradle test commands.
- Update adapter, command-guard, verification-runner, Spring context, and product documentation tests.

## Non-Goals

- Add Node.js, Python, Docker, or arbitrary language execution.
- Run arbitrary Gradle tasks requested by users or the model.
- Add Gradle dependency caching, daemon tuning, or build-scan support.
- Change patch generation behavior.

## Validation Plan

- Verify Gradle wrapper and non-wrapper repositories are detected.
- Verify non-Gradle repositories are rejected by the Gradle adapter.
- Verify the command guard allows only `./gradlew test` and `gradle test`.
- Verify the generic verification runner can execute the Gradle wrapper command.
- Verify Spring registers both Java/Maven and Java/Gradle adapters.
- Run full backend tests and whitespace checks before merging.
