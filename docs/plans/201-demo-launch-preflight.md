# 201 Demo Launch Preflight

## Goal

Add a final read-only preflight before a live demo trigger is posted on GitHub. Operators should be able to enter the exact repository, issue, trigger user, and `/agent fix` issue comment that will be posted, then see whether PatchPilot is ready and whether that comment would create a task.

This closes the gap between general readiness checks and the real launch action: posting a GitHub issue comment.

## Scope

- Add `POST /api/demo/launch-preflight`.
- Reuse the demo readiness gate for current runtime, GitHub, queue, adapter, and safety-policy evidence.
- Reuse trigger evaluation with `ISSUE_COMMENT` source so the dry run follows the same safety, active-task, quarantine, rate-limit, issue-context, and model-classification path as webhook task creation.
- Keep the endpoint read-only: no task creation, queue item creation, GitHub comments, webhook delivery diagnostics, rejected-trigger audit rows, rate-limit increments, Git commits, pushes, Pull Requests, or model patch generation.
- Add a dashboard panel that submits the exact issue comment and displays ready/blocked status, trigger evaluation status, issue-context state, blocked reason, and next actions.
- Add a copyable Markdown report for demo handoff or operator review.

## Out Of Scope

- Posting the GitHub comment from the dashboard.
- Creating tasks directly from this endpoint.
- Replacing the existing manual task form.
- Editing safety policy from the dashboard.

## Validation Plan

- Backend service tests prove ready, blocked, readiness-warning, and validation cases.
- Backend controller tests prove successful and bad-request API responses.
- Frontend API tests prove the dashboard client calls `POST /api/demo/launch-preflight`.
- Frontend component and App tests prove the panel submits the exact issue comment, renders ready/blocked outcomes, and copies Markdown evidence.
- Full backend and frontend regression verification before merge.
