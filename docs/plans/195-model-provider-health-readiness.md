# Model Provider Health Readiness

## Goal

Make PatchPilot's demo readiness reflect whether the configured OpenAI-compatible model provider actually responds, not only whether model credentials are present. Operators should see a clear setup action before a live `/agent fix` run reaches model-backed planning.

## Scope

- Add an admin-protected read-only `GET /api/model-provider/health` endpoint.
- Run a minimal OpenAI-compatible `chat/completions` probe and return only non-sensitive health fields.
- Add model provider health to `GET /api/demo/readiness`.
- Surface model provider health in the dashboard operator setup checklist.
- Keep health checks out of task model-call audit records so dashboard refreshes do not pollute execution metrics.
- Update README, product docs, progress logs, and tests.

## Non-Goals

- Do not expose API keys, raw model responses, or raw base URLs.
- Do not create tasks, mutate repositories, run tests, push branches, create Pull Requests, or write GitHub comments.
- Do not add new model providers beyond the existing OpenAI-compatible path.

## Validation

- `mvn -pl PatchPilot -Dtest=ModelProviderHealthServiceTests,ModelProviderHealthControllerTests,DemoReadinessServiceTests test`
- `npm test -- --run src/api.test.ts src/dashboard/components/OperatorSetupChecklistPanel.test.tsx`
- `npm test -- --run src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
