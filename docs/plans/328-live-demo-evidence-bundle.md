# Live Demo Evidence Bundle

## Goal

Provide one final, read-only evidence bundle after a real `/agent fix` run so the operator can hand off a stable record of launch preparation, trigger execution, task outcome, webhook delivery, and Pull Request result.

## Why This Matters

The live demo flow now has separate launch package and outcome closeout archives. Reviewers still need a single summary artifact that answers whether the end-to-end run is complete, which archived records prove it, and what to do next.

## Scope

- Add a backend live demo evidence bundle service and admin-protected controller.
- Aggregate the most recent live trigger launch package archive and most recent live trigger outcome closeout archive.
- Classify the bundle as `READY` only when the launch package was ready, the closeout archive is successful, and the closeout references the same launch package archive.
- Expose JSON and Markdown download endpoints.
- Add frontend API helpers, App state/handlers, and Live launch gate dashboard rendering and download controls.

## Out of Scope

- New persistence tables.
- Automatic evidence-bundle archival.
- Creating or mutating GitHub issues, tasks, branches, or Pull Requests.

## Verification

- Backend service and controller tests must fail first, then pass after implementation.
- Frontend API and panel tests must fail first, then pass after implementation.
- Full Maven tests, full Vitest suite, frontend production build, `git diff --check`, and secret scan must pass before merge.
