# 214 - Evaluation Run Snapshot Archive

## Goal

Turn the read-only evaluation run preview into durable local evidence that an operator can archive during a demo or review. This is still not an automated benchmark runner: it stores a snapshot of the current checked-in evaluation metadata and Markdown report, but it must not call the model, clone repositories, run verification commands, mutate Git, or write to GitHub.

## Scope

- Add an evaluation run snapshot archive model, repository, service, and controller endpoints.
- Support the default in-memory profile and MySQL-backed `local`, `docker`, and `idea` profiles.
- Add Flyway schema coverage for persisted archives.
- Add dashboard API helpers and a UI action to archive the current preview and inspect recent archives.
- Update README and progress logs with the new evidence workflow.

## API Contract

- `POST /api/evaluation/run-snapshots`: archive the current evaluation run preview as PatchPilot-local state.
- `GET /api/evaluation/run-snapshots`: list the latest archived snapshots.
- `GET /api/evaluation/run-snapshots/{snapshotId}/report/download`: download one archived Markdown report.

The POST endpoint is protected by the existing admin API filter. The archive record stores only non-secret catalog metadata and Markdown evidence.

## Validation

- Backend focused tests for service, controller, repository conversion, and migration.
- Frontend focused tests for API helpers and the evaluation catalog panel archive workflow.
- Regression checks: backend evaluation tests, frontend test/build, and `git diff --check`.
