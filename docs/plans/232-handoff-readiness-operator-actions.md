# 232 - Handoff Readiness Operator Actions

## Goal

Make demo handoff readiness actionable instead of only descriptive. Operators should see the next required action for the overall handoff state and for every readiness check in the API, Markdown package, and dashboard.

## Scope

- Add `nextAction` to structured handoff readiness responses.
- Add `nextAction` to each handoff readiness check row.
- Render the same action guidance in generated Markdown handoff readiness sections.
- Show the overall action and per-check actions in the dashboard demo session snapshot panel.
- Preserve the existing readiness status, summary, and check ordering.

## Non-Goals

- Do not add new persistence tables.
- Do not create tasks, call the model, run Git operations, or mutate GitHub.
- Do not change readiness scoring rules beyond adding action guidance.

## Validation

- Backend targeted tests cover structured API fields and Markdown report output.
- Frontend targeted tests cover API parsing and dashboard rendering.
- Full backend and frontend regression checks must pass before merging.
