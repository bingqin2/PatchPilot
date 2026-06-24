# 169 - Task Retry Preflight

## Goal

Prevent blind retries from the dashboard or API when the latest task failure requires operator setup work before another execution attempt.

## Scope

- Add a backend retry-preflight read model for a single task.
- Reuse the same policy in `POST /api/tasks/{id}/retry` so the button and backend behavior cannot drift.
- Show retry readiness in the dashboard task detail panel.
- Disable retry when the preflight identifies a blocked category such as GitHub permission or unsupported repository failure.
- Keep the existing retry behavior for verification, model, workspace, patch-review, generic failed, and cancelled tasks.

## API Contract

`GET /api/tasks/{id}/retry-preflight` returns:

- `taskId`
- `status`
- `retryable`
- `category`
- `reason`
- `operatorAction`

Missing tasks return `404`. Blocked retry attempts return `409` from `POST /api/tasks/{id}/retry` with the same operator action.

## UI Contract

Failed and cancelled task details load retry preflight alongside the aggregate task detail. The detail panel shows:

- `Ready to retry` when the next attempt can be safely queued.
- `Retry blocked` when setup or repository support work is required first.
- The failure category, sanitized reason, and operator action.

## Validation

- Backend service and controller tests cover retryable verification failures, blocked GitHub failures, cancelled tasks, missing tasks, and `409` retry guard behavior.
- Frontend API, component, and App tests cover retry-preflight loading, display, and disabled retry action.
