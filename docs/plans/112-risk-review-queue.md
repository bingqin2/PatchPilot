# 112 Risk Review Queue

## Goal

Promote generated-diff risk-gate rejections from generic task failures into an explicit `PENDING_REVIEW` state. Operators should see that the task stopped because PatchPilot needs human review, not because verification failed.

## Scope

- Add `FixTaskStatus.PENDING_REVIEW` and `FixTaskTimelineEventType.PENDING_REVIEW`.
- Mark generated-diff risk-gate rejections as pending review in `FixTaskWorker`.
- Keep the concrete rejection reason in `failureReason`.
- Update the existing GitHub status comment to say the task is paused for human review.
- Include pending-review counts in status count and metrics APIs.
- Allow operators to cancel pending-review tasks.
- Reject retry for pending-review tasks until a future approval flow exists.
- Show `PENDING_REVIEW` as a dashboard status filter, count badge, task status pill, and risk-gate evidence.

## Non-Goals

- Approving and resuming a pending-review task.
- Editing generated patches in the dashboard.
- Assigning reviewers or sending notification workflows.

## Verification

- Backend focused tests cover worker status transitions, cancel/retry control behavior, metrics counts, task API filtering, and status-comment updates.
- Frontend focused tests cover task list filters, API typing, and task detail risk evidence.
- Full backend and frontend suites should pass before merge.
