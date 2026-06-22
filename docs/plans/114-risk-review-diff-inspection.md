# 114 - Risk Review Diff Inspection

## Goal

Make `PENDING_REVIEW` tasks safe to approve by exposing the generated diff as a first-class task detail artifact. Operators should not need to infer proposed changes from tool-call logs before approving a blocked risk review.

## Scope

- Add a generated-diff read model to `GET /api/tasks/{taskId}/detail`.
- Include the generated diff in copied Markdown task reports.
- Render a dedicated dashboard diff preview before timeline, test, tool-call, and model-call records.
- Keep the source of truth as the existing successful `DiffTool` tool-call output; do not add another persistence table.

## Backend Design

`TaskController` derives the latest successful `DiffTool` call with a nonblank output summary and returns it as `FixTaskGeneratedDiffVo`.

The read model contains:

- `toolCallId`
- `diff`
- `generatedAt`

`FixTaskReportFormatter` adds a `Generated Diff` section with a fenced `diff` block when the read model exists.

## Frontend Design

`TaskDetailPanel` renders a `Generated diff` section when `detail.generatedDiff` is present. For `PENDING_REVIEW` tasks, the panel tells the operator to review the changes before approving. Other task states describe it as the latest captured patch.

## Validation

- Backend controller tests cover detail projection and Markdown report output.
- Frontend API and dashboard tests cover `generatedDiff` transport and UI rendering.
- Full backend and frontend suites must pass before handoff.
