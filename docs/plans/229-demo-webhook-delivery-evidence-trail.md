# Demo Webhook Delivery Evidence Trail

## Goal

Carry the recent GitHub webhook delivery trail into the demo evidence bundle,
dashboard evidence panel, and session report Markdown. A live-demo handoff should
show not only the latest delivery but also the surrounding delivery sequence and
each delivery's task, rejected-trigger, duplicate, ignored, or error outcome.

## Scope

- Add a capped `recentWebhookDeliveries` list to the demo evidence bundle read
  model.
- Keep `latestWebhookDelivery` for backward-compatible summary cards.
- Render a `Recent Webhook Deliveries` section in generated session reports.
- Show a compact delivery trail in the dashboard evidence bundle panel.
- Preserve read-only behavior: no task creation, GitHub redelivery, GitHub
  mutation, model call, queue mutation, Git command, or test execution happens
  from evidence bundle or report generation.

## Out of Scope

- Calling GitHub's webhook delivery API.
- Creating new webhook delivery diagnostics.
- Changing webhook handling or task creation behavior.
- Persisting new database rows.

## Validation

- Backend focused tests should cover evidence bundle construction, REST
  serialization compatibility, and session report Markdown.
- Frontend focused tests should cover dashboard trail rendering and existing app
  fixture compatibility.
- Full backend and frontend regression suites should pass before merge.
