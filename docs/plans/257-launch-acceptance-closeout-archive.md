# Plan 257: Launch Acceptance Closeout Archive

## Goal

Make the final launch acceptance closeout durable and reviewable after refresh or backend restart, so the self-hosted issue-to-PR demo has one final archived acceptance record.

## Scope

- Add a `DemoLaunchAcceptanceCloseoutArchiveService` that captures the current launch acceptance closeout.
- Add archive read model, repository interface, in-memory repository, MyBatis repository, entity, mapper, converter, and Flyway migration.
- Add protected archive endpoints:
  - `POST /api/demo/launch-acceptance-closeout/archives`
  - `GET /api/demo/launch-acceptance-closeout/archives`
  - `GET /api/demo/launch-acceptance-closeout/archives/{archiveId}/report/download`
- Record protected admin audit evidence when an operator archives the closeout.
- Extend the dashboard launch evidence package panel with:
  - `Archive closeout`
  - recent archived closeouts
  - archived report downloads
- Refresh archive history after a new closeout archive is created.
- Update README, product spec, frontend design notes, and execution log.

## Archive Fields

Each archive stores:

- id
- status
- accepted
- summary
- session id
- latest task id
- latest Pull Request URL
- latest webhook delivery id
- evaluation run id
- latest launch archive id
- latest delivery receipt id
- latest delivery target
- latest delivery channel
- delivery receipt freshness
- created time
- Markdown report

## Side-Effect Contract

Archive creation is a PatchPilot-local evidence write only. It must not create tasks, call the model, run tests, mutate Git, send messages, record delivery receipts, or write to GitHub.

## Validation

- Backend tests first fail because the closeout archive service, repository, migration, and endpoints do not exist.
- Frontend tests first fail because the closeout archive API helpers and panel controls do not exist.
- Run targeted backend/frontend tests, full backend tests, full frontend tests, production build, and `git diff --check` before merge.
