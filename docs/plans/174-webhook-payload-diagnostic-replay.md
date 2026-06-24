# 174 Webhook Payload Diagnostic Replay

## Goal

Add an admin-protected, read-only webhook payload diagnostic endpoint and dashboard panel so operators can validate GitHub webhook payload shape, trigger recognition, and optional signature matching before redelivering or posting a live `/agent fix` comment.

## Why This Matters

Self-hosted demos often fail before task creation because the temporary URL, webhook secret, event type, or payload shape is wrong. Current diagnostics show only deliveries that already reached the real webhook endpoint. This feature lets the operator paste a payload and verify what PatchPilot would parse without creating tasks, queue items, rejected-trigger audits, GitHub comments, delivery diagnostics, rate-limit records, or worker activity.

## Scope

- Add `POST /api/github/webhook-diagnostics/evaluate-payload`.
- Require the existing admin-token protection because this endpoint can reveal parsed payload metadata.
- Accept:
  - `event`
  - `deliveryId`
  - `signature`
  - `payload`
- Return:
  - diagnostic status
  - signature check result when a signature is provided
  - JSON validity
  - supported event/action flags
  - `/agent fix` trigger recognition
  - parsed repository, issue, trigger user, and trigger comment fields when available
  - next operator action
- Add dashboard controls in the webhook delivery panel for pasting a payload and running the diagnostic.
- Update README, product spec, architecture, frontend design notes, and execution log.

## Out of Scope

- No task creation.
- No queue dispatch.
- No rejected-trigger audit records.
- No GitHub issue comments.
- No webhook delivery diagnostic persistence.
- No rate-limit checks or model calls.
- No full replay of GitHub's delivery UI.

## Verification

- Backend RED: controller/service tests first fail because the diagnostic endpoint and read model do not exist.
- Backend GREEN: tests prove valid payloads parse, invalid signatures are reported without mutation, malformed payloads return a diagnostic response, and task state remains unchanged.
- Frontend RED: API and panel tests first fail because no diagnostic helper or form exists.
- Frontend GREEN: dashboard can submit a payload diagnostic and render status, parsed target, signature state, and next action.
- Full checks: `mvn -pl PatchPilot test`, `npm test`, `npm run build`, and `git diff --check`.
