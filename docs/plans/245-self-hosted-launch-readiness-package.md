# Self-Hosted Launch Readiness Package

## Goal

Create one operator-facing launch readiness package that answers whether the current self-hosted PatchPilot instance is ready for a real `/agent fix` demo or handoff.

## Problem

PatchPilot already exposes many separate readiness surfaces: demo readiness, evidence bundle, webhook setup, credentials, queue health, adapter runtime readiness, and handoff finalization. Operators still need to jump between panels and reports to decide whether the self-hosted instance is ready to launch. That makes the final pre-demo check harder to explain and easier to miss.

## Scope

- Add a read-only backend service that composes existing readiness and evidence-bundle read models into a single launch package.
- Add `GET /api/demo/self-hosted-launch-readiness` for structured status.
- Add `GET /api/demo/self-hosted-launch-readiness/report/download` for a Markdown evidence report.
- Add dashboard API/types and a compact launch readiness panel with status, gate checks, next actions, and report download.
- Update README and product docs so this becomes the recommended final pre-launch readout.

## Out Of Scope

- No task creation.
- No model calls.
- No GitHub mutation.
- No webhook redelivery.
- No fixture/test execution.
- No persistence; this is a current-state read model.

## Validation

- Backend tests cover ready, needs-attention, and blocked launch-package aggregation.
- Controller tests cover JSON and Markdown download endpoints.
- Frontend tests cover loading, rendering, and report download.
- Run focused backend and frontend tests, then full backend tests, full frontend tests, build, and `git diff --check`.
