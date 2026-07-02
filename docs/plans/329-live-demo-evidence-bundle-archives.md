# Live Demo Evidence Bundle Archives

## Goal

Freeze the final live demo evidence bundle into a stable local archive after a real `/agent fix` run, so the operator can preserve one immutable handoff artifact even when later dashboard refreshes or demo runs change the current bundle.

## Why This Matters

The current evidence bundle is generated on demand from the latest launch package and outcome closeout archives. A reviewer handoff needs a durable snapshot with its own archive id, archive timestamp, report body, task evidence, webhook evidence, and Pull Request link.

## Scope

- Add a backend live demo evidence bundle archive VO, capped in-memory repository, archive service, and admin-protected controller endpoints.
- Support creating an archive from the current bundle, listing recent archives, and downloading an archived Markdown report.
- Freeze bundle status, handoff readiness, repository, issue, trigger user/comment, launch archive, closeout archive, task, webhook delivery, Pull Request URL, evidence notes, next actions, side-effect contract, bundle generation time, archive time, and report.
- Add frontend API helpers, App-level loading/handlers, a live launch gate archive button, archive error feedback, recent archive history, and archived report downloads.

## Out of Scope

- MySQL-backed archive persistence.
- Automatic archive creation on task completion.
- Any GitHub, queue, task, branch, or Pull Request mutation.

## Verification

- Backend archive service and controller tests must fail first, then pass after implementation.
- Frontend API and panel tests must fail first, then pass after implementation.
- Full Maven tests, full Vitest suite, frontend production build, `git diff --check`, and secret scan must pass before merge.
