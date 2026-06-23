# 129 Safe Refusal Issue Comments

## Goal

Post a safe GitHub issue comment when PatchPilot rejects a `/agent fix` webhook request before task creation, and expose that refusal comment in audit APIs and the dashboard.

This advances the safety target by making non-execution visible to the requester while preserving the rule that unsafe, vague, unauthorized, or rate-limited triggers never create tasks or run commands.

## Scope

- Add optional refusal comment metadata to rejected trigger audits.
- Persist refusal comment id and URL in MySQL-backed rejected trigger audit records.
- Add a safe refusal comment method to `IssueCommentTool`.
- Make webhook rejection paths attempt a refusal comment before recording the audit.
- Keep task creation, queue dispatch, and status comments unchanged for accepted tasks.
- Show the refusal comment link in the dashboard rejected triggers panel.

## Non-Goals

- Do not post comments for ignored non-`/agent fix` comments.
- Do not post comments for manual dashboard/API rejections.
- Do not fail webhook handling when refusal comment creation fails.
- Do not expose raw stack traces, secrets, or model prompts in refusal comments.

## Validation

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests,GitHubIssueCommentClientTests,RejectedTriggerAuditControllerTests,InMemoryRejectedTriggerAuditServiceTests,MyBatisRejectedTriggerAuditServiceTests,RejectedTriggerAuditMigrationTests test`
- `cd frontend && npm test -- RejectedTriggerPanel.test.tsx api.test.ts App.test.tsx`
- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
