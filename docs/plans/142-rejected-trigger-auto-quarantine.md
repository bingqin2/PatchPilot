# Rejected Trigger Auto Quarantine

## Goal

Automatically reject repeated abusive or low-quality `/agent fix` attempts before they reach model classification, repository cloning, queueing, or task creation.

## Why This Matters

PatchPilot already records rejected trigger audits and summarizes recent abuse patterns. The next maturity step is to use that evidence to reduce repeated noise automatically during self-hosted demos and private deployments.

## Scope

- Add a configurable rejected-trigger quarantine policy.
- Check recent rejected-trigger audits by trigger user and repository.
- Reject requests with a stable `ABUSE_QUARANTINED` category once the threshold is reached.
- Apply the same quarantine check to GitHub webhook triggers and dashboard/manual task creation.
- Expose quarantine configuration in the configuration summary and dashboard.
- Keep existing rate limits, deterministic safety checks, model classification, task queue, and retry behavior intact.

## Non-Goals

- Do not implement public-hosted reputation scoring, captcha, IP intelligence, billing limits, or account suspension.
- Do not block non-triggering comments.
- Do not mutate historical rejected-trigger audit rows.
- Do not add a separate quarantine management page.

## Acceptance Criteria

- A trigger user with enough recent rejected attempts is rejected with `ABUSE_QUARANTINED`.
- A repository with enough recent rejected attempts is rejected with `ABUSE_QUARANTINED`.
- Quarantine runs after deterministic command/user/repository safety checks and before rate-limit/model-classifier checks.
- Quarantined requests create rejected-trigger audit rows and GitHub delivery diagnostics.
- Operators can see quarantine policy settings in `/api/configuration/summary` and the dashboard configuration panel.
- The rejected-trigger dashboard can filter and summarize `ABUSE_QUARANTINED` records.
