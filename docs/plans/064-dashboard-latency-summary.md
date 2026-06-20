# Dashboard Latency Summary

## Goal

Show aggregate task and execution latency in the operations dashboard.

## Scope

- Add a backend metrics endpoint at `GET /api/tasks/metrics/latency`.
- Aggregate completed task duration, model call duration, tool call duration, and test-run duration.
- Return counts plus average and maximum duration fields in milliseconds.
- Add a compact frontend dashboard panel for latency.
- Keep the feature read-only and dashboard-focused.

## Backend Design

Add `FixTaskLatencySummaryVo` with:

- `completedTaskCount`
- `averageTaskDurationMs`
- `maxTaskDurationMs`
- `modelCallCount`
- `averageModelCallDurationMs`
- `maxModelCallDurationMs`
- `toolCallCount`
- `averageToolCallDurationMs`
- `maxToolCallDurationMs`
- `testRunCount`
- `averageTestRunDurationMs`
- `maxTestRunDurationMs`

Extend `FixTaskMetricsService` with `latency()`. `DefaultFixTaskMetricsService` should read all tasks, collect model calls through `FixTaskModelCallService`, tool calls through `FixTaskToolCallService`, and test runs through `FixTaskTestRunService`. Completed task duration uses `completedAt - createdAt`.

## Frontend Design

Add `getLatencySummary()` in `frontend/src/api.ts`, a matching TypeScript type, and a `LatencyPanel` component. Render it in the existing operational summary area beside failure causes and model usage.

## Testing

- Unit test `DefaultFixTaskMetricsService#latency()`.
- Controller test for `GET /api/tasks/metrics/latency`.
- Frontend API helper test for the new endpoint.
- Dashboard render test for `Latency`, task average, model average, tool average, and test average.

## Acceptance Criteria

- Empty records return zero counts and zero duration fields.
- Only completed tasks count toward task duration.
- Model, tool, and test-run durations use their recorded `durationMs` fields.
- Existing dashboard refresh flow loads the new endpoint.
