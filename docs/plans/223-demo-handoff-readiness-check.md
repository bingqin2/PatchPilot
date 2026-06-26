# 223 - Demo Handoff Readiness Check

## Goal

Make demo session handoff artifacts self-checking by showing whether the current handoff package has enough evidence for a credible live-demo handoff before an operator copies, downloads, or archives it.

## Scope

- Add a `Handoff Readiness` section to session reports and handoff packages.
- Check current session status, completed recent task evidence, recent Pull Request evidence, prepared launch command context, archived launch outcome evidence, and readiness snapshot trend baseline.
- Mark the handoff as `READY`, `NEEDS_ATTENTION`, or `BLOCKED` with concrete missing-evidence guidance.
- Render the same readiness summary in the dashboard demo session snapshot panel.
- Update README, product spec, architecture notes, frontend design docs, and execution log.

## Non-Goals

- Do not create a new persistence table or archive type.
- Do not call the model, create tasks, run verification commands, mutate Git, archive records, or write to GitHub.
- Do not replace the broader demo readiness gate; this check only validates handoff evidence completeness.

## Validation

- Backend focused tests for ready and missing-evidence Markdown handoff readiness.
- Frontend focused tests for ready and missing-evidence dashboard handoff readiness.
- Full backend test suite, full frontend test suite, production frontend build, and `git diff --check`.
