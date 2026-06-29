# 288 Final Acceptance Completion Closeout Archive

## Goal

Make the final acceptance completion closeout durable after the live closeout readout changes, so an operator can freeze the final external-review completion proof and download it later.

## Scope

- Add a backend closeout archive VO, entity, converter, mapper, repository, service, and Flyway migration.
- Add archive/list/download endpoints under `/api/demo/final-acceptance-completion-closeout/archives`.
- Require the current closeout to be `READY` and `closed` before archive creation.
- Record protected admin audit evidence for archive creation.
- Add frontend type/API helpers, App loading/refresh handlers, dashboard archive button, recent archive list, and archived report download.
- Update README, product spec, frontend design notes, and execution log.

## Behavior

- `POST /api/demo/final-acceptance-completion-closeout/archives` stores the current closeout only when it is ready and closed.
- `GET /api/demo/final-acceptance-completion-closeout/archives` returns recent archives newest first.
- `GET /api/demo/final-acceptance-completion-closeout/archives/{archiveId}/report/download` returns the frozen Markdown closeout report.
- Archive creation writes PatchPilot-local evidence and protected admin audit only; it does not send messages, create tasks, call the model, run tests, mutate Git, record receipts, or write to GitHub.

## Validation

- Backend service, converter, in-memory repository, MyBatis repository, migration, and controller tests.
- Frontend API, final acceptance panel, and App loading tests.
- Full backend tests, full frontend tests, production build, and `git diff --check`.
