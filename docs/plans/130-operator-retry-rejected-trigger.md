# 130 Operator Retry Rejected Trigger

## Goal

Let an operator retry a previously rejected `/agent fix` trigger from the dashboard without bypassing PatchPilot safety gates.

This improves demo recovery and operator debugging: a vague or temporarily rejected request can be inspected, corrected in configuration if needed, and retried from the same audit trail instead of requiring the user to re-comment on GitHub.

## Scope

- Add `POST /api/rejected-triggers/{id}/retry`.
- Add rejected-trigger lookup by audit id for in-memory and MyBatis-backed audit services.
- Recreate the task through the existing manual task creation flow so allowlists, active-task checks, rate limits, and model trigger classification still apply.
- Record a task timeline event that links the new task back to the rejected trigger audit id and prior rejection reason.
- Add a dashboard retry action for rejected triggers with per-row loading state and dashboard refresh after success.

## Non-Goals

- Do not bypass safety, rate limit, or model intent checks.
- Do not mutate the original rejected trigger audit record.
- Do not post a new GitHub comment for dashboard retries.
- Do not add operator identity tracking beyond the existing dashboard/API controls.

## Validation

- `mvn -pl PatchPilot -Dtest=RejectedTriggerAuditControllerTests,DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,GitHubWebhookServiceTests test`
- `cd frontend && npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`
- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
