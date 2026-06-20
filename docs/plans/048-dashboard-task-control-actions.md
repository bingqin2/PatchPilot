# 048 Dashboard Task Control Actions

## Goal

Add read/write task control actions to the React dashboard so operators can cancel active tasks and retry failed or cancelled tasks from the task detail view.

## Scope

- Reuse existing backend endpoints:
  - `POST /api/tasks/{taskId}/cancel`
  - `POST /api/tasks/{taskId}/retry`
- Show `Cancel task` only for `PENDING`, `RUNNING`, and `RUNNING_TESTS` tasks.
- Show `Retry task` only for `FAILED` and `CANCELLED` tasks.
- Refresh dashboard data after a successful control action.
- Preserve backend error messages in the existing dashboard alert.

## Tasks

1. Add frontend tests for cancel and retry interactions.
2. Add typed POST API helpers for task control endpoints.
3. Render task detail action buttons based on task status.
4. Disable the active action button while the request is in flight.
5. Refresh task list, metrics, and selected task detail after successful actions.
6. Document the control actions and record validation evidence.

## Acceptance Criteria

- `Cancel task` calls `/api/tasks/{taskId}/cancel` with `POST`.
- `Retry task` calls `/api/tasks/{taskId}/retry` with `POST`.
- Terminal completed tasks do not show control buttons.
- Backend failures surface in the dashboard alert.
- `npm test` and `npm run build` pass under `frontend/`.
