# 298 Final External Review Release Bundle

## Goal

Create one read-only terminal release bundle for external reviewers after the certified final external-review delivery certificate has been archived.

## Scope

- Derive a final release-ready/not-ready read model from the latest final external-review delivery certificate archive.
- Expose JSON and Markdown download endpoints for the release bundle.
- Show the release bundle in the final demo acceptance dashboard with attachments, checks, evidence notes, and download actions.
- Wire the bundle into App-level loading so the main dashboard proves the complete review package is available.
- Document the endpoint, no-side-effect boundary, and verification evidence.

## Backend

- Add `DemoFinalExternalReviewReleaseBundleVo` and `DemoFinalExternalReviewReleaseBundleService`.
- Use the latest certificate archive as the release source of truth.
- Report `READY` only when the archive is `READY`, certified, and has fresh delivery receipt evidence.
- Include required attachments for the certificate archive, finalization archive, package archive, and delivery receipt.
- Add `GET /api/demo/final-external-review-release-bundle` and `GET /api/demo/final-external-review-release-bundle/report/download`.

## Frontend

- Add TypeScript types and API helpers for the release bundle and report download.
- Load the release bundle during the dashboard API batch.
- Render the bundle in `DemoAcceptanceSummaryPanel` with release status, key identifiers, checks, attachments, evidence notes, and report download.
- Keep the view read-only; it must not create tasks, call the model, archive records, record receipts, mutate Git, send messages, or write to GitHub.

## Validation

- Backend service and controller tests for ready and missing-bundle states.
- Frontend API, component, and App-level dashboard tests.
- Full backend Maven test suite, frontend test suite, production build, and diff whitespace check before merge.
