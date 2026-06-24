# 172 Trigger Evaluation Dry Run

## Goal

Let an operator evaluate a proposed `/agent fix` command before creating a task. The dry run should explain whether the request would execute, why it would be blocked, and which gate produced the decision without mutating tasks, queue, rejected-trigger audits, GitHub comments, or rate-limit counters.

## Scope

- Add a read-only backend evaluation API for manual/dashboard trigger inputs.
- Reuse the same safety, active-task, quarantine, rate-limit, issue-context, and model trigger-classification order as manual task creation.
- Add a read-only rate-limit check so dry-run evaluation does not consume rate-limit quota.
- Render the evaluation in the dashboard manual task area so operators can test a command before pressing `Create task`.
- Record documentation and validation evidence.

## Out of Scope

- No new database tables or migrations.
- No task creation, queueing, GitHub comments, rejected-trigger audit rows, or model patch generation from the dry-run endpoint.
- No replacement for the full rejected-trigger audit or task creation APIs.

## Verification

- Backend RED: controller/service tests first fail because dry-run evaluation does not exist.
- Backend GREEN: focused tests prove accepted, safety-rejected, active-task, rate-limit, and issue-context model-assisted outcomes.
- Frontend RED: API and manual form tests first fail because the dry-run API and panel do not exist.
- Frontend GREEN: dashboard renders allowed/blocked decisions and disables no existing create flow.
- Full checks: `mvn -pl PatchPilot test`, `npm test`, `npm run build`, and `git diff --check`.
