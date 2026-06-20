# 057 Task History Search Pagination Plan

## Goal

Define the next implementation step for backend-backed task search and pagination so the dashboard can move beyond local filtering of the first loaded task page.

## Current State

`GET /api/tasks` currently supports:

- `status`
- `repositoryOwner`
- `repositoryName`
- `limit`

The controller calls `fixTaskService.listTasks()` and applies filters in memory. The React dashboard calls `GET /api/tasks?limit=50` or `GET /api/tasks?limit=50&status=...`, then applies local search in the browser.

This is acceptable for the current small demo dataset, but it will not search full task history once the task table grows.

## Target API

Extend the existing endpoint instead of adding a new route:

```text
GET /api/tasks?query=demo&status=FAILED&repositoryOwner=bingqin2&repositoryName=PatchPilot&limit=50&offset=0
```

Query parameters:

- `query`: optional free-text search.
- `status`: optional `FixTaskStatus`.
- `repositoryOwner`: optional exact owner filter.
- `repositoryName`: optional exact repository filter.
- `limit`: optional page size, default `50`, valid range `1..100`.
- `offset`: optional page offset, default `0`, valid range `0..10000`.

Initial search fields:

- `id`
- `repository_owner`
- `repository_name`
- `issue_number`
- `trigger_user`
- `trigger_comment`
- `delivery_id`
- `failure_reason`
- `pull_request_url`

Sorting:

- Always sort by `created_at desc`.
- Add `id desc` as a deterministic tie breaker if needed.

## Response Shape

Keep the current list response compatible for one more phase:

```json
{
  "success": true,
  "data": [
    { "id": "task-id", "status": "COMPLETED" }
  ],
  "message": null
}
```

Do not introduce pagination metadata in the same phase as backend search. A later phase can add:

```json
{
  "items": [],
  "limit": 50,
  "offset": 0,
  "hasMore": true
}
```

Reason: keeping response shape stable lets backend search ship without breaking the existing dashboard.

## Backend Implementation Plan

1. Add `FixTaskListQuery` under `task/domain/bo`.
2. Add `listTasks(FixTaskListQuery query)` to `FixTaskService`.
3. Keep `listTasks()` as a default or compatibility method for metrics and older callers.
4. Move validation for `status`, `limit`, and `offset` into controller helper methods.
5. Implement query-aware filtering in `MyBatisFixTaskService` using `LambdaQueryWrapper`.
6. Keep `InMemoryFixTaskService` behavior equivalent for tests and non-MySQL profiles.
7. Update `TaskController` to pass a query object to the service instead of filtering all tasks in memory.

## MySQL Query Guidance

Use exact filters for status and repository fields:

```text
status = ?
repository_owner = ?
repository_name = ?
```

Use grouped `LIKE` filters for free text:

```text
id LIKE ?
OR repository_owner LIKE ?
OR repository_name LIKE ?
OR CAST(issue_number AS CHAR) LIKE ?
OR trigger_user LIKE ?
OR trigger_comment LIKE ?
OR delivery_id LIKE ?
OR failure_reason LIKE ?
OR pull_request_url LIKE ?
```

Escape `%` and `_` in user input before using `LIKE`.

Do not add a schema migration or full-text index in the first implementation. Add indexing only after real task volume shows a need.

## Frontend Upgrade Plan

After backend search exists:

1. Extend `frontend/src/api.ts#listTasks()` to accept `{ status, query, limit, offset }`.
2. Replace local-only search with backend search for submitted query changes.
3. Keep local search only as a temporary filter while a request is loading, if useful.
4. Add `Load more` or page controls after backend pagination metadata exists.

## Tests To Add Later

Backend controller tests:

- `GET /api/tasks?query=broken` returns matching trigger comment or failure reason.
- `GET /api/tasks?query=42` matches issue number.
- `GET /api/tasks?offset=1&limit=1` returns the second sorted task.
- Invalid `offset=-1` returns HTTP 400.
- Existing `status`, `repositoryOwner`, `repositoryName`, and `limit` behavior remains compatible.

Service tests:

- MyBatis query returns sorted, filtered tasks.
- In-memory service returns the same logical results as MyBatis for query, status, limit, and offset.
- Search input escapes `%` and `_` instead of treating them as wildcards.

Frontend tests:

- Dashboard sends `query` to `GET /api/tasks` after search input changes.
- Dashboard preserves status filter and query together.
- Empty state distinguishes backend-filtered empty results from request failure.

## Acceptance Criteria For Future Implementation

- `GET /api/tasks` remains backward compatible with existing frontend calls.
- Backend query supports `query`, `status`, `repositoryOwner`, `repositoryName`, `limit`, and `offset`.
- Filtering is performed before limiting.
- Results are sorted newest first.
- Invalid query parameters return clear HTTP 400 messages.
- Frontend no longer depends on local-only search for full task history.
