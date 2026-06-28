# 264 Task Evidence Archive Review Center

## Goal

Expose task evidence package archives as a first-class review surface instead of hiding them behind the selected task detail panel. This improves demo review and operator handoff because a reviewer can see recent archived task evidence across tasks, identify the latest task, and download archived reports without manually opening each task first.

## Scope

- Add read-only backend APIs for recent task evidence package archives and aggregate summary counts.
- Add a dashboard review panel that shows archive totals, latest task evidence, side-effect contract, next action, and recent archive download actions.
- Keep archive creation unchanged and protected by the existing admin audit path.
- Document the new endpoints and dashboard behavior.

## Out of Scope

- No external sharing or delivery receipt workflow for task evidence archives.
- No mutation of tasks, Git, GitHub, models, or verification commands from the review endpoints.
- No new database table; this reuses the existing task evidence package archive storage.

## Verification

- Backend controller tests cover recent archive listing and summary output.
- Frontend API and component tests cover endpoint paths, summary rendering, task selection, and report download.
- Full verification should include `mvn -pl PatchPilot test`, `npm test`, `npm run build`, and `git diff --check`.
