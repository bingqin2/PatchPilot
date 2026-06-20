# 062 Dashboard Failure Cause Summary

## Goal

Show a compact failure-cause summary in the dashboard so operators can see why failed tasks are failing without opening each task.

## Scope

- Add a read-only backend API for failure cause counts.
- Classify existing `FixTaskVo.failureReason` text into stable cause keys.
- Render the summary in the React dashboard metrics area.
- Keep task persistence, queue behavior, retry behavior, and detailed task records unchanged.

## Cause Categories

- `MAVEN_TESTS`: Maven test failures or test command failures.
- `GITHUB_AUTH`: GitHub token, permission, 401, 403, or auth failures.
- `MODEL_ERROR`: model provider or OpenAI-compatible call failures.
- `SANDBOX_REJECTION`: command allowlist, sandbox, or rejected command failures.
- `UNKNOWN`: failed tasks without a recognized failure reason.

## Tasks

1. Add backend service and controller tests for failure cause summary.
2. Add `FixTaskFailureCauseSummaryVo`.
3. Extend `FixTaskMetricsService` with `failureCauses()`.
4. Implement text classification in `DefaultFixTaskMetricsService`.
5. Add frontend API types and helper for `/api/tasks/metrics/failure-causes`.
6. Render the failure cause summary in the dashboard.
7. Update README and execution log with behavior and validation evidence.

## Acceptance Criteria

- `GET /api/tasks/metrics/failure-causes` returns ordered cause counts for failed tasks.
- Non-failed tasks do not affect the summary.
- Empty task lists return an empty cause summary.
- Dashboard renders cause labels and counts from the backend.
- Backend tests, frontend tests, and frontend build pass.
