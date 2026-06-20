# Dashboard Task Deep Links

## Goal

Allow operators to open or share a dashboard URL that selects a specific task detail panel.

## Scope

- Add frontend-only support for `?taskId={id}`.
- Read `taskId` from the current URL during dashboard initialization.
- Preserve the selected task after refresh when the task is still present in the loaded page.
- Update the URL when an operator selects a task row.
- Do not add React Router, backend endpoints, or database fields.

## Frontend Design

Use the browser URL as a lightweight state carrier:

- `/?taskId=task-2` opens the dashboard with `task-2` selected when that task is in the loaded task page.
- Clicking a task row updates the URL with `history.replaceState`.
- Existing filters, search, metrics, queue, and configuration loading continue to use the same backend APIs.

If the URL task is not in the loaded task list, the dashboard falls back to the first visible task.

## Testing

- App test for selecting task detail from `?taskId=...`.
- App test for updating `window.location.search` after selecting a task row.
- Run the full frontend test suite and production build.

## Acceptance Criteria

- A copied dashboard URL can restore a selected task.
- Clicking a task row updates the URL without a page reload.
- Missing or stale `taskId` values do not break the default task selection.
