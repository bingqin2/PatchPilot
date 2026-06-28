# Final Acceptance Completion Evidence Bundle

## Goal

Create a read-only final acceptance completion evidence bundle that summarizes the latest final acceptance finalization, completion archives, share package archive, and delivery receipt into one reviewer-facing proof package.

## Scope

- Add a backend VO and service for `DemoFinalAcceptanceCompletionEvidenceBundle`.
- Expose a JSON API and markdown download endpoint under `/api/demo`.
- Add frontend API helpers, dashboard loading, and a panel section with download action.
- Keep the bundle read-only: it must not create tasks, call models, run tests, mutate Git, send messages, archive records, record receipts, or write to GitHub.

## Acceptance Criteria

- The bundle is `READY` only when final acceptance finalization is ready and at least one completion archive exists.
- The bundle includes the latest completion archive id, share package archive id, delivery receipt id, delivery target, latest task id, evidence notes, download actions, and a side-effect contract.
- Operators can view the bundle in the final demo acceptance panel and download the markdown report.
- Backend and frontend focused tests cover the new API and UI behavior.

## Verification

- `mvn -q -pl PatchPilot -Dtest=DemoFinalAcceptanceCompletionEvidenceBundleServiceTests,DemoReadinessControllerTests test`
- `npm test -- src/api.test.ts src/dashboard/components/DemoAcceptanceSummaryPanel.test.tsx src/App.test.tsx --reporter=basic`
- `mvn -q -pl PatchPilot test`
- `npm test -- --reporter=basic`
- `npm run build`
- `git diff --check`
