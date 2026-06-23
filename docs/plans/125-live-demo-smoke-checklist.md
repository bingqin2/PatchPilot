# 125 Live Demo Smoke Checklist

## Goal

Add a read-only smoke checklist that tells an operator what to verify immediately before a live `/agent fix` demo and which evidence is currently available.

## Scope

- Add `GET /api/demo/smoke-checklist`.
- Derive checklist steps from existing demo readiness, recent webhook deliveries, and recent task history.
- Include step order, status, evidence, and action text.
- Add a dashboard panel that renders the checklist near the existing readiness/setup panels.
- Update README, frontend design notes, and execution logs.

## Non-Goals

- Do not create tasks automatically.
- Do not call GitHub's webhook delivery API.
- Do not mutate queue state, retry tasks, approve risk reviews, or post comments.
- Do not replace the existing demo readiness API.

## Verification

- Backend service and controller tests cover ready, attention, and blocked checklist states.
- Frontend API and dashboard tests cover loading and rendering the checklist.
- Full backend and frontend checks pass before handoff.
