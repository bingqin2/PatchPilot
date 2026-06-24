# 165 - Rejected Trigger Retry Preflight

## Goal

Prevent unsafe or already-consumed rejected-trigger audit rows from creating manual retry tasks, while making the retry decision visible in the dashboard before an operator clicks anything.

## Scope

- Add a derived retry eligibility policy for rejected-trigger audit rows.
- Expose `retryable` and `retryBlockedReason` on rejected-trigger API rows.
- Return `409 Conflict` from `POST /api/rejected-triggers/{id}/retry` when the rejection category requires a new safer request, allowlist change, cooldown wait, quarantine release, or when the audit row has already created a retry task.
- Disable blocked retry buttons in the dashboard and show the operator-facing reason inline.
- Keep directly retryable categories limited to actionability/model-classification cases where rerunning the manual task flow is reasonable.

## Backend Behavior

`RejectedTriggerRetryPolicy` derives retry eligibility from the stable rejection category and `retriedTaskId`.

Direct retry is allowed for:

- `NOT_ACTIONABLE`
- `MODEL_REJECTED`
- `MODEL_NEEDS_CLARIFICATION`
- `MODEL_CLASSIFICATION_FAILED`

Direct retry is blocked for dangerous instructions, unauthorized users, unauthorized repositories, rate limits, active abuse quarantines, unsupported commands, empty commands, unknown categories, and any audit row that already has a retried task id.

## Frontend Behavior

`RejectedTriggerPanel` renders `retryBlockedReason` as guidance next to the row action. The retry button shows `Retry blocked` and is disabled when `retryable` is false. Rows that remain retryable still show `Retry trigger` and call the same retry endpoint.

## Validation

- `mvn -pl PatchPilot -Dtest=RejectedTriggerRetryPolicyTests,DefaultRejectedTriggerRetryServiceTests,RejectedTriggerAuditControllerTests test`
- `npm test`
- `npm run build`
