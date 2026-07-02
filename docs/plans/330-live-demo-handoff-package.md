# Live Demo Handoff Package

## Goal

Produce a reviewer-facing handoff package from the latest archived live demo evidence bundle. This closes the live `/agent fix` demo path by turning the frozen local evidence archive into a concise review checklist, delivery instructions, and downloadable Markdown report.

## Scope

- Add an admin-protected backend JSON endpoint at `GET /api/demo/live-demo-handoff-package`.
- Add an admin-protected Markdown download endpoint at `GET /api/demo/live-demo-handoff-package/report/download`.
- Classify the package as `READY`, `NEEDS_ATTENTION`, or `BLOCKED` from the newest live demo evidence bundle archive.
- Surface the package in the live launch gate dashboard with refresh and download controls.
- Keep the package read-only: no task creation, GitHub writes, model calls, archive writes, Git mutation, or repository changes.

## Backend Contract

The package includes the evidence bundle archive id, repository, issue, trigger comment, task status, Pull Request URL, webhook delivery id, review checklist, delivery instructions, evidence notes, generated timestamp, side-effect contract, and Markdown report.

`READY` requires the latest evidence bundle archive to be ready for handoff. Missing archives produce `BLOCKED`; non-ready archives produce `NEEDS_ATTENTION`.

## Frontend Contract

The live launch gate panel shows:

- `Refresh handoff package`
- `Download handoff package`
- package readiness summary
- evidence archive, task, Pull Request, and webhook identifiers
- review checklist, delivery instructions, and evidence notes

## Verification

- Focused backend tests: `DemoLiveDemoHandoffPackageServiceTests`, `DemoLiveDemoHandoffPackageControllerTests`
- Focused frontend tests: `src/api.test.ts`, `src/dashboard/components/LiveLaunchGatePanel.test.tsx`
- Full backend tests: `mvn -pl PatchPilot test`
- Full frontend tests and build: `npm --prefix frontend test -- --reporter=dot`, `npm --prefix frontend run build`
