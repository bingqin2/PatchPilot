# Demo Handoff Webhook Delivery Check

## Goal

Make the final demo handoff package explicitly prove that a recent GitHub
webhook delivery reached PatchPilot and created task-backed work. The previous
session report showed a delivery trail, but the handoff readiness summary could
still be `READY` without naming webhook delivery evidence as one of the required
handoff checks.

## Scope

- Add a `Webhook delivery evidence` row to demo handoff readiness.
- Mark the row `READY` when recent delivery diagnostics include a
  `TASK_CREATED` delivery.
- Mark the row `NEEDS_ATTENTION` when no recent delivery evidence exists or the
  latest delivery was intentionally ignored/rejected without a task-created
  delivery in the trail.
- Mark the row `BLOCKED` when the latest non-task delivery recommends
  redelivery, such as invalid signature, bad request, or backend processing
  failure.
- Update handoff package summary text and long-lived product documentation.

## Out of Scope

- Calling GitHub's webhook delivery API.
- Redelivering webhook events.
- Creating tasks, queue items, archives, or new delivery diagnostics from report
  generation.
- Changing dashboard layout; the current dashboard already copies/downloads the
  generated handoff package.

## Validation

- Focused backend tests cover `READY`, `NEEDS_ATTENTION`, and `BLOCKED` handoff
  readiness output.
- Full backend regression, frontend regression, production frontend build, and
  whitespace checks pass before merge.
