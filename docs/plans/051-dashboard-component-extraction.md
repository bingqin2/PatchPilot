# 051 Dashboard Component Extraction

## Goal

Split the React dashboard into focused modules so `App.tsx` remains the data-loading and action coordinator instead of owning all dashboard rendering.

## Scope

- Extract dashboard presentation components from `frontend/src/App.tsx`.
- Keep API calls, state transitions, status filters, issue links, Pull Request links, cancel, retry, and queue rendering behavior unchanged.
- Keep existing CSS class names unchanged.
- Add small dashboard helpers for shared formatting and task-detail state.
- Do not change backend APIs or frontend test expectations.

## Tasks

1. Use the existing dashboard test suite as a refactor safety net.
2. Extract metrics, task list, task detail, queue, summary, and record-line UI components.
3. Move shared dashboard formatting helpers out of `App.tsx`.
4. Keep `App.tsx` focused on fetching data, selecting tasks, and dispatching task actions.
5. Document the component boundary and record validation evidence.

## Acceptance Criteria

- `frontend/src/App.tsx` no longer contains the task list, task detail, queue, metric card, summary item, or record-line implementations.
- Dashboard behavior remains covered by the existing six frontend tests.
- `npm test` and `npm run build` pass under `frontend/`.
- README points maintainers to the extracted dashboard component directory.
