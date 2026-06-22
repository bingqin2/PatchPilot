# 116 Risk Review Approval Authorization

## Goal

Prevent arbitrary dashboard or API callers from resuming `PENDING_REVIEW` tasks by typing any approver name. Risk review approval should require an operator from an explicit allowlist, keep the existing approval audit metadata, and make missing configuration visible before a demo.

## Backend Scope

- Add `patchpilot.review-approval.allowed-operators`, bound from `PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS`.
- Expose normalized approval operators through `GET /api/configuration/summary`.
- Reject `POST /api/tasks/{taskId}/approve-review` with `403` when the operator is not in the allowlist.
- Keep unauthorized approvals from mutating task status, approval metadata, queue state, or timeline records.

## Frontend Scope

- Show configured review approvers in the configuration panel.
- Treat an empty approver allowlist as an advisory configuration issue.
- Replace free-text approver entry with a configured approver selector.
- Disable pending-review approval when no approvers are configured.

## Documentation

Update README and product notes to explain that task trigger allowlists may be left open for local development, but review approval is strict: no allowlist means no approval.

## Validation

- Backend focused tests for configuration summary exposure, control-service authorization, and controller `403`.
- Frontend component tests for configuration display and approval form behavior.
- Full backend and frontend test/build verification before handoff.
