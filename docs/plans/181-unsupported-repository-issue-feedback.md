# Unsupported Repository Issue Feedback

## Goal

Make unsupported repository failures clear to the GitHub issue author, not only to dashboard operators. When adapter preflight fails during a real task, the issue-facing failure comment should explain that PatchPilot stopped before model patch generation, tests, Git mutation, or Pull Request creation, then list supported repository shapes and safe next actions.

## Scope

- Extend failed task issue comments for `UNSUPPORTED_REPOSITORY`.
- Reuse the supported language adapter catalog for the issue-facing support matrix.
- Keep GitHub comment failures best-effort and non-blocking for durable task status.
- Cover both comment body formatting and worker failure feedback paths with backend tests.
- Update product documentation and execution progress.

## Non-Goals

- Do not add a new adapter.
- Do not run preflight before cloning from webhook metadata.
- Do not make unsupported repositories retryable without repository changes.
- Do not execute arbitrary user-provided commands for unsupported repositories.

## Validation

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,FixTaskWorkerTests test`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
