# 306 - Final Reviewer Handoff Package

## Goal

Create one read-only final reviewer handoff package from the latest archived terminal release-bundle delivery certificate. This gives operators a single final artifact to inspect, download, and send after all release-bundle delivery proof has been frozen.

## Scope

- Add a backend package VO/service derived from the latest `DemoFinalExternalReviewReleaseBundleDeliveryCertificateArchiveVo`.
- Add `GET /api/demo/final-reviewer-handoff-package` and `GET /api/demo/final-reviewer-handoff-package/report/download`.
- Surface the package in the top-level demo evidence bundle and copied runbook.
- Render a final reviewer handoff card in the operations dashboard with readiness, key archive ids, delivery proof, required attachments, download actions, Pull Request link, and Markdown download.
- Cover backend package creation, controller endpoints, evidence bundle aggregation, runbook output, frontend API helpers, dashboard rendering, download behavior, and legacy evidence-bundle fallback.

## Out of Scope

- Sending reviewer messages externally.
- Creating new archives or delivery receipts.
- Mutating tasks, Git, GitHub, queue state, or model-call state.

## Verification

- `mvn -q -pl PatchPilot test`
- `npm --prefix frontend test -- --reporter=dot`
- `npm --prefix frontend run build`
- `git diff --check`
