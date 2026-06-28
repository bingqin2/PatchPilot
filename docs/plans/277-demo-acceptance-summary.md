# 277 Demo Acceptance Summary

## Goal

Provide one final, read-only demo acceptance summary that combines the latest archived launch acceptance certificate and the latest archived task evidence acceptance certificate.

## Why This Matters

The demo now has strong launch-level and task-level certificates, but operators still need one final answer to the external-review question: is the whole self-hosted issue-to-PR demo accepted, and which evidence should be shared?

## Scope

- Add a backend read model that summarizes latest launch certificate archive evidence, latest task certificate archive evidence, acceptance checks, evidence notes, download actions, and a Markdown report.
- Add `GET /api/demo/acceptance-summary` and `GET /api/demo/acceptance-summary/report/download`.
- Render a `Final demo acceptance` dashboard panel with accepted/not-accepted state, certificate archive identifiers, checks, read-only side-effect contract, and report download.
- Update frontend API helpers, TypeScript types, App data loading, README, product docs, frontend design docs, and execution log.

## Non-Goals

- Do not create certificate archives automatically.
- Do not send messages or deliver evidence externally.
- Do not create tasks, call the model, run verification commands, mutate Git, record receipts, archive records, or write to GitHub from the summary endpoints.

## Validation

- Backend RED tests first for the acceptance summary service and controller endpoints.
- Frontend RED tests first for API helpers and final acceptance panel rendering/download behavior.
- Full backend tests, full frontend tests, frontend build, and `git diff --check` before merge.
