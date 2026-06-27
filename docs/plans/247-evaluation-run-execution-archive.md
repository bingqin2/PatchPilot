# Evaluation Run Execution Archive

## Goal

Turn the evaluation surface from a read-only preview into an executable local run that archives measurable fixture and safety-coverage evidence for the self-hosted issue-to-PR demo.

## Scope

- Add an evaluation run model that combines the current catalog preview, a real fixture baseline execution for supported fix cases, and static safety-rejection coverage for rejection cases.
- Persist recent evaluation runs in memory for the default profile and in MySQL for `local`, `docker`, and `idea` profiles.
- Add APIs to run and archive an evaluation, list recent runs, and download a run report.
- Update the dashboard evaluation catalog panel with a `Run evaluation` action, recent run rows, and archived run report downloads.
- Update README, product docs, frontend design docs, and execution log.

## Non-Goals

- Do not create PatchPilot fix tasks, call the model, clone external repositories, mutate Git, create branches, push commits, open Pull Requests, send GitHub comments, or write to GitHub.
- Do not replace the existing fixture baseline run archive; the new evaluation run consumes fixture baseline output and adds higher-level catalog/safety coverage evidence.
- Do not implement hosted benchmark scheduling or model comparison in this slice.

## Design

`EvaluationRunArchiveService` runs `EvaluationFixtureBaselineService.runBaseline()`, reads `EvaluationCaseCatalogService.getEvaluationRunPreview()`, derives coverage counts, and stores one report in an `EvaluationRunArchiveRepository`. The report is `READY` when the preview is ready, the fixture baseline is ready, all supported cases pass, and all safety rejection cases are represented. Otherwise it is `NEEDS_ATTENTION` with a concrete next action.

The dashboard keeps the existing preview and fixture baseline panels, then adds a higher-level recent evaluation run section. Operators can trigger one local run, inspect status/counts, and download the exact Markdown evidence later.

## Validation

- Backend RED/GREEN tests for service behavior, in-memory repository trimming, MyBatis repository, migration text, and controller endpoints.
- Frontend RED/GREEN tests for API bindings, panel rendering, run action, and archived report download.
- Full regression before merge:
  - `mvn -pl PatchPilot test`
  - `npm test -- --reporter=dot`
  - `npm run build`
  - `git diff --check`
