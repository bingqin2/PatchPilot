# Plan 160: Issue Context Trigger Classification

## Goal

Allow short trigger comments such as `/agent fix` to be evaluated against the GitHub issue title, body, and recent comments before PatchPilot decides whether to create a task. This improves real issue-to-PR usage where the issue contains the concrete bug report and the trigger comment only asks the agent to start.

## Scope

- Extend trigger intent classification requests with issue title, issue body, and recent issue comments.
- Include issue context in the model trigger-classification prompt.
- Let webhook and manual task creation use issue context only after cheap safety, duplicate, active-task, quarantine, and rate-limit checks pass.
- Keep destructive, unsupported, unauthorized, quarantined, and rate-limited requests rejected before any issue context read.
- Allow `NOT_ACTIONABLE` short commands to proceed only when the active classifier explicitly supports issue-context classification.
- Reject conservatively when issue context cannot be loaded for model classification.

## Out of Scope

- Changing task execution prompts or repository-edit logic.
- Persisting issue context snapshots with tasks.
- Adding dashboard UI for model prompt inspection.
- Supporting non-GitHub issue trackers.

## Verification

- Model classifier tests cover issue context prompt content.
- Webhook service tests cover short `/agent fix` using issue context before task creation.
- Manual task service tests cover the same behavior for dashboard-created tasks.
- Issue context service tests cover repository/issue based context loading.
- Full backend test suite should pass after integration.
