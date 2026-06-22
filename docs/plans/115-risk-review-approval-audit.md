# 115 - Risk Review Approval Audit

## Goal

Make risk-review approval accountable. A `PENDING_REVIEW` task should only resume after an operator records who approved the generated diff and why it is safe to continue.

## Scope

- Require `operator` and `reason` in `POST /api/tasks/{taskId}/approve-review`.
- Persist approval metadata with the task: approval time, approver, and approval reason.
- Record approval metadata in the timeline, task API response, copied report, and dashboard detail view.
- Keep approval restricted to `PENDING_REVIEW` tasks.
- Clear stale approval metadata when a task enters a fresh retry or new pending-review state.

## Backend Design

`ApproveReviewDto` accepts the API request body and maps to `ApproveReviewCommand`. `TaskController` returns `400` for a missing or blank operator/reason before calling the control service.

`FixTaskService.markPendingForReviewApproval(...)` stores `riskReviewApprovedAt`, `riskReviewApprovedBy`, and `riskReviewApprovalReason`. MySQL uses a Flyway migration to add nullable audit columns so existing task history remains readable.

The control service records a `REVIEW_APPROVED` timeline event with the approver and reason before re-enqueuing the task.

## Frontend Design

`TaskDetailPanel` replaces one-click approval with a compact review approval form. The button is disabled until both fields are filled. Approved tasks show the stored approver, approval time, and reason in task detail.

## Validation

- Backend controller, control service, in-memory service, MyBatis service, conversion, and migration tests cover audit metadata.
- Frontend API, detail panel, and app integration tests cover the request body and approval display.
- Full backend and frontend suites must pass before handoff.
