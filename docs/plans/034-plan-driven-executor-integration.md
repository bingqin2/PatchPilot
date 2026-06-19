# 034 Plan Driven Executor Integration

## Goal

Make the production patch workflow use the model-generated fix plan before applying a controlled file edit.

## Scope

- Add a production `PatchWorkflow` implementation that calls `FixPlanGenerator`.
- Reuse `PlannedPatchWorkflow` to apply only plan-authorized `replace` instructions.
- Keep `SimplePatchWorkflow` available for tests and deterministic fallback code, but do not register it as the primary production bean.
- Keep the existing executor sequence after patching unchanged: diff, Maven tests, commit, push, and Pull Request creation.
- Do not add multi-step model tool calling, arbitrary shell execution, or new Git behavior.

## Tasks

- [x] Add tests for plan generation followed by planned patch application.
- [x] Add tests that the Spring context has a single production `PatchWorkflow`.
- [x] Implement the production plan-driven workflow bean.
- [x] Update tool-call audit expectations where needed.
- [x] Run focused workflow and executor tests.
- [x] Run full backend tests.

## Acceptance Criteria

- [x] Production `PatchWorkflow` generates a `FixPlan` before applying a patch.
- [x] File edits still require the target file to be listed in `FixPlan.targetFiles()`.
- [x] Existing executor behavior after patch application remains unchanged.
- [x] `PatchPilotApplicationTests` starts without duplicate `PatchWorkflow` beans.
