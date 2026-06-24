# 173 Webhook Trigger Evaluation Preview

## Goal

Let operators preview a proposed GitHub `issue_comment` `/agent fix` trigger with the same read-only gate flow used by manual dry runs. The preview explains whether the webhook-style trigger would create a task or be blocked without creating tasks, queue items, rejected-trigger audit rows, GitHub comments, webhook delivery diagnostics, or rate-limit records.

## Scope

- Add a `source` field to trigger evaluation input and output.
- Support `MANUAL` and `ISSUE_COMMENT` evaluation sources.
- Keep existing manual evaluation behavior as the default when `source` is omitted.
- Reuse the same safety, active-task, quarantine, read-only rate-limit, issue-context, and model trigger-classification order.
- Use source-specific backend values for downstream gates:
  - `MANUAL` maps to existing source value `manual`.
  - `ISSUE_COMMENT` maps to existing webhook source value `issue_comment`.
- Add dashboard controls so operators can switch between manual API and GitHub issue-comment preview before creating a task.
- Record documentation and validation evidence.

## Out of Scope

- No GitHub webhook signature validation or full webhook payload simulator.
- No task creation, queueing, GitHub comments, rejected-trigger audit rows, webhook delivery diagnostics, or model patch generation from preview evaluation.
- No change to the real GitHub webhook execution path.
- No new database tables or migrations.

## Verification

- Backend RED: controller/service tests first fail because trigger evaluation has no source field and always uses manual source.
- Backend GREEN: focused tests prove `ISSUE_COMMENT` source is returned, passed to quarantine/rate-limit/model classification, and remains read-only.
- Frontend RED: API/form tests first fail because source is not sent or rendered.
- Frontend GREEN: dashboard lets the operator select manual API or GitHub issue-comment preview and renders the selected source in the evaluation result.
- Full checks: `mvn -pl PatchPilot test`, `npm test`, `npm run build`, and `git diff --check`.
