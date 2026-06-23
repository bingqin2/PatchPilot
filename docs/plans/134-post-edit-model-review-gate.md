# 134 Post-Edit Model Review Gate

## Goal

Add a post-edit model review gate so PatchPilot can reject model-generated edits that do not match the issue request or fix plan before writing files and continuing toward tests, commits, pushes, and Pull Requests.

This moves PatchPilot closer to a safe issue-to-PR agent: the system can now plan, propose controlled edits, and ask for a second structured review before mutating the workspace. Clear mismatches fail fast instead of creating noisy or misleading PRs.

## Scope

- Add a model-backed patch review generator that returns JSON-only review decisions.
- Review the trigger comment, fix plan, target files, bounded before content, proposed after content, and edit rationale.
- Support explicit `APPROVE` and `REJECT` decisions.
- Block rejected edits before `FileWriteTool` writes them to the repository workspace.
- Preserve the manual `/agent fix replace <path> <content>` smoke path without invoking the post-edit review gate.
- Include the review decision in successful patch workflow summaries.

## Non-Goals

- Do not add another dashboard panel in this slice.
- Do not persist a dedicated patch-review table; review model calls are already audited by the model provider path.
- Do not retry or regenerate edits automatically after rejection.
- Do not bypass generated-diff risk gates, adapter verification, commit, push, PR, or human review.

## Validation

- `mvn -pl PatchPilot -Dtest=PatchReviewGeneratorTests,PlannedPatchWorkflowTests,PlanDrivenPatchWorkflowTests,PatchPilotApplicationTests test`
- `mvn -pl PatchPilot test`
- `git diff --check`
