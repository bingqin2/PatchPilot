# 061 Task List Total Count

## Goal

Return total matching task count from `GET /api/tasks` and show that count in the dashboard task list header.

## Scope

- Add `total` to the task page response object.
- Count all matching tasks before `limit` and `offset` are applied.
- Keep `items`, `limit`, `offset`, and `hasMore` behavior unchanged.
- Update the React dashboard to render loaded count versus total matching count.
- Do not add page numbers, sorting controls, or task creation UI.

## Tasks

1. Add backend controller coverage for `data.total`.
2. Add service coverage for count behavior in memory and MyBatis-backed task services.
3. Add a `countTasks(FixTaskListQuery query)` service method.
4. Implement equivalent count filtering in `InMemoryFixTaskService` and `MyBatisFixTaskService`.
5. Add `total` to `FixTaskPageVo`, frontend `FixTaskPage`, API helper tests, and dashboard task-list props.
6. Document the API shape and validation evidence.

## Acceptance Criteria

- `GET /api/tasks?limit=1&offset=0` returns `data.total == 2` when two matching tasks exist.
- `total` is unaffected by `limit` and `offset`.
- Existing invalid `limit`, `offset`, and `status` errors remain unchanged.
- Dashboard shows loaded count and total count for the current backend filter.
- Backend tests, frontend tests, and frontend build pass.
