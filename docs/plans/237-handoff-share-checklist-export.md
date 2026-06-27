# Handoff Share Checklist Export

## Goal

Make the latest handoff share checklist portable and visible from the top-level
demo evidence flow. Operators should be able to download the checklist as
Markdown and see the current share-readiness status without opening the full
session snapshot first.

## Scope

- Add a read-only backend download endpoint for the handoff share checklist
  Markdown report.
- Add the current handoff share checklist status, summary, and next action to
  the demo evidence bundle.
- Show that evidence in the dashboard evidence bundle panel.
- Add a dashboard action that downloads the handoff share checklist Markdown
  from the session snapshot panel.
- Keep this feature read-only: no tasks, model calls, test execution, Git
  mutation, archive creation, or GitHub writes.

## API

`GET /api/demo/handoff-share-checklist/report/download` returns the existing
checklist Markdown as a `text/markdown` attachment named
`patchpilot-demo-handoff-share-checklist.md`.

`GET /api/demo/evidence-bundle` includes:

- `handoffShareChecklistStatus`
- `handoffShareChecklistSummary`
- `handoffShareChecklistNextAction`

## Dashboard Behavior

- The evidence bundle panel shows a `Handoff share checklist` evidence card.
- The session snapshot panel keeps the existing copy action and adds
  `Download checklist`.
- Download status feedback is scoped to the same session report action area.

## Validation

- Backend focused tests cover evidence bundle serialization, evidence bundle
  service construction, checklist report download headers, and archive summary
  dependency wiring.
- Frontend focused tests cover the API helper, evidence bundle rendering,
  session snapshot download action, and App-level download integration.
- Full backend tests, full frontend tests, frontend production build, and
  `git diff --check` should pass before merge.
