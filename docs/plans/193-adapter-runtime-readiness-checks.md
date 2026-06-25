# Adapter Runtime Readiness Checks

## Goal

Make multi-language adapter readiness reflect the current backend runtime, not only declared support and fixture detection. Operators should know whether the executable selected by each adapter is available before a live `/agent fix` run reaches verification.

## Scope

- Add a read-only `GET /api/language-adapters/runtime-readiness` endpoint.
- Check the first executable from each allowlisted adapter verification command against the backend process `PATH`.
- Return language, build system, executable, verification command, readiness status, and reason.
- Do not execute adapter verification commands or mutate task, queue, workspace, GitHub, or model state.
- Add runtime readiness to the dashboard adapter readiness report and copied Markdown report.
- Cover backend service/controller behavior, frontend API parsing, dashboard rendering, and App integration with tests.
- Update product, README, and progress documentation.

## Non-Goals

- Do not install missing runtime tools.
- Do not run `go test`, `npm test`, `tox`, `pytest`, or any other verification command from this diagnostic.
- Do not change repository adapter selection or task execution behavior.
- Do not replace fixture verification; runtime readiness complements fixture drift checks.

## Validation

- `mvn -pl PatchPilot -Dtest=LanguageAdapterRuntimeReadinessServiceTests,LanguageAdapterControllerTests test`
- `npm test -- --run src/api.test.ts src/dashboard/components/AdapterReadinessReportPanel.test.tsx src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
