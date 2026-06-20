# 054 Dashboard Detail Empty States

## Goal

Make task detail panels explicit when a task has no timeline events, test runs, tool calls, or model calls so operators can distinguish "no records" from a loading or rendering problem.

## Scope

- Add empty-state text to task detail record sections.
- Only show empty states when detail loading has finished.
- Keep existing detail records unchanged when data exists.
- Do not change backend APIs, persistence, task filters, queue rendering, or task control actions.

## Tasks

1. Add frontend coverage for missing detail records.
2. Render an empty state when timeline events are absent.
3. Render an empty state when Maven test runs are absent.
4. Render empty states when tool calls or model calls are absent.
5. Document the behavior and record validation evidence.

## Acceptance Criteria

- Empty timeline data shows `No timeline events recorded.`
- Empty Maven test-run data shows `No Maven test runs recorded.`
- Empty tool-call data shows `No tool calls recorded.`
- Empty model-call data shows `No model calls recorded.`
- Existing populated task detail rendering remains unchanged.
- `npm test` and `npm run build` pass under `frontend/`.
