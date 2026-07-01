# 323 External Exposure Operator Handoff Archives

## Goal

Preserve the current external exposure operator handoff checklist as local evidence before the operator posts another live `/agent fix` trigger. This closes the gap between the real-time checklist and durable proof of the decision that was visible at handoff time.

## Scope

- Add a backend local archive service for the current operator handoff checklist.
- Add endpoints:
  - `POST /api/security/external-exposure-operator-handoff-checklist/archives`
  - `GET /api/security/external-exposure-operator-handoff-checklist/archives`
  - `GET /api/security/external-exposure-operator-handoff-checklist/archives/{archiveId}/report/download`
- Store capped in-memory archives for the running backend process.
- Add dashboard actions to archive the current checklist, list recent archives, and download archived Markdown.
- Update API types, App loading/refresh wiring, README, product spec, architecture, frontend design, and execution log.

## Safety Contract

Archive creation is a PatchPilot-local evidence write only. It must not create tasks, call the model, run tests, probe public URLs, mutate Git, create branches, open Pull Requests, edit GitHub webhook settings, write GitHub comments, send messages, or expose secrets.

## Validation

- [x] Backend service and controller tests fail before implementation.
- [x] Frontend API and panel tests fail before implementation.
- [x] Targeted backend and frontend tests pass.
- [x] App smoke test passes.
- [x] Full backend and frontend test suites pass.
- [x] Frontend build passes.
- [x] Secret scan and `git diff --check` pass before commit.
