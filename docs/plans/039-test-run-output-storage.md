# Test Run Output Storage Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Store normal Maven test output without MySQL `TEXT` overflow while still bounding extreme logs.

**Architecture:** Expand `fix_task_test_run.output` from `TEXT` to `MEDIUMTEXT` with a Flyway migration. Keep service-level truncation as a safety guard, but raise the test-run output limit so useful Maven logs are retained. Leave failure-reason and status-comment truncation unchanged.

**Tech Stack:** Java 17, Spring Boot 3.5.x, JUnit 5, AssertJ, MyBatis-Plus, MySQL 8.4, Flyway.

## Global Constraints

- Do not store secrets in test output.
- Keep task API response shape unchanged.
- Keep a bounded maximum output size to avoid unbounded memory and API payload growth.
- Do not change tool-call or timeline output limits in this phase.

---

## Tasks

- [x] Add a failing migration test for expanding `fix_task_test_run.output`.
- [x] Add failing service tests that preserve large-but-valid test output.
- [x] Add Flyway migration `V10__expand_fix_task_test_run_output.sql`.
- [x] Raise the test-run output truncation limit with a dedicated helper.
- [x] Run focused tests and the full backend test suite.
- [x] Record validation evidence in `docs/progress/execution-log.md`.

## Acceptance Criteria

- Existing databases migrate `fix_task_test_run.output` to `MEDIUMTEXT`.
- Test-run output around 120k characters is preserved.
- Extremely large test-run output is still truncated.
- Full backend tests pass.
