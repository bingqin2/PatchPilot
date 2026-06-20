# 055 Dashboard Call Durations

## Goal

Make task detail audit records more useful by showing how long recorded tool calls and model calls took.

## Scope

- Display existing `durationMs` values for tool calls in the task detail panel.
- Display existing `durationMs` values for model calls in the task detail panel.
- Reuse the existing dashboard duration formatter.
- Do not change backend APIs, audit persistence, queue behavior, task controls, or dashboard data loading.

## Tasks

1. Add frontend coverage for tool-call and model-call durations.
2. Render tool-call duration next to the success/failure state.
3. Render model-call duration next to the token count.
4. Document the behavior and record validation evidence.

## Acceptance Criteria

- Tool calls render state and duration, for example `success · 1.0s`.
- Model calls render token count and duration, for example `1800 tokens · 2.0s`.
- Existing task detail sections still render unchanged apart from the added duration text.
- `npm test` and `npm run build` pass under `frontend/`.
