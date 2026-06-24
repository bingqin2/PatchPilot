# 171 Trigger Execution Intent Audit

## Goal

Expose why an accepted `/agent fix` task was allowed to execute as structured task evidence. Operators should not need to parse raw timeline text to understand the safety gate, issue-context, and model trigger-classification decisions behind a task.

## Scope

- Derive a nullable `triggerIntentAudit` read model from the latest accepted-trigger timeline event.
- Include the audit in `GET /api/tasks/{taskId}/detail`.
- Include the audit in copied Markdown task reports.
- Render the audit in the selected-task dashboard detail panel.
- Reuse existing `TRIGGER_ACCEPTED` timeline evidence and avoid new model calls, database tables, or migrations.

## Out of Scope

- No change to command safety-gate behavior.
- No change to rejected-trigger retry or quarantine policy.
- No new persistent audit table.
- No dashboard controls that can override trigger decisions.

## Verification

- Add failing backend controller coverage for `triggerIntentAudit` and report output before implementation.
- Add failing frontend component coverage for the task-detail trigger intent section before implementation.
- Run focused backend and frontend tests.
- Run full backend and frontend regression checks before handoff.
