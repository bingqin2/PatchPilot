# 058 Backend Task Search Pagination

## Goal

Implement backend-backed task search and offset pagination for `GET /api/tasks` while preserving the current list response shape.

## Scope

- Add `query` and `offset` support to `GET /api/tasks`.
- Keep existing `status`, `repositoryOwner`, `repositoryName`, and `limit` behavior compatible.
- Move list filtering into `FixTaskService` through a query object.
- Implement equivalent MyBatis and in-memory query behavior.
- Extend the frontend task API helper to support future `{ query, limit, offset }` calls without changing dashboard UI behavior in this phase.
- Do not introduce pagination metadata yet.

## Tasks

1. Add controller coverage for `query`, `offset`, and invalid offset.
2. Add `FixTaskListQuery` as the task-list query boundary.
3. Add query-aware `listTasks(FixTaskListQuery)` service behavior.
4. Implement in-memory query filtering for default profile tests.
5. Implement MyBatis query filtering with exact filters, grouped text search, sorting, and offset/limit.
6. Extend `frontend/src/api.ts#listTasks()` options for future backend-search UI work.
7. Document behavior and validation evidence.

## Acceptance Criteria

- `GET /api/tasks?query=search&status=FAILED&limit=1&offset=1` filters before limiting and returns the expected second matching task.
- `GET /api/tasks?offset=-1` returns HTTP 400 with `offset must be between 0 and 10000`.
- `GET /api/tasks` remains backward compatible with existing list consumers.
- MyBatis and in-memory services support equivalent query semantics.
- Existing frontend dashboard calls still request `GET /api/tasks?limit=50` and status-specific variants.
- Backend and frontend tests pass.
