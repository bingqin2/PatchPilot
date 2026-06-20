# 053 Dashboard Task Timestamps

## Goal

Show task creation and update times in the React dashboard task list so operators can quickly distinguish fresh, stale, and recently changed tasks.

## Scope

- Use the existing `createdAt` and `updatedAt` fields already returned by task APIs.
- Render compact task row timestamps for each visible task.
- Keep status filters, issue links, status comment links, Pull Request links, cancel, retry, and queue rendering unchanged.
- Do not change backend APIs or persistence.

## Tasks

1. Add frontend test coverage for task row creation and update timestamps.
2. Render `Created` and `Updated` times in `TaskListPanel`.
3. Reuse the existing dashboard `compactTime()` formatter.
4. Add task timestamp styling that wraps safely on narrow rows.
5. Document dashboard timestamp visibility and record validation evidence.

## Acceptance Criteria

- Each task row shows a `Created` time derived from `createdAt`.
- Each task row shows an `Updated` time derived from `updatedAt`.
- Timestamp elements keep the original ISO value in `dateTime`.
- `npm test` and `npm run build` pass under `frontend/`.
