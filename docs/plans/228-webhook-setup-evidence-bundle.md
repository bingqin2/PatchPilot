# 228 - Webhook Setup Evidence Bundle

## Goal

Carry combined GitHub webhook setup readiness into the demo evidence bundle, session report, and dashboard evidence panel. Operators should not need to open a separate diagnostics panel to prove that the webhook secret, public payload URL, and latest delivery context were ready when a session report or handoff artifact was generated.

## Scope

- Add webhook setup readiness to `DemoEvidenceBundleVo` and populate it from `GitHubWebhookSetupReadinessService`.
- Include a `Webhook Setup Readiness` section in demo session report Markdown.
- Render webhook setup status, summary, and payload URL in the dashboard demo evidence bundle panel.
- Update tests and docs for the new evidence path.

## Non-Goals

- Do not edit GitHub webhook settings.
- Do not redeliver GitHub events.
- Do not create tasks, call the model, run tests, mutate Git, or write to GitHub from evidence/report endpoints.

## Validation

- Backend focused tests cover evidence bundle construction, REST serialization compatibility, and session report Markdown.
- Frontend focused tests cover API typing, dashboard integration, and evidence panel rendering.
- Full backend and frontend regression suites must pass before merge.
