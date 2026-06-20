# 059 Dashboard Backend Search Pagination

## Goal

Wire the React dashboard task list to backend-backed task search and offset pagination.

## Scope

- Send `query` from the dashboard search input to `GET /api/tasks`.
- Preserve status filters while searching.
- Stop relying on browser-only task filtering for full task history.
- Add a `Load more` control backed by `offset`.
- Keep the existing backend list response shape; do not require pagination metadata yet.

## Tasks

1. Add frontend coverage for backend search query requests.
2. Replace local task filtering in `App.tsx` with backend query parameters.
3. Add frontend coverage for status plus query requests.
4. Add a `Load more` task-list control using `offset=tasks.length`.
5. Document the new dashboard behavior and validation evidence.

## Acceptance Criteria

- Typing in `Search tasks` causes the dashboard to request `/api/tasks?limit=50&query=...`.
- Combining search with a non-`ALL` status filter requests both `query` and `status`.
- The task list displays backend-filtered results rather than locally filtering the first loaded page.
- `Load more tasks` requests the next page using `offset`.
- Frontend tests and production build pass.
