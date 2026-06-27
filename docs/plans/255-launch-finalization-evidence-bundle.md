# Plan 255: Launch Finalization Evidence Bundle

## Goal

Surface final launch evidence acceptance in the top-level demo evidence bundle and copied runbook so operators can confirm archived, delivered, and accepted launch proof from the first demo readout.

## Scope

- Add launch evidence finalization fields to the backend `DemoEvidenceBundleVo`.
- Reuse `DemoLaunchEvidenceFinalizationService` inside `DemoEvidenceBundleService` so the evidence bundle and standalone finalization endpoint share one source of truth.
- Require launch finalization to be `READY` before the overall evidence bundle reports `READY`.
- Add launch finalization status, accepted receipt id, receipt freshness, and next action to the Markdown runbook.
- Render a `Launch evidence finalization` record in the dashboard evidence bundle panel.
- Update frontend typed fixtures and docs.

## API Contract

`GET /api/demo/evidence-bundle` includes:

- `launchEvidenceFinalizationStatus`
- `launchEvidenceFinalized`
- `launchEvidenceFinalizationSummary`
- `launchEvidenceFinalizationNextAction`
- `launchEvidenceFinalizationDeliveryReceiptFreshness`
- `launchEvidenceFinalizationDeliveryReceiptFresh`
- `launchEvidenceFinalizationLatestDeliveryReceiptId`

The endpoint remains read-only. It must not create tasks, call the model, run tests, create archives, mutate Git, send messages, or write to GitHub.

## Validation

- Backend tests first fail because the service constructor and bundle VO do not expose launch finalization fields, then pass after aggregation implementation.
- Frontend tests first fail because the evidence bundle panel does not render launch finalization, then pass after UI implementation.
- Target backend/frontend tests, full backend tests, full frontend tests, production build, and `git diff --check` must pass before merge.
