# 299 Final Release Bundle Evidence Surface

## Goal

Promote the final external-review release bundle into the top-level demo evidence bundle and copied runbook so operators can prove final reviewer handoff readiness from the first dashboard readout.

## Scope

- Add the final external-review release bundle to `DemoEvidenceBundleVo`.
- Make top-level evidence readiness depend on the release bundle status.
- Include release bundle status, identifiers, attachments, checks, evidence notes, and download actions in copied runbooks.
- Render a final release bundle card in the dashboard evidence bundle panel with legacy fallback guidance.
- Update README, product spec, frontend design, and progress log.

## Backend

- Inject `DemoFinalExternalReviewReleaseBundleService` into `DemoEvidenceBundleService`.
- Add a `DemoFinalExternalReviewReleaseBundleVo` supplier for focused tests.
- Include `finalExternalReviewReleaseBundle` in the evidence bundle record.
- Add default missing release-bundle evidence for legacy constructors.
- Extend `aggregateStatus` and `nextActions` so missing or blocked release bundles affect top-level readiness.
- Extend `DemoRunbookService` with final release-bundle lines.

## Frontend

- Extend `DemoEvidenceBundle` with optional `finalExternalReviewReleaseBundle`.
- Add fallback missing release-bundle evidence for older backend responses.
- Render release readiness, certificate archive, package archive, delivery receipt, Pull Request, attachments, evidence notes, and download actions in `DemoEvidenceBundlePanel`.
- Update App-level test fixtures so the main dashboard smoke test loads the new field.

## Validation

- Backend RED/GREEN tests for evidence bundle readiness and copied runbook release lines.
- Frontend RED/GREEN tests for the top-level release bundle card and legacy fallback guidance.
- Full backend Maven tests, frontend tests, production build, and `git diff --check` before merge.
