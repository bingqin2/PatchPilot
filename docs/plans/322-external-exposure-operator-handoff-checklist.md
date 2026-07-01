# 322 External Exposure Operator Handoff Checklist

## Goal

Provide one read-only operator checklist after a temporary public URL has been closed. The checklist should say whether the latest external exposure closeout archive, handoff package, session state, and live GitHub publish preflight are ready for the next live `/agent fix` step.

## Scope

- Add a backend read model at `GET /api/security/external-exposure-operator-handoff-checklist`.
- Add a Markdown download at `GET /api/security/external-exposure-operator-handoff-checklist/report/download`.
- Aggregate latest closeout archive evidence, current handoff package, recent exposure sessions, and live publish preflight for the configured demo repository.
- Add a dashboard panel with status counts, evidence notes, checks, next actions, report download, and refresh.
- Update product, architecture, frontend, README, and progress notes.

## Safety Contract

The checklist must not create tasks, call the model, run tests, probe public URLs, mutate Git, create branches, open Pull Requests, edit GitHub webhook settings, write GitHub comments, archive records, send messages, or expose secrets.

## Validation

- [x] Backend service and controller tests fail before implementation.
- [x] Frontend API and panel tests fail before implementation.
- [x] Targeted backend and frontend tests pass.
- [x] Service regression test proves `READY` status with false readiness flags is not treated as ready.
- [x] Full backend and frontend test suites pass.
- [x] Frontend build passes.
- [x] Secret scan and `git diff --check` pass before commit.
