# 072 Dashboard Copy Task Link

## Goal

Make selected task detail URLs easy to share during local demos and debugging.

## Scope

- Add a `Copy link` action to the task detail header.
- Generate the link from the current dashboard URL and set the selected task as `taskId`.
- Preserve existing query parameters when adding or replacing `taskId`.
- Show a short success or failure message after the clipboard action.

## Non-Goals

- Add a dedicated task detail route.
- Add backend API changes.
- Add browser-side task creation or PR merge actions.

## Validation

- Add component tests for task link generation and clipboard copy behavior.
- Run the full frontend test suite.
- Run the frontend production build.
