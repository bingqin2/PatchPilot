# 217 - Evaluation Baseline Run History

## Goal

Turn on-demand evaluation fixture baseline execution into durable local demo evidence. Operators should be able to run the checked-in fixture baseline, review recent baseline runs after refresh, and copy or download the exact Markdown report that was produced.

## Scope

- Add an evaluation fixture baseline run archive model, repository, service, and controller endpoints.
- Support the default in-memory profile and MySQL-backed `local`, `docker`, and `idea` profiles.
- Add Flyway schema coverage for persisted baseline run archives.
- Keep command selection inside the existing fixture-baseline service so only supported checked-in fixtures run adapter-selected verification commands.
- Add dashboard API helpers and UI actions to run and archive the baseline, list recent runs, copy archived reports, and download archived reports.
- Update README, product docs, frontend design notes, and the execution log.

## API Contract

- `POST /api/evaluation/fixture-baseline-runs`: execute the supported fixture baseline and archive the resulting Markdown report.
- `GET /api/evaluation/fixture-baseline-runs`: list recent archived fixture baseline runs.
- `GET /api/evaluation/fixture-baseline-runs/{runId}/report/download`: download one archived Markdown report.

The POST endpoint runs only checked-in local fixture verification commands selected by the adapter registry. It does not create tasks, call the model, mutate Git, push branches, open Pull Requests, or write to GitHub.

## Non-Goals

- No arbitrary command or fixture-path input.
- No model-backed benchmark execution.
- No comparison across model, prompt, adapter, or repository revisions.
- No replacement for future full `evaluation_run` records.

## Validation

- Backend focused tests cover service archiving, in-memory/MyBatis repositories, Flyway migration, controller list/download endpoints, and the run-and-archive flow.
- Frontend focused tests cover API helpers, dashboard loading, run-and-archive action, copy action, download action, and App-level integration.
- Full backend and frontend regression checks run before merging.
