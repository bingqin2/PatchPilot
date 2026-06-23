# 131 Retry Rejected Trigger Audit Link

## Goal

Persist the relationship between a rejected trigger audit record and the task created when an operator retries it.

This closes the recovery loop from `Rejected triggers`: operators can see which refusal was retried, open the generated task from the same dashboard row, and verify the resulting execution without searching task history manually.

## Scope

- Add `retriedTaskId` and `retriedAt` metadata to rejected trigger audit records.
- Persist the metadata for both in-memory and MyBatis-backed rejected trigger audit services.
- Add a Flyway migration for MySQL-backed deployments.
- Update rejected-trigger retry flow to mark the audit row after creating the new task and recording the retry timeline event.
- Return retry metadata from `GET /api/rejected-triggers`.
- Add a dashboard `Retried task` link that selects the generated task and routes to `/tasks/{id}`.

## Non-Goals

- Do not create a full retry history table; the audit row stores the latest retried task.
- Do not change the existing safety, rate-limit, active-task, or model-classification gates.
- Do not post a new GitHub issue comment for dashboard retries.
- Do not add operator identity tracking beyond the existing dashboard/API controls.

## Validation

- `mvn -pl PatchPilot -Dtest=DefaultRejectedTriggerRetryServiceTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditControllerTests,RejectedTriggerAuditMigrationTests,GitHubWebhookServiceTests test`
- `cd frontend && npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`
- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
