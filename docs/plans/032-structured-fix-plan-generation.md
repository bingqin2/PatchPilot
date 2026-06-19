# 032 Structured Fix Plan Generation

## Goal

Generate and parse a structured model-produced fix plan without applying repository changes.

## Scope

- Add a `FixPlanGenerator` boundary under `agent.workflow`.
- Add fix-plan request/result domain records.
- Build a deterministic system prompt and user prompt from task metadata.
- Parse model output as strict JSON with `summary`, `targetFiles`, `steps`, and `risk`.
- Fail clearly when model output is not valid JSON or required fields are missing.
- Keep `SimplePatchWorkflow` and `NoopFixTaskExecutor` unchanged.

## Tasks

- [x] Add fix-plan generator tests for successful JSON parsing.
- [x] Add tests for invalid or incomplete model output.
- [x] Implement fix-plan domain records and generator.
- [x] Run focused tests and full backend tests.

## Acceptance Criteria

- [x] The model provider receives task id, system prompt, and issue-oriented user prompt.
- [x] Valid JSON output becomes a typed fix plan.
- [x] Invalid JSON and missing required fields throw `FixPlanGenerationException`.
- [x] No task execution path calls the fix-plan generator in this phase.
