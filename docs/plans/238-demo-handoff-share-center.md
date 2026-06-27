# Demo Handoff Share Center

## Goal

Give operators one final read-only handoff center after a live demo. The center should combine the latest handoff package archive summary with the handoff share checklist, show whether the package can be sent, and provide a single Markdown report download.

## Scope

- Add a backend read model that aggregates:
  - latest handoff package archive summary,
  - handoff share checklist,
  - share-ready status,
  - concrete download actions,
  - evidence notes,
  - a Markdown report.
- Expose:
  - `GET /api/demo/handoff-share-center`
  - `GET /api/demo/handoff-share-center/report/download`
- Render the center in the dashboard session snapshot panel between archive summary and share checklist.
- Refresh the center after archiving a new handoff package.
- Keep the endpoint read-only. It must not create tasks, call the model, run tests, mutate Git, archive records, or write to GitHub.

## Out of Scope

- No new persistence table.
- No new archive mutation endpoint.
- No outbound email, Slack, or GitHub posting.
- No change to handoff package generation rules.

## Acceptance Criteria

- Operators can inspect one final share center that states whether the latest handoff package archive is ready to send.
- Operators can download `patchpilot-demo-handoff-share-center.md`.
- Missing archive or checklist warnings produce `NEEDS_ATTENTION` with concrete next action.
- Existing handoff archive summary and checklist remain available separately.
- Backend and frontend tests cover API, aggregation behavior, dashboard rendering, and download wiring.
