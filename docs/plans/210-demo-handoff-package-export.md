# 210 - Demo Handoff Package Export

## Goal

Export a complete Markdown handoff package for a live PatchPilot demo.

## Why This Matters

The dashboard can prepare commands, track launch outcomes, and include that context in session reports. The next maturity step is a single handoff artifact that reviewers can read after the demo without opening multiple panels. It should preserve setup state, prepared commands, observed outcomes, task and Pull Request evidence, and next actions in one copyable/downloadable package.

## Scope

- Add a backend handoff package formatter that wraps the current session report with a concise handoff summary.
- Add `POST /api/demo/handoff-package` for copy actions.
- Add `POST /api/demo/handoff-package/download` for Markdown downloads.
- Reuse `DemoSessionReportRequestDto` so browser-local prepared commands and archived outcomes are included.
- Add frontend API helpers and demo session snapshot actions for copying and downloading the package.
- Keep the feature read-only: no task creation, queue mutation, model call, Git command, GitHub mutation, or backend archive write.
- Update README, product docs, and execution log.

## Out of Scope

- Persisting handoff packages.
- Posting handoff packages to GitHub automatically.
- Replacing existing session report or session archive actions.

## Validation

- RED backend tests should fail because the handoff package formatter and endpoints do not exist.
- RED frontend tests should fail because the API helpers and panel actions do not exist.
- Focused backend tests should cover Markdown contents, controller POST binding, and download headers.
- Focused frontend tests should cover API request bodies and panel copy/download actions.
- Full frontend/backend regression verification should run before merge.
