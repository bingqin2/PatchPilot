# 239 Demo Share Center Evidence Bundle

## Goal

Surface the final demo handoff share center directly in the top-level demo evidence bundle so operators can decide whether post-demo evidence is ready to send without opening the full session snapshot first.

## Problem

The share center already combines the latest handoff package archive summary and share checklist into a final send/no-send view. However, the evidence bundle still only surfaced the checklist status. Operators using the first-screen demo readout had to open the session snapshot to see the final share-center summary and download actions.

## Scope

- Add share-center status, summary, next action, and download actions to the backend evidence bundle read model.
- Reuse the existing share-center service so evidence bundle readiness is derived from the same rule source as the standalone share-center endpoint.
- Render a `Handoff share center` record in the dashboard evidence bundle panel.
- Update dashboard and API tests so the expanded contract is covered.
- Update product, architecture, frontend design, README, and execution-log documentation.

## Out of Scope

- Creating new archive records from the evidence bundle.
- Adding new task-triggering paths.
- Changing share-center readiness rules.
- Persisting browser-local command history on the backend.

## Validation

- Start with failing backend tests that expect share-center fields in `DemoEvidenceBundleVo`.
- Start with failing frontend tests that expect the evidence bundle panel to render share-center evidence.
- Pass focused backend and frontend tests after implementation.
- Run full backend tests, frontend tests, frontend production build, and `git diff --check` before merge.
