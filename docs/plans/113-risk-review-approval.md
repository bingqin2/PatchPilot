# 113 Risk Review Approval

## Goal

Allow an operator to approve a `PENDING_REVIEW` task after inspecting a generated-diff risk gate rejection, then continue the same task without regenerating a different patch.

## Design

- Add `riskReviewApprovedAt` to `fix_task` as durable approval evidence.
- Add `POST /api/tasks/{id}/approve-review`.
- Only allow approval from `PENDING_REVIEW`; reject other states with `409`.
- Approval changes the task back to `PENDING`, clears the risk failure reason, records a `REVIEW_APPROVED` timeline event, and enqueues the task.
- The executor treats `riskReviewApprovedAt != null` as a resume signal:
  - restore the existing task workspace,
  - run language adapter detection again,
  - skip model patching, diff generation, and generated-diff risk gate,
  - continue verification, commit, push, and Pull Request creation.
- Retry remains reserved for `FAILED` and `CANCELLED` tasks and clears any old review approval signal.
- The dashboard shows an `Approve review` action only for `PENDING_REVIEW` tasks.

## Constraints

Approval is tied to the existing task workspace. If that workspace is deleted, the resumed run fails with a clear message and the operator should cancel or retry to generate a fresh patch.

Approval does not bypass deterministic trigger safety, command allowlists, adapter verification, test execution, GitHub Pull Request review, or human merge review.

## Validation

- Backend control, controller, service, and executor tests cover approval state transitions, queueing, API responses, persisted approval timestamps, and workspace resume without regenerating diffs.
- Frontend API, detail panel, and app tests cover the approve-review action and dashboard refresh behavior.
