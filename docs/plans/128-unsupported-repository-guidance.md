# 128 Unsupported Repository Guidance

## Goal

Make unsupported or unverifiable repositories fail with actionable guidance instead of only showing a generic failed task.

This advances the multi-language and safety target by making PatchPilot's refusal path explicit: unsupported repositories should not execute arbitrary commands, and operators should know which supported adapter signals are missing before retrying.

## Scope

- Add structured repository support guidance to task detail responses when a task failed because no supported language adapter matched.
- Include supported adapter signals and verification commands from the existing adapter catalog.
- Add the same guidance to copied task reports.
- Show a dedicated guidance panel in the dashboard task detail view.
- Cover backend detail/report behavior and frontend rendering with tests.

## Non-Goals

- Do not add a new language adapter in this slice.
- Do not allow custom shell commands for unsupported repositories.
- Do not classify arbitrary failure messages as repository support failures.
- Do not change task retry, queue, or PR creation behavior.

## Validation

- `mvn -pl PatchPilot -Dtest=TaskControllerTests test`
- `cd frontend && npm test -- TaskDetailPanel.test.tsx`
- `mvn -pl PatchPilot test`
- `cd frontend && npm test`
- `cd frontend && npm run build`
- `git diff --check`
