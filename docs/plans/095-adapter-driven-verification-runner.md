# Adapter-Driven Verification Runner

## Goal

Decouple test execution from Maven-specific detection so future language adapters can supply their own allowlisted verification commands. The executor should detect the repository once, then pass the selected adapter command to a generic verification runner.

## Scope

- Add `VerificationRunner` for controlled process execution of adapter-provided verification commands.
- Keep `MavenTestRunner` as a compatibility wrapper for direct Java/Maven tests and older call sites.
- Change task execution to run the `LanguageDetectionResult.verificationCommand()` returned by the preflight.
- Preserve command allowlist enforcement, timeout behavior, process registration, environment sanitization, and test-run recording.
- Update executor, runner, webhook integration tests, and product documentation.

## Non-Goals

- Add Gradle, Node.js, Python, or Docker sandbox support.
- Widen the command allowlist beyond the current supported Maven commands.
- Remove `MavenTestRunner` entirely in this step.

## Validation Plan

- Verify `VerificationRunner` executes the adapter-supplied command and registers task processes.
- Verify task execution records the command returned by language detection instead of re-detecting Maven inside the runner.
- Verify existing Java/Maven runner tests still pass through the compatibility wrapper.
- Run full backend tests and whitespace checks before merging.
