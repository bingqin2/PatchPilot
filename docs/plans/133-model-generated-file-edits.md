# 133 Model Generated File Edits

## Goal

Let PatchPilot apply bounded model-generated file edits after planning, instead of requiring a maintainer to encode the exact change in `/agent fix replace ...`.

This moves PatchPilot closer to a credible issue-to-PR agent: after issue context ingestion, the agent can now turn a model fix plan into controlled source changes and still keep the existing safety gates around paths, file size, tests, diff risk, and pull request creation.

## Scope

- Add a model-backed file edit generator that asks for JSON-only full-file edits.
- Read only the files named by the generated fix plan and include their bounded content in the edit prompt.
- Preserve the existing manual `/agent fix replace <path> <content>` demo path for smoke tests.
- Apply generated edits only when every proposed path is listed in the fix plan.
- Reject sensitive paths such as `.env`, `.git`, GitHub workflows, private keys, and oversized edits before writing.
- Return a clear patch workflow summary listing the applied files.

## Non-Goals

- Do not add a multi-step autonomous tool loop in this slice.
- Do not let the model execute shell commands or choose verification commands.
- Do not implement semantic repository search beyond the fix-plan target files.
- Do not change dashboard UI; generated edits are visible through existing task results, diffs, and PRs.

## Validation

- `mvn -pl PatchPilot -Dtest=FileEditPlanGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests test`
- `mvn -pl PatchPilot test`
- `git diff --check`
