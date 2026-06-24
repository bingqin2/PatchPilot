# 170 - Task Retry Reason Audit

## Goal

Require an explicit operator reason whenever a failed or cancelled task is retried, then preserve that reason in task state, timeline evidence, copied reports, and dashboard detail.

This makes task retry an auditable recovery action instead of a blind rerun. It also keeps the retry flow aligned with the operator-safety posture used for review approvals and quarantine mutations.

## Scope

- Add a required retry request body to `POST /api/tasks/{id}/retry`.
- Reject missing or blank retry reasons with `400 Bad Request`.
- Store the retry reason on the task alongside retry lineage.
- Include the reason in the `REQUEUED` timeline event and copied task report.
- Show the retry reason in the dashboard retry lineage.
- Require the dashboard operator to enter a reason before `Retry task` is enabled.

## Non-Goals

- Do not change rejected-trigger retry behavior.
- Do not add a separate retry-history table.
- Do not retry pending-review tasks.
- Do not loosen existing retry-preflight blocking policy.

## Acceptance Criteria

- Backend controller and service tests cover missing, blank, and valid retry reasons.
- MySQL and in-memory task services preserve `retryReason`.
- Flyway migration adds `fix_task.retry_reason`.
- Frontend API, component, and App tests verify retry reason input and POST body.
- Product docs describe retry reason audit behavior.
