# 060 Task List Pagination Metadata

## Goal

Return explicit pagination metadata from the task list API and make the dashboard use `hasMore` instead of guessing from page size.

## Scope

- Change `GET /api/tasks` response data from a plain task array to a page object.
- Include `items`, `limit`, `offset`, and `hasMore`.
- Compute `hasMore` by internally fetching one extra task beyond the requested limit.
- Update the frontend API helper and dashboard pagination state.
- Keep task detail, metrics, queue, control, and audit endpoints unchanged.

## Tasks

1. Add backend controller coverage for `items`, `limit`, `offset`, and `hasMore`.
2. Add a `FixTaskPageVo` response object.
3. Update `TaskController` to return a page object and trim the extra task.
4. Update frontend types and `listTasks()` to return the page object.
5. Update dashboard refresh and load-more behavior to use `page.items` and `page.hasMore`.
6. Document the API shape and validation evidence.

## Acceptance Criteria

- `GET /api/tasks?limit=1&offset=0` returns `data.items.length() == 1`, `data.limit == 1`, `data.offset == 0`, and `data.hasMore == true` when more matching tasks exist.
- Invalid `limit`, `offset`, and `status` errors remain unchanged.
- Dashboard initial load and `Load more tasks` use the backend `hasMore` flag.
- Frontend tests, frontend build, and backend tests pass.
