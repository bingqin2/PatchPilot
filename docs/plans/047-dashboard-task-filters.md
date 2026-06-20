# 047 Dashboard Task Filters

## Goal

Add status filters to the React operations dashboard so operators can inspect active, completed, failed, and cancelled task groups without scanning the full task list.

## Scope

- Reuse the existing `GET /api/tasks` query parameters.
- Add frontend filters for `ALL`, `PENDING`, `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, `FAILED`, and `CANCELLED`.
- Preserve the current selected task only when it remains visible after filtering.
- Keep the dashboard read-only; do not add task creation, login, or admin controls.

## Tasks

1. Extend the frontend task API helper with an optional status filter.
2. Add task status filter controls above the task list.
3. Reset the selected task when the filtered result no longer contains it.
4. Show a clear empty state for filtered lists.
5. Cover status filtering with Vitest and Testing Library.

## Acceptance Criteria

- `ALL` loads `/api/tasks?limit=50`.
- Status filters load `/api/tasks?limit=50&status={STATUS}`.
- Selecting `FAILED` shows only failed tasks returned by the backend.
- Empty filtered results show `No {STATUS} tasks found.`
- `npm test` and `npm run build` pass under `frontend/`.
