# 045 Task Metrics Test Pass Rate

## Goal

Extend the task metrics summary endpoint with test-run pass/fail statistics so operators can quickly see whether generated fixes are producing passing validation runs.

## Scope

- Reuse existing task and test-run records.
- Extend `GET /api/tasks/metrics/summary`.
- Count `exitCode == 0` test runs as passed and all other exit codes as failed.
- Return `0.0` pass rate when no test runs exist.

## Tasks

1. Add test-run fields to the metrics summary response:
   - `testRunCount`
   - `passedTestRunCount`
   - `failedTestRunCount`
   - `testPassRate`
2. Aggregate test runs through `FixTaskTestRunService`.
3. Cover zero-state, mixed pass/fail state, and controller JSON output.
4. Log validation results in `docs/progress/execution-log.md`.

## Acceptance Criteria

- `GET /api/tasks/metrics/summary` includes the new test-run fields.
- A successful test run increments passed count.
- A non-zero exit code increments failed count.
- Existing metrics fields remain backward compatible.
- Backend tests pass with `mvn -pl PatchPilot test`.
