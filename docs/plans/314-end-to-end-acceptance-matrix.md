# 314 End-to-End Acceptance Matrix

## Goal

Build one operator-facing acceptance matrix that answers how close PatchPilot is to the final self-hosted issue-to-PR goal. The matrix should aggregate launch readiness, adapter coverage, evaluation coverage, safety rejection coverage, and recent task outcomes into a single read-only status.

## Scope

- Add a backend read-only service and endpoint at `GET /api/demo/end-to-end-acceptance-matrix`.
- Reuse existing read models instead of creating new durable state:
  - `DemoLiveLaunchGateService`
  - `EvaluationCaseCatalogService`
  - `EvaluationFixtureBaselineRunRegressionSummaryService`
  - `EvaluationRunArchiveReadinessSummaryService`
  - `FixTaskService`
- Add typed frontend API support and a dashboard panel.
- Update README, product spec, and execution log.

## Non-Goals

- Do not create tasks, call the model, run tests, mutate Git, write GitHub comments, or archive records.
- Do not replace the live launch gate, evaluation pages, or task history.
- Do not require a live GitHub call from this endpoint; the launch gate provider is injectable for tests and runtime defaults can use demo properties.

## Acceptance Criteria

- The API returns `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- The matrix includes rows for live launch gate, supported language coverage, safety rejection coverage, evaluation baseline, evaluation run archive, recent successful PR evidence, recent failure evidence, unsupported/pending-review evidence, and overall product gap.
- Each row includes status, category, evidence, gap, and next action.
- The response includes `readinessPercent`, `readyCount`, `needsAttentionCount`, `blockedCount`, `nextActions`, a side-effect contract, and a Markdown report.
- The dashboard renders the matrix, high-level counts, next actions, and copyable Markdown.

## Verification

- Backend RED/GREEN tests:
  - `DemoEndToEndAcceptanceMatrixServiceTests`
  - `DemoReadinessControllerTests`
- Frontend RED/GREEN tests:
  - `EndToEndAcceptanceMatrixPanel.test.tsx`
  - `api.test.ts`
  - `App.test.tsx`
- Final checks:
  - `mvn -q -pl PatchPilot test`
  - `npm --prefix frontend test -- --reporter=dot`
  - `npm --prefix frontend run build`
  - `git diff --check`
