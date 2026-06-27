# 227 - Demo Webhook Setup Gate

## Goal

Make the combined webhook setup readiness summary part of the live demo gate. Operators should not have to mentally combine secret configuration, public URL health, and latest delivery diagnostics before deciding whether it is safe to post a live `/agent fix` trigger.

## Scope

- Feed `GitHubWebhookSetupReadinessService` into demo readiness.
- Replace the readiness check label `GitHub webhook URL` with `GitHub webhook setup`.
- Map setup states to demo readiness: `READY` stays ready, `NEEDS_ATTENTION` warns, and `BLOCKED` blocks the demo.
- Feed webhook setup readiness into the smoke checklist before evaluating latest delivery evidence.
- Keep the existing five smoke steps, but make `Webhook delivery` block or warn when setup is not ready.
- Update the operator setup checklist to prefer the combined demo readiness check while keeping the URL-only fallback.
- Update tests and docs for the new gate.

## Non-Goals

- Do not edit GitHub webhook settings or redeliver events.
- Do not create tasks, comments, queue items, or delivery records.
- Do not call the model, clone repositories, run verification commands, mutate Git, or expose secrets.
- Do not redesign the dashboard layout.

## Validation

- Backend demo readiness and smoke checklist tests cover blocked setup and ready setup behavior.
- Frontend App and operator checklist tests cover the new `Webhook setup` row and setup-first fallback.
- Full backend and frontend regression tests should pass before merge.
