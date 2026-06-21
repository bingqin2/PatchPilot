# 077 Task Detail Queue Status

## Goal

Show the selected task's queue state inside task detail so operators can inspect scheduling, retries, and worker failures without switching to the global queue panel.

## Scope

- Extend the queue query service with `findByTaskId(String taskId)`.
- Include the latest queue item in `GET /api/tasks/{taskId}/detail`.
- Keep the existing queue summary and queue item list APIs unchanged.
- Add frontend typing for the optional queue item in aggregate task detail.
- Render queue status, attempt count, last error, available time, and locked time in `TaskDetailPanel`.

## Non-Goals

- Change queue execution, retry policy, or persistence schema.
- Add queue mutations from the detail panel.
- Remove the global queue panel.

## Validation

- Add backend coverage for queue item inclusion in aggregate task detail.
- Add queue query service coverage for finding the latest item by task id.
- Add frontend API and component coverage for task-detail queue state.
- Run backend tests.
- Run the full frontend test suite and production build.
