# 031 Model Provider Client Boundary

## Goal

Add an auditable OpenAI-compatible model client boundary without wiring model output into repository editing yet.

## Scope

- Add `agent.provider` request/response domain objects.
- Add `ModelProviderClient` and `ModelProviderException`.
- Add `AgentProperties` for provider, model, base URL, and API key.
- Implement `OpenAiCompatibleModelClient` using Java `HttpClient`.
- Record successful and failed model calls through `FixTaskModelCallService`.
- Keep `SimplePatchWorkflow` unchanged.

## Tasks

- [x] Add client request/response and HTTP behavior tests.
- [x] Add audit tests for successful and failed calls.
- [x] Implement provider domain, config, and client.
- [x] Register agent properties in the Spring application.
- [x] Run focused tests and full backend tests.

## Acceptance Criteria

- [x] Missing API key fails before sending an HTTP request.
- [x] Successful calls send OpenAI-compatible `/chat/completions` requests and parse assistant text.
- [x] Non-2xx responses fail with an actionable status message.
- [x] Successful and failed calls create model-call audit records.
- [x] No workflow starts calling real models in this phase.
