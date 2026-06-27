# 226 - Webhook Setup Readiness Summary

## Goal

Give operators one read-only answer to "is GitHub webhook setup ready for a live `/agent fix` run?" The previous URL readiness check proves the public tunnel health endpoint is reachable, but operators still need to see the webhook secret state, derived payload URL, latest delivery outcome, and redelivery guidance together.

## Scope

- Add `GET /api/github/webhook-setup-readiness`.
- Combine non-sensitive configuration summary, public webhook URL readiness, and the latest webhook delivery diagnostic.
- Return `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- Include payload URL, health URL, latest delivery status/id, redelivery recommendation, next actions, and a copyable Markdown report.
- Add the summary to the dashboard webhook delivery panel above recent deliveries and pasted payload diagnostics.
- Update API helpers, frontend types, tests, and docs.

## Non-Goals

- Do not create tasks, queue items, delivery records, or rejected-trigger audits.
- Do not call the model.
- Do not clone repositories, run verification commands, mutate Git, post GitHub comments, edit webhook settings, or redeliver GitHub events.
- Do not expose the webhook secret value.

## Validation

- Backend service and controller tests cover ready, blocked, and redelivery-attention states.
- Frontend API, Webhook delivery panel, and App tests cover the endpoint call and dashboard rendering.
- Full backend and frontend regression tests should pass before merge.
