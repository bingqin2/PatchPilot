# Plan 151: Demo Runbook Export

## Goal

Turn the demo evidence bundle into a copyable Markdown runbook so an operator can hand off the current demo state, evidence, and next actions without manually combining dashboard panels or curl output.

## Backend Scope

- Add `GET /api/demo/runbook`.
- Reuse the existing demo evidence bundle service.
- Format the bundle as Markdown with status, summary, generated time, recent PR, latest task, latest webhook delivery, adapter fixture counts, queue counts, rejected-trigger count, active quarantine count, readiness checks, smoke checklist steps, and next actions.
- Keep the endpoint read-only: no task creation, model call, test execution, queue mutation, GitHub write, or repository access.

## Frontend Scope

- Add `getDemoRunbook`.
- Add a `Copy runbook` action to `DemoEvidenceBundlePanel`.
- Fetch the runbook only when the operator clicks the action.
- Copy the Markdown to the clipboard and show success/failure feedback.

## Validation

- Backend service and controller tests for Markdown generation and endpoint exposure.
- Frontend API, component, and App tests for copy behavior.
- Full backend test suite, frontend test suite, production build, and whitespace check before handoff.
