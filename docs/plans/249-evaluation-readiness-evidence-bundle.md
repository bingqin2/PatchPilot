# 249 - Evaluation Readiness Evidence Bundle

## Goal

Carry full evaluation run readiness into the top-level demo evidence bundle. After `248-evaluation-run-readiness-gate`, demo readiness can block on missing or failed full evaluation archives, but the one-call evidence bundle still does not explain the latest evaluation run, coverage, safety categories, deltas, or next action.

## Scope

- Add a compact `evaluationRunReadiness` evidence object to `GET /api/demo/evidence-bundle`.
- Derive the evidence from `EvaluationRunArchiveReadinessSummaryService`.
- Let the evidence bundle status and next actions reflect missing or blocked evaluation run archives.
- Add the same evidence to the generated demo runbook Markdown.
- Render the evaluation evidence in the dashboard demo evidence bundle panel.
- Update README, product spec, architecture, frontend design notes, and progress logs.

## Non-Goals

- Do not run evaluation commands from the evidence bundle.
- Do not create tasks, call the model, mutate Git, open Pull Requests, send GitHub comments, or write to GitHub.
- Do not duplicate the full evaluation catalog panel; the bundle only shows the summary needed for launch and handoff decisions.

## Validation

- Backend RED/GREEN tests for evidence-bundle aggregation, REST serialization, and runbook Markdown.
- Frontend RED/GREEN tests for API typing, panel rendering, and App integration.
- Full backend tests, full frontend tests, production frontend build, and `git diff --check` before merging.
