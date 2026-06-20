# Dashboard Queue Health

## Goal

Make queue problems visible at a glance in the operations dashboard without requiring operators to inspect every queue row.

## Scope

- Keep using existing `/api/task-queue/summary` and `/api/task-queue/items`.
- Add frontend-only queue health evaluation in `QueuePanel`.
- Keep the panel read-only.
- Do not add backend endpoints or new database fields.

## Health Rules

- `Queue has failures` when `failedCount > 0`.
- `Queue delayed` when there are delayed pending items and no failed items.
- `Queue active` when there are running items and no failed or delayed items.
- `Queue idle` when there are no failed, delayed, or running items.

Health details should include non-zero failed, delayed, and running counts.

## Frontend Design

Render a compact health strip below the queue panel header and above the summary cards. Use terse labels:

- `1 failed item`
- `2 delayed items`
- `1 running item`
- `No queue items need attention.`

Failed state has the highest priority, delayed is advisory, running is active, and idle is healthy.

## Testing

- Component tests for idle, active, delayed, and failed priority states.
- Dashboard render test for a queue with failed, running, and delayed items.

## Acceptance Criteria

- Failed queue state is visible before reading queue rows.
- Delayed and running queue state are visible in the same panel.
- Existing queue summary and item list remain unchanged.
