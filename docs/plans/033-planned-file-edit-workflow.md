# 033 Planned File Edit Workflow

## Goal

Apply a narrowly scoped file edit only when a structured fix plan authorizes the target file.

## Scope

- Add `PlannedPatchWorkflow` as an internal workflow class.
- Support only `/agent fix replace <path> <text>` trigger comments.
- Require `<path>` to appear in the `FixPlan.targetFiles`.
- Write through existing `FileWriteTool`, preserving workspace path guards.
- Do not register the workflow as the production `PatchWorkflow` bean.
- Do not run shell commands, Git commands, tests, or Pull Request creation in this workflow.

## Tasks

- [x] Add tests for allowed planned replacement.
- [x] Add tests for target-file mismatch and unsafe path rejection.
- [x] Implement `PlannedPatchWorkflow`.
- [x] Run focused tests and full backend tests.

## Acceptance Criteria

- [x] Planned replacement writes only files listed in the fix plan.
- [x] Missing or non-matching replacement instructions return a skipped patch result.
- [x] Unsafe paths still fail through the existing workspace path guard.
- [x] Existing `SimplePatchWorkflow` remains the production `PatchWorkflow` bean.
