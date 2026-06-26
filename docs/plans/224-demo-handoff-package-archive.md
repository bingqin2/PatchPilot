# 224 Demo Handoff Package Archive

## Goal

Persist the final demo handoff package as durable local evidence so an operator can archive, review, copy, and download the exact package used after a live issue-to-PR demonstration.

## Scope

- Add a backend handoff package archive model, repository, migration, service, and controller endpoints.
- Add dashboard controls to archive the current handoff package and inspect recent package archives.
- Keep session report archives separate from handoff package archives.
- Add tests for API routing, persistence contracts, and dashboard behavior.

## Non-Goals

- Uploading archives to GitHub or external storage.
- Changing the existing handoff package Markdown format.
- Replacing the existing session report archive flow.

## Verification

- `mvn -pl PatchPilot test`
- `npm test -- --reporter=basic`
- `npm run build`
