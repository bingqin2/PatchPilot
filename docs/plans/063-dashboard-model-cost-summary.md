# Dashboard Model Cost Summary

## Goal

Show aggregate model usage and estimated cost in the operations dashboard.

## Scope

- Add a backend metrics endpoint at `GET /api/tasks/metrics/model-usage`.
- Aggregate model call records across current task history.
- Return prompt tokens, completion tokens, total tokens, successful calls, failed calls, and estimated USD cost.
- Add a compact frontend dashboard panel for model usage.
- Keep model pricing configurable and default to zero cost.

## Backend Design

Add `FixTaskModelUsageSummaryVo` with:

- `totalPromptTokens`
- `totalCompletionTokens`
- `totalTokens`
- `successfulCalls`
- `failedCalls`
- `estimatedCostUsd`

Extend `FixTaskMetricsService` with `modelUsage()`. `DefaultFixTaskMetricsService` should read all tasks from `FixTaskService`, collect their model calls through `FixTaskModelCallService`, and compute totals. Cost uses `AgentProperties`:

- `patchpilot.agent.cost.prompt-token-usd`
- `patchpilot.agent.cost.completion-token-usd`

Both default to `0`.

## Frontend Design

Add `getModelUsageSummary()` in `frontend/src/api.ts`, a matching TypeScript type, and a `ModelUsagePanel` component. Render it near failure causes because both explain operational health.

## Testing

- Unit test `DefaultFixTaskMetricsService#modelUsage()`.
- Controller test for `GET /api/tasks/metrics/model-usage`.
- Frontend API helper test for the new endpoint.
- Dashboard render test for `Model usage`, token totals, call counts, and estimated cost.

## Acceptance Criteria

- Empty model calls return zero totals and zero cost.
- Failed model calls count toward token totals and failed call count.
- `estimatedCostUsd` is deterministic from configured token prices.
- Existing dashboard refresh flow loads the new endpoint.
