# 185 GitHub Verification Result Evidence

## Goal

Make GitHub-facing feedback show not only which verification command PatchPilot selected, but also the actual verification result when a test run exists. Maintainers should be able to inspect a Pull Request or issue status comment and see whether verification ran, which command ran, the exit code, and the duration without opening the dashboard.

## Scope

- Add actual verification result evidence to Pull Request bodies after successful verification.
- Add actual verification result evidence to completed issue comments.
- Add actual verification result evidence to failed issue comments when a failed test run exists.
- Add a clear "verification not run" statement to `PENDING_REVIEW` issue comments when the generated-diff risk gate pauses a task before verification.
- Wire `FixTaskWorker` to read the latest test-run record before terminal issue-comment updates.
- Pass the recorded test-run evidence from the executor into Pull Request creation.

## Out of Scope

- Exposing full stdout or stderr in GitHub comments.
- Adding new database columns.
- Changing dashboard task detail rendering.
- Changing retry or review approval semantics.

## Validation

- Focused tests should fail first because PR and issue comments do not accept or render test-run evidence.
- Focused tests should pass after the PR tool, issue comment tool, executor, and worker are wired.
- Full backend and frontend regression checks must pass before handoff.
