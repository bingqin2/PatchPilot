# Handoff Archive Summary Evidence Actions

## Goal

Make the latest demo handoff package archive summary directly portable from the dashboard. Operators should be able to copy or download the share-ready summary evidence without opening an individual archive or manually assembling notes.

## Scope

- Add a read-only backend Markdown download endpoint for the handoff package archive summary.
- Add a frontend API helper for the summary report download.
- Add `Copy summary` and `Download summary` actions to the handoff package archive summary panel.
- Keep the summary download separate from per-archive downloads to avoid route ambiguity with archive ids.
- Update product docs and execution history.

## Non-Goals

- Do not create another archive record.
- Do not call the model, clone repositories, run tests, mutate Git, or write to GitHub.
- Do not change handoff readiness scoring.
- Do not add browser-local persistence for summary downloads.

## API Design

`GET /api/demo/handoff-package-archives/summary-report/download` returns the existing archive summary Markdown as a `text/markdown` attachment named `patchpilot-demo-handoff-package-archive-summary.md`.

The existing JSON endpoint remains unchanged:

`GET /api/demo/handoff-package-archives/summary`

## Dashboard Design

`DemoSessionSnapshotPanel` continues to show the handoff package archive summary above recent handoff package archives. When a summary is present, the panel now offers:

- `Copy summary`: copies the already loaded `markdownReport` field to the clipboard.
- `Download summary`: fetches the backend Markdown attachment and downloads it as `patchpilot-demo-handoff-package-archive-summary.md`.

Both actions reuse the panel's existing copy/download status feedback.

## Verification

- Backend controller test proves the new download endpoint returns a Markdown attachment.
- Frontend API test proves the helper calls the new endpoint.
- Component tests prove summary copy/download actions are rendered and update operator feedback.
- App test proves the dashboard button is wired to the real API helper path.
