# 225 - GitHub Webhook URL Readiness

## Goal

Make stale or missing public webhook URLs visible before a live `/agent fix` demo. A localhost backend can be healthy while GitHub still cannot reach it because the `cloudflared` quick tunnel changed; operators need one readiness check that derives the exact GitHub Payload URL and proves the public health endpoint reaches PatchPilot.

## Scope

- Add `PATCHPILOT_GITHUB_WEBHOOK_PUBLIC_BASE_URL` / `patchpilot.github.webhook-public-base-url`.
- Add a read-only backend readiness endpoint at `GET /api/github/webhook-url-readiness`.
- Normalize the configured base URL and derive:
  - payload URL: `<base>/api/github/webhook`
  - health URL: `<base>/health`
- Probe the public health URL and return `READY` or `NEEDS_ATTENTION` with latency, checked time, and operator action.
- Add the check to demo readiness and the dashboard operator setup checklist.
- Expose non-sensitive webhook URL configuration in `/api/configuration/summary`.
- Update docs and tests.

## Non-Goals

- Do not create or edit GitHub webhook settings.
- Do not redeliver GitHub events.
- Do not validate webhook signatures in this readiness check.
- Do not create tasks, queue items, model calls, Git operations, or GitHub comments.

## Validation

- Backend unit and controller tests cover missing, reachable, and stale public URL states.
- Demo readiness tests cover the new setup check.
- Frontend API, checklist, and app tests cover rendering the payload URL and attention state.
- Full backend and frontend regression tests should pass before merge.
