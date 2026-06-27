# Self-Hosted Launch Readiness Archive

## Goal

Persist final self-hosted launch readiness packages as local evidence so an operator can keep, reopen, and download the exact pre-launch decision after the current runtime state changes.

## Scope

- Add a local archive model for `GET /api/demo/self-hosted-launch-readiness` output.
- Support in-memory storage for the default profile and MySQL storage for `local`, `docker`, and `idea`.
- Add APIs to archive the current package, list recent archives, and download one archived Markdown report.
- Add dashboard actions to archive the current package, display recent archive rows, and download archived reports.
- Update README, product docs, frontend design docs, and execution log.

## Non-Goals

- Do not create tasks, call the model, run tests, mutate Git, send messages, redeliver webhooks, or write to GitHub.
- Do not replace readiness snapshots, session archives, or handoff package archives.
- Do not add public sharing or upload behavior.

## Validation

- Backend RED/GREEN tests for service, in-memory repository, MyBatis repository, migration text, and controller endpoints.
- Frontend RED/GREEN tests for API bindings, dashboard rendering, archive action, and archived report download.
- Full regression before merge:
  - `mvn -pl PatchPilot test`
  - `npm test -- --reporter=dot`
  - `npm run build`
  - `git diff --check`
