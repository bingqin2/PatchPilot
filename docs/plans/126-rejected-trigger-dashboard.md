# 126 Rejected Trigger Dashboard

## Goal

Make rejected `/agent fix` attempts visible in the React dashboard so operators can answer why no task was created without using curl or GitHub delivery payload inspection.

This supports the self-hosted issue-to-PR demo goal by making safety decisions observable. A maintainer should be able to distinguish a real backend problem from an intentionally rejected vague, unsafe, unauthorized, rate-limited, or model-rejected trigger.

## Scope

- Reuse the existing backend `GET /api/rejected-triggers?limit=...` endpoint.
- Add a typed frontend API helper and dashboard data loading path.
- Render a dedicated rejected-trigger panel with source, repository, issue, trigger user, command text, delivery id, timestamp, and rejection reason.
- Keep rejected-trigger API failures local to the panel so the rest of the dashboard can still load.
- Update README, frontend design notes, and execution progress.

## Non-Goals

- Do not add new backend persistence or safety rules.
- Do not expose raw webhook payloads or signatures.
- Do not add browser-side retry or redelivery controls for rejected triggers.

## Validation

- `npm test -- api.test.ts App.test.tsx RejectedTriggerPanel.test.tsx`
- `npm test`
- `npm run build`
- `mvn -pl PatchPilot test`
- `git diff --check`
