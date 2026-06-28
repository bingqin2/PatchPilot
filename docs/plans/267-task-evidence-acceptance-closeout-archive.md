# 267 Task Evidence Acceptance Closeout Archive

## Goal

Preserve finalized task evidence acceptance as a durable PatchPilot-local archive so an operator can prove a task evidence package was shared, delivered, finalized, and accepted after the live read model changes.

## Scope

- Add persistent acceptance closeout archive storage for default, local, docker, and idea profiles.
- Add backend APIs to create, list, and download task evidence acceptance closeout archive reports.
- Require the current task evidence finalization gate to be `READY` before creating a closeout archive.
- Add dashboard controls that show recent closeout archives, create a closeout, and download archived Markdown.
- Document commands and the side-effect boundary.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run tests, mutate Git, push branches, open Pull Requests, or write GitHub comments.
- Do not replace task evidence archives, share-center reports, delivery receipts, or finalization reports.

## Acceptance Criteria

- `POST /api/tasks/evidence-packages/acceptance-closeout/archives` stores a local archive only when finalization is `READY`.
- `GET /api/tasks/evidence-packages/acceptance-closeout/archives` lists recent archives.
- `GET /api/tasks/evidence-packages/acceptance-closeout/archives/{archiveId}/report/download` downloads the archived Markdown report.
- The dashboard exposes create/list/download flows in the task evidence review panel.
- Backend and frontend tests cover persistence conversion, repositories, migration, service behavior, controller routes, API helpers, component rendering, and dashboard integration.
