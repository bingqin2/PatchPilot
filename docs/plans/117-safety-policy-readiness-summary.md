# 117 Safety Policy Readiness Summary

## Goal

Make PatchPilot's runtime safety posture visible before a live demo. Operators should not need to infer whether trigger allowlists, repository allowlists, review approvers, model intent classification, and trigger rate limits are configured by reading `.env` or backend logs.

## Backend Scope

- Extend `GET /api/configuration/summary` with non-sensitive safety policy fields:
  - whether trigger-user, repository, and review-approval allowlists are configured
  - normalized trigger users, repositories, and review approvers
- Add a `Safety policy` check to `GET /api/demo/readiness`.
- Treat open trigger-user or repository allowlists as `NEEDS_ATTENTION`, not `BLOCKED`, because local development can intentionally leave them open.
- Treat missing review approvers as `NEEDS_ATTENTION` because it disables `PENDING_REVIEW` approval.

## Frontend Scope

- Show trigger-user and repository allowlist state in the configuration panel.
- Count missing trigger-user and repository allowlists as advisory configuration items.
- Surface the backend `Safety policy` readiness check in the existing demo readiness panel.

## Documentation

Update README and product notes so the operator-facing safety posture is documented alongside the API behavior.

## Validation

- Backend focused tests for configuration summary and demo readiness safety policy checks.
- Frontend focused tests for configuration panel and demo readiness display.
- Full backend and frontend verification before handoff.
