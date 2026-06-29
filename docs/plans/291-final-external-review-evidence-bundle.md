# 291 Final External Review Evidence Bundle

## Goal

Promote the final external-review evidence package into the top-level demo evidence bundle and copied demo runbook. Operators should be able to see the final reviewer-facing proof package from the first demo evidence readout without opening the final acceptance panel first.

## Scope

- Add `finalExternalReviewEvidencePackage` to `DemoEvidenceBundleVo` with compatibility defaults for older constructor paths.
- Wire `DemoEvidenceBundleService` to read `DemoFinalExternalReviewEvidencePackageService`, include the package in bundle status aggregation, and add its next action when it is not ready.
- Extend copied runbooks with final external-review package status, ready flag, closeout archive, completion archive, delivery receipt, task, Pull Request, delivery target, freshness, next action, and download actions.
- Render the package in `DemoEvidenceBundlePanel`, including safe legacy fallback text when older bundle responses do not include it.
- Update product docs and execution log with verification evidence.

## Validation

- `mvn -q -pl PatchPilot -Dtest=DemoEvidenceBundleServiceTests,DemoRunbookServiceTests test`
- `npm test -- --run src/dashboard/components/DemoEvidenceBundlePanel.test.tsx`
- Full backend/frontend validation before merge.
