# Maven Test Runner MVP Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan with failing tests before production code. Track steps with checkbox (`- [ ]`) syntax.

**Goal:** Add a controlled Maven test runner that future task execution can use to verify patches.

**Architecture:** Place command execution under the `runner` module. The runner detects supported Maven layouts and executes only backend-constructed allowlisted commands. This phase exposes an internal Java service only; it does not edit files, invoke a model, push branches, or create Pull Requests.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, `ProcessBuilder`.

## Global Constraints

- Support only `./mvnw test` and `mvn test`.
- Prefer `./mvnw test` when a Maven wrapper exists.
- Never accept arbitrary command text from model output or HTTP input.
- Capture exit code and combined stdout/stderr output.
- Enforce a command timeout and return a structured timeout result.
- Keep repository operations inside the prepared repository directory.

## Target Files

Create:

- `PatchPilot/src/main/java/io/patchpilot/backend/runner/domain/vo/TestRunResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/runner/service/MavenTestRunner.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/runner/service/MavenTestRunnerTests.java`

Modify:

- `docs/progress/execution-log.md`
- `docs/plans/006-maven-test-runner-mvp.md`

## Implementation Tasks

### Task 1: Add Runner Contract And Detection Tests

- [x] Write tests for Maven wrapper preference, fallback to system Maven when only `pom.xml` exists, non-zero command exit capture, and unsupported repositories.
- [x] Run the targeted test and verify it fails because the runner does not exist.
- [x] Add `TestRunResult` and `MavenTestRunner`.
- [x] Run the targeted test and verify it passes.

### Task 2: Validate Full Backend

- [x] Run the targeted Maven runner test.
- [x] Run the full backend test suite.
- [x] Run the package build.
- [x] Rebuild and restart Docker compose.
- [x] Call `GET /api/tasks` as a runtime smoke check.

### Task 3: Update Documentation

- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark completed checkboxes in this plan.

## Acceptance Checklist

- [x] Maven wrapper repositories run `./mvnw test`.
- [x] Maven repositories without a wrapper run `mvn test`.
- [x] Unsupported repositories fail explicitly.
- [x] Non-zero test exits preserve exit code and output.
- [x] Timeout exits return a structured result instead of hanging indefinitely.
- [x] Full Maven and Docker verification passes.
