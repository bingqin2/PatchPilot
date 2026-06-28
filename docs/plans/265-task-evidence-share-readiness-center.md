# 265 Task Evidence Share Readiness Center

## Goal

Turn archived task evidence packages into an operator-ready sharing decision. After a task evidence archive exists, operators need a clear answer for whether there is a completed Pull Request-backed archive that can be sent to reviewers, which report to download, and what evidence is missing if the archive set is not share-ready.

## Scope

- Add read-only backend APIs for task evidence share readiness and downloadable Markdown.
- Select the newest archived task evidence package that is `COMPLETED` and has a Pull Request URL as the shareable package.
- Surface `READY`, `NEEDS_ATTENTION`, or `BLOCKED` status, archive counts, latest archive evidence, shareable archive evidence, download actions, evidence notes, and a read-only side-effect contract.
- Add dashboard support inside the task evidence archive review panel so operators can inspect share readiness and download the share center report without opening each task.
- Update README and progress notes.

## Out of Scope

- No external delivery receipts for task evidence packages.
- No sending, GitHub mutation, task creation, model calls, verification commands, Git operations, or Pull Request creation from the share center.
- No new persistence table; this reuses existing task evidence package archives.

## Verification

- Backend controller tests cover share-center JSON and Markdown report download.
- Frontend API, component, and App integration tests cover endpoint paths, readiness rendering, and report downloads.
- Full verification should include `mvn -pl PatchPilot test`, `npm test`, `npm run build`, and `git diff --check`.
