# 219 - Evaluation Baseline Demo Readiness Gate

## Goal

Turn archived fixture baseline regression evidence into a demo readiness gate so operators know whether the current evaluation baseline is safe to use before a live issue-to-PR demonstration.

## Scope

- Add an `Evaluation baseline` check to `GET /api/demo/readiness`.
- Reuse the read-only regression summary from `GET /api/evaluation/fixture-baseline-runs/summary`.
- Mark demo readiness as blocked when the latest baseline has failed cases or the latest run regressed.
- Mark demo readiness as needing attention when there are no archived baselines or only one archived baseline.
- Keep the gate read-only: no tasks, model calls, Git mutations, fixture execution, or GitHub writes.
- Surface the new check in the dashboard demo readiness panel with its operator action visible.

## Backend

- Modify `DemoReadinessService` to depend on `EvaluationFixtureBaselineRunRegressionSummaryService`.
- Add an `Evaluation baseline` `DemoReadinessCheckVo`.
- Map summary statuses:
  - `NO_ARCHIVES` and `SINGLE_ARCHIVE` -> `NEEDS_ATTENTION`.
  - `REGRESSED` -> `BLOCKED`.
  - `STABLE` and `IMPROVED` with zero latest failures -> `READY`.
  - Any latest failed cases -> `BLOCKED`.
- Include the baseline gate action in aggregated next actions.

## Frontend

- Keep the existing `DemoReadinessPanel` contract.
- Render every readiness check action inside each check row so the evaluation baseline gate gives a visible next step without requiring operators to scroll to the global next-action list.

## Validation

- Backend RED/GREEN: focused `DemoReadinessServiceTests` and `DemoReadinessControllerTests`.
- Frontend RED/GREEN: focused `DemoReadinessPanel.test.tsx`.
- Full backend: `mvn -pl PatchPilot test`.
- Full frontend: `npm test -- --reporter=basic`.
- Build: `npm run build`.
- Diff hygiene: `git diff --check`.
