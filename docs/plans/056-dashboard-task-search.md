# 056 Dashboard Task Search

## Goal

Add a local search control to the React dashboard task list so operators can quickly narrow the currently loaded task set.

## Scope

- Add a search input to the task list panel.
- Filter the currently loaded frontend task list by task id, repository, issue number, status, trigger comment, or failure reason.
- Keep status filters backed by the existing backend query parameters.
- Record backend search as future work; do not add `GET /api/tasks?query=...` in this phase.

## Tasks

1. Add frontend coverage for local task search.
2. Add search state and local filtering in `App.tsx`.
3. Render a task search input in `TaskListPanel`.
4. Document local search behavior and future backend search.
5. Record validation evidence.

## Acceptance Criteria

- Searching `broken` shows the failed task and hides the completed task from the current list.
- Search matches repository, issue number, status, trigger comment, failure reason, and task id.
- Search does not add a backend `query` parameter.
- The empty state distinguishes no local search matches from no backend status-filter results.
- `npm test` and `npm run build` pass under `frontend/`.
