# Plan 152: Demo Script And Health Contract

## Goal

Turn the current demo evidence bundle into a read-only live-demo script so an operator can follow the same issue-to-PR path every time without inventing commands from memory.

## Backend Scope

- Add `GET /api/demo/script`.
- Reuse the existing demo evidence bundle service.
- Return ordered script steps for backend/dashboard access, configuration and safety posture, repository support, controlled `/agent fix` triggering, task execution tracking, and Pull Request evidence review.
- Include operator action, verification command, success criteria, troubleshooting panel, and current evidence for each step.
- Include a health contract that states the endpoint is read-only and does not create tasks, call the model, run tests, mutate Git, or write to GitHub.

## Frontend Scope

- Add `getDemoScript`.
- Add `DemoScriptPanel` near the existing demo evidence/readiness panels.
- Render ordered steps, status, generated time, health contract, and next actions.
- Keep script loading independent so a script API failure does not hide the rest of the dashboard.

## Validation

- Backend service and controller tests for script generation, health contract, and endpoint exposure.
- Frontend API, component, and App tests for script loading and rendering.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
