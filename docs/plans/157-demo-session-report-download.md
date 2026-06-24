# Plan 157: Demo Session Report Download

## Goal

Make the demo session report exportable as a real Markdown file from both curl/API clients and the dashboard. This closes the handoff gap left by copy-only reports: an operator can keep the current session report or an archived report as a portable `.md` artifact after a live demo.

## Scope

- Add a read-only current report download endpoint that returns `text/markdown` with `Content-Disposition: attachment`.
- Add a read-only archived report download endpoint keyed by archive id.
- Preserve the existing JSON report and archive-list contracts.
- Add dashboard actions to download the current session report and individual archived reports.
- Keep the download path protected by the same admin-token behavior as other operator APIs.

## Out of Scope

- Changing report content or archive persistence.
- Creating tasks, calling the model, running tests, mutating Git, writing GitHub comments, or opening Pull Requests.
- Bulk archive export or ZIP packaging.

## Verification

- Backend controller tests cover Markdown attachment headers, body content, and archived-report not-found behavior.
- Archive service and repository tests cover archive lookup by id for in-memory and MyBatis repositories.
- Frontend API tests cover Blob downloads for current and archived reports.
- Dashboard component tests cover current and archived download button behavior.
