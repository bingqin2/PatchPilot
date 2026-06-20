# Task List Filter API Plan

**Goal:** Make the task list API more useful for dashboard and operator workflows by supporting simple read-only filters.

**Scope:** This phase only extends `GET /api/tasks`. It does not add database tables, pagination metadata, sorting options, queue behavior, GitHub calls, or task execution changes.

## Tasks

- [x] Add controller tests for filtering by task status and repository.
- [x] Add controller tests for limiting result count.
- [x] Add controller tests for invalid status and invalid limit responses.
- [x] Implement query-parameter filtering in the existing task list endpoint.
- [x] Document examples in README.
- [x] Record validation evidence in `docs/progress/execution-log.md`.

## Acceptance Criteria

- `GET /api/tasks` remains backward compatible and returns all tasks by default.
- `status` filters by `FixTaskStatus`.
- `repositoryOwner` and `repositoryName` filter exact repository coordinates.
- `limit` restricts returned item count and must be between `1` and `100`.
- Invalid `status` or `limit` returns HTTP `400` with an `ApiResponse.fail(...)` message.
