# 052 Dashboard Status Comment Links

## Goal

Expose PatchPilot's GitHub status comment links in the React dashboard so operators can jump directly to the comment that the backend edits during task execution.

## Scope

- Use the existing `statusCommentUrl` field already returned by task APIs.
- Render `Status Comment` links in visible task rows when the field is present.
- Render `Status Comment` in the selected task detail actions when the field is present.
- Keep issue links, Pull Request links, filters, queue rendering, cancel, and retry behavior unchanged.
- Do not change backend APIs or persistence.

## Tasks

1. Add frontend test coverage for task status comment links.
2. Render optional status comment links in `TaskListPanel`.
3. Render optional status comment links in `TaskDetailPanel`.
4. Document the dashboard status comment link behavior and record validation evidence.

## Acceptance Criteria

- Tasks with `statusCommentUrl` show a `Status Comment` link in the task row.
- The selected task detail shows a `Status Comment` link when available.
- Tasks without `statusCommentUrl` do not show a placeholder link.
- `npm test` and `npm run build` pass under `frontend/`.
