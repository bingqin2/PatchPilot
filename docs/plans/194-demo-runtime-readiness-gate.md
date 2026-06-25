# Demo Runtime Readiness Gate

## Goal

Make the demo readiness flow fail visibly when a supported adapter's verification executable is missing from the backend runtime. A live demo should not look ready just because fixture detection passes if the backend cannot run the selected verification command.

## Scope

- Add adapter runtime executable availability to `GET /api/demo/readiness`.
- Add a dedicated adapter runtime gate step to `GET /api/demo/smoke-checklist`.
- Include runtime executable guidance in demo session snapshots and generated demo scripts.
- Surface runtime executable availability in the dashboard operator setup checklist.
- Reuse `GET /api/language-adapters/runtime-readiness` evidence instead of rechecking runtime state in parallel.
- Update README, product docs, progress logs, and tests.

## Non-Goals

- Do not install missing executables.
- Do not run adapter verification commands from readiness or checklist endpoints.
- Do not change task execution, adapter selection, repository mutation, GitHub writes, or model calls.

## Validation

- `mvn -pl PatchPilot -Dtest=DemoReadinessServiceTests,DemoSmokeChecklistServiceTests,DemoSessionSnapshotServiceTests,DemoScriptServiceTests test`
- `npm test -- --run src/dashboard/components/OperatorSetupChecklistPanel.test.tsx src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
