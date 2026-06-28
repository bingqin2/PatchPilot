# 282 Final Acceptance Completion Archive

## Goal

Preserve the final reviewer-facing acceptance handoff as a durable PatchPilot-local completion archive after the final acceptance share finalization gate is `READY`.

## Scope

- Add a final acceptance completion archive read model that stores the current finalization status, finalized flag, package archive id, task id, delivery receipt id, target/channel, receipt freshness, evidence notes, Markdown report, generated time, and archived time.
- Add in-memory and MyBatis-backed repositories plus Flyway migration for database-backed local profiles.
- Add protected controller endpoints to archive the current finalization, list recent completion archives, and download archived Markdown reports.
- Record protected admin audit evidence when an archive is created.
- Add frontend API helpers, dashboard controls, recent archive rows, and archived report downloads to the final demo acceptance panel.
- Update product docs and execution log.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run verification commands, mutate Git, or write to GitHub.
- Do not change final acceptance share package or delivery receipt creation rules.

## Verification

- RED/GREEN backend tests for service, converter, repository, migration, controller routes, and audit event creation.
- RED/GREEN frontend tests for API helpers, final acceptance panel archive controls, and App data loading.
- Full backend Maven tests, frontend tests, frontend build, and `git diff --check` before merge.
