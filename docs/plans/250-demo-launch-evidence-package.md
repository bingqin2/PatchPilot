# 250 - Demo Launch Evidence Package

## Goal

Create one final launch evidence package for a self-hosted PatchPilot demo. Operators already have readiness, evidence bundle, session snapshot, finalization, and launch readiness screens, but there is no single API/UI object that says what can be shared before a live `/agent fix` run, what must be archived after the run, and whether the package is safe to use as demo evidence.

## Scope

- Add a read-only `GET /api/demo/launch-evidence-package` endpoint.
- Add a downloadable Markdown report endpoint for the same package.
- Combine self-hosted launch readiness, demo session snapshot, evidence bundle, runbook, readiness trend, handoff finalization, latest task, latest Pull Request, latest webhook delivery, evaluation readiness, and next actions.
- Classify the package as `READY`, `NEEDS_ATTENTION`, or `BLOCKED` from launch readiness, session snapshot, evidence bundle, and finalization status.
- Surface explicit evidence sections: pre-launch checks, live-run proof, post-demo handoff proof, evaluation proof, and health contract.
- Add dashboard API wiring and a panel section with status, copy/download actions, launch blockers, live-run proof, and post-demo proof.
- Update README, product spec, architecture notes, frontend design notes, and progress log.

## Non-Goals

- Do not create tasks, call the model, run tests, archive records, send messages, mutate Git, open Pull Requests, or write to GitHub.
- Do not replace existing detailed panels; this package is the top-level operator artifact that links existing evidence.
- Do not add persistent storage for the package in this slice.

## Validation

- Backend RED/GREEN tests for package aggregation, Markdown report contents, REST serialization, and report download.
- Frontend RED/GREEN tests for API typing, panel rendering, copy report, and App integration.
- Full backend tests, full frontend tests, production frontend build, and `git diff --check` before merging.
