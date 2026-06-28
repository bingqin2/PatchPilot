# 268 Task Evidence Acceptance Certificate

## Goal

Create one final task evidence acceptance certificate from the latest accepted task evidence closeout archive. This gives operators a single shareable proof that a completed Pull Request-backed task evidence package was archived, delivered, finalized, accepted, and certified.

## Scope

- Add a backend certificate read model derived from the latest task evidence acceptance closeout archive.
- Add persistent certificate archive storage for default, local, docker, and idea profiles.
- Add protected APIs to read the current certificate, download its Markdown report, create a certificate archive, list recent archives, and download archived reports.
- Add dashboard controls for certificate status, current report download, certificate archive creation, archive history, and archived report download.
- Document operator commands, side-effect boundaries, and validation evidence.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run tests, mutate Git, push branches, open Pull Requests, or write GitHub comments.
- Do not replace task evidence archives, share-center reports, delivery receipts, finalization reports, or acceptance closeout archives.

## Acceptance Criteria

- `GET /api/tasks/evidence-packages/acceptance-certificate` reports certified only when the latest closeout archive is `READY` and accepted.
- `GET /api/tasks/evidence-packages/acceptance-certificate/report/download` downloads the current Markdown certificate.
- `POST /api/tasks/evidence-packages/acceptance-certificate/archives` stores a local archive only when the current certificate is certified.
- `GET /api/tasks/evidence-packages/acceptance-certificate/archives` lists recent certificate archives newest first.
- `GET /api/tasks/evidence-packages/acceptance-certificate/archives/{archiveId}/report/download` downloads one archived certificate report.
- The dashboard exposes the current certificate, archive controls, archive history, and download actions in the task evidence review panel.
- Backend and frontend tests cover services, persistence conversion, repositories, migration, controller routes, API helpers, component rendering, and dashboard integration.
