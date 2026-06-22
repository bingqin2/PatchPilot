# Trigger Rate Limit Abuse Guard

## Goal

Reject excessive `/agent fix` attempts before task creation, model classification, queue dispatch, or GitHub API work. This protects the self-hosted service from repeated comments by the same user, repository, or issue while preserving normal local demo usage.

## Scope

- Add a `TriggerRateLimitService` behind the safety boundary.
- Start with an in-memory sliding-window implementation for single-instance local self-hosting.
- Apply the guard to both GitHub webhook triggers and dashboard/manual task creation.
- Record rejected rate-limited attempts through `RejectedTriggerAuditService`.
- Expose non-sensitive rate-limit configuration in `/api/configuration/summary`.
- Show rate-limit state and weak-configuration advisories in the dashboard configuration panel.
- Document environment variables and operator behavior.

## Non-Goals

- Redis-backed or MySQL-backed distributed rate limiting.
- Per-organization billing limits.
- Captcha, account reputation, or public hosted abuse prevention.

## Validation Plan

- Unit test the sliding-window limit by issue, trigger user, and disabled mode.
- Verify webhook rate-limit rejection happens before task creation, status comments, dispatch, and model classification.
- Verify manual task rate-limit rejection happens before task creation, dispatch, and model classification.
- Verify configuration summary and dashboard display the configured rate-limit state.
- Run full backend and frontend test/build checks before merging.
