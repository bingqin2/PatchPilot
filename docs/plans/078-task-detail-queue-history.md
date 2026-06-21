# 078 Task Detail Queue History

## Goal

Show the selected task's full queue history in the aggregate task detail response and dashboard detail panel, while keeping the existing latest queue status summary.

## Scope

- Extend the queue query service with `listByTaskId(String taskId)`.
- Return both `queueItem` and `queueItems` from `GET /api/tasks/{taskId}/detail`.
- Keep `queueItem` as the latest queue item for compatibility and compact summary display.
- Render a `Queue History` section in `TaskDetailPanel` with item id, status, attempt count, available time, locked time, and last error.
- Update API typing and dashboard tests for the new detail field.

## Non-Goals

- Change queue execution, retry, locking, or recovery behavior.
- Add queue mutation controls from the detail panel.
- Remove the global queue panel.

## Validation

- Add backend coverage for queue history inclusion in aggregate task detail.
- Add queue query service coverage for listing queue records by task id.
- Add frontend API and component coverage for task-detail queue history.
- Run backend tests.
- Run the frontend test suite and production build.
