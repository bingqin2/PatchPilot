# Live Trigger Outcome Closeout Archives

## Goal

Freeze each live trigger outcome closeout into a local, downloadable archive so the operator can keep stable proof after a real `/agent fix` run creates or fails to create a Pull Request.

## Why This Matters

The current live trigger closeout is generated from mutable task and launch-package state. A demo reviewer needs a durable artifact that captures the final result, the related task, the Pull Request URL, the webhook delivery, and the next action at the moment the operator finishes the live test.

## Scope

- Add backend archive storage for live trigger outcome closeouts.
- Add admin-protected API endpoints to create, list, and download closeout archive reports.
- Add dashboard controls to archive the current closeout, list recent archives, and download archived reports.
- Keep archive creation local to PatchPilot; it must not mutate GitHub, repositories, queues, or task state.

## Out of Scope

- Persistent MySQL storage for closeout archives.
- Automatic archive creation when a task finishes.
- Changing the existing closeout decision logic.

## Verification

- Backend service and controller tests prove archive creation, listing, and report download.
- Frontend API and panel tests prove archive calls, rendering, and download actions.
- Full backend Maven tests, frontend Vitest suite, frontend production build, diff whitespace check, and secret scan must pass before merge.
