# 137 Review Retry Lineage

## Goal

Make retry recovery traceable after a failed model patch review or other terminal task failure.

This moves PatchPilot closer to a credible self-hosted issue-to-PR agent: when an operator retries a failed task, the recovered run should still show which failed state it came from and why the retry was needed.

## Scope

- Store retry source metadata on the task when `FAILED` or `CANCELLED` work is moved back to `PENDING`.
- Preserve the source task id, source status, source failure reason, and retry timestamp in in-memory and MySQL-backed task services.
- Add a Flyway migration for durable retry lineage columns.
- Include retry lineage in task API responses and markdown reports.
- Render retry lineage in the dashboard task detail panel so operators can inspect recovery context without reading logs.
- Keep retry lineage searchable in the in-memory fallback task list.

## Non-Goals

- Do not create a separate retry-history table.
- Do not create a new task id for task-level retry; retry continues to requeue the same task.
- Do not automatically retry rejected patches.
- Do not change pending-review approval semantics.

## Validation

- `mvn -pl PatchPilot -Dtest=DefaultFixTaskControlServiceTests,TaskControllerTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,FixTaskMigrationTests test`
- `npm test -- --run src/dashboard/components/TaskDetailPanel.test.tsx src/api.test.ts`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
