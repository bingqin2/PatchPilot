# Task Metrics Summary API Plan

**Goal:** Add a small read-only operational metrics API for the future dashboard and milestone 5 observability evidence.

**Scope:** This phase adds task-level aggregate metrics only. It does not add new database tables, frontend views, cost pricing, GitHub calls, or task execution changes.

## Tasks

- [x] Add regression coverage for empty task metrics.
- [x] Add regression coverage for status counts, completion/failure rates, completion duration, and model token aggregation.
- [x] Add `FixTaskMetricsSummaryVo` for API output.
- [x] Add `FixTaskMetricsService` and a default aggregation implementation using existing task and model-call services.
- [x] Expose `GET /api/tasks/metrics/summary`.
- [x] Document the API in README and record validation evidence.

## Acceptance Criteria

- Empty task history returns zero counts, zero rates, and zero averages.
- Mixed task history returns status counts for `PENDING`, `RUNNING`, `RUNNING_TESTS`, `COMPLETED`, `FAILED`, and `CANCELLED`.
- Completion and failure rates are computed against total task count.
- Average completion duration only uses completed tasks with `completedAt`.
- Token totals use existing model-call audit records.
