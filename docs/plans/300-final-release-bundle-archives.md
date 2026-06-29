# 300 Final Release Bundle Archives

## Goal

Freeze the READY final external-review release bundle into durable local evidence so operators can prove exactly which reviewer-facing bundle was preserved after the live read model changes.

## Scope

- Add backend release-bundle archive persistence, create/list/download endpoints, and protected audit evidence.
- Promote latest release-bundle archive evidence into the top-level demo evidence bundle and copied runbook.
- Add dashboard archive controls, recent archive history, report downloads, and evidence-bundle archive visibility.
- Update README, product spec, frontend design, architecture notes, and progress log.

## Backend

- Add `DemoFinalExternalReviewReleaseBundleArchiveVo`, entity, mapper, converter, repository interface, in-memory repository, MyBatis repository, and Flyway migration.
- Implement `DemoFinalExternalReviewReleaseBundleArchiveService` with a READY-only archive guard and capped recent archive listing.
- Expose `POST /api/demo/final-external-review-release-bundle/archives`, `GET /api/demo/final-external-review-release-bundle/archives`, and `GET /api/demo/final-external-review-release-bundle/archives/{archiveId}/report/download`.
- Record protected operator safety audit rows for archive creation.
- Add `finalExternalReviewReleaseBundleArchiveEvidence` to the demo evidence bundle and copied runbook.

## Frontend

- Add API helpers and types for archive create/list/download.
- Load release-bundle archives during dashboard refresh.
- Add an `Archive final external-review release bundle` action, archive-history list, archive report download, and archive load error handling in the final demo acceptance panel.
- Show latest release-bundle archive evidence in the top-level evidence bundle with legacy fallback guidance.

## Validation

- Backend RED/GREEN tests for service, conversion, migration, controller endpoints, evidence-bundle readiness, and runbook output.
- Frontend RED/GREEN tests for API helpers, acceptance-panel archive controls/history, evidence-bundle archive evidence, and App smoke wiring.
- Full backend Maven tests, frontend tests, production build, and `git diff --check` before merge.
