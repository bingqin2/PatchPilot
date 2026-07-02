# 338 Live Demo Reviewer Delivery Center Archives

## Goal

Freeze the live demo reviewer delivery center as durable local evidence after it becomes deliverable. This turns the current reviewer-facing readout into an auditable handoff artifact that operators can list and download after refreshes or later demo review.

## Scope

- Add backend archive value object, repository, service, and controller endpoints under `/api/demo/live-demo-handoff-package/reviewer-delivery-center/archives`.
- Archive only `READY` and deliverable reviewer delivery centers.
- Preserve readiness cards, blockers, evidence links, download actions, side-effect contract, generated time, archive time, and frozen Markdown report.
- Add dashboard API helpers, TypeScript types, archive/list/download wiring, and recent archive display in the live launch panel.
- Update progress documentation with RED/GREEN and verification evidence.

## Out of Scope

- No GitHub writes, model calls, task creation, or automatic reviewer notifications.
- No MySQL persistence; this follows the existing in-memory live-demo archive pattern for the demo evidence surface.
- No changes to readiness scoring for handoff package, artifact chain, certificate, or replay package.

## Verification

- Focused backend tests for archive service and controller routes.
- Focused frontend API and live launch panel tests.
- Full backend test suite, frontend test suite, production build, whitespace check, and secret scan before merge.
