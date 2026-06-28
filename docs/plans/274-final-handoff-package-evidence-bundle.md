# 274 Final Handoff Package Evidence Bundle

## Goal

Surface the latest archived final demo handoff report package in the top-level demo evidence bundle and copied runbook so operators can prove post-demo closeout readiness without opening the session panel first.

## Why This Matters

The final handoff report package archive is the stable closeout artifact for reviewers. After `273`, operators can create and download those archives from the session panel, but the first-screen evidence bundle still does not say whether that final package archive exists. This feature closes that gap by making the latest archive visible in the one-call demo summary.

## Scope

- Add a compact backend evidence read model for final handoff report package archives.
- Read the latest final package archive from `DemoFinalHandoffReportPackageArchiveRepository`.
- Make the evidence bundle report archive status, download readiness, linked handoff archive, session, delivery receipt, task certificate, summary, next action, archive count, archived time, and download actions.
- Add the same evidence to the copied demo runbook.
- Render the final package archive evidence in the dashboard evidence bundle panel.
- Update README, execution log, and tests.

## Non-Goals

- Do not create final handoff report package archives automatically.
- Do not replace the session panel archive controls.
- Do not create tasks, call the model, run verification commands, mutate GitHub, or send external messages from evidence-bundle reads.

## Validation

- Backend RED tests first for ready and missing archive evidence in `DemoEvidenceBundleService`, copied runbook Markdown, and REST serialization.
- Frontend RED tests first for rendering final package archive proof and missing-archive guidance in `DemoEvidenceBundlePanel`.
- Full backend test suite, full frontend test suite, frontend build, and `git diff --check` before merge.
