# Persisted Pre-Execution Decision Records

## Goal

Persist the structured checks that allowed a task to move from trigger handling into queued repository work. Operators should be able to inspect accepted-task safety evidence without relying on timeline message parsing.

## Scope

- Add a `fix_task_pre_execution_decision` table for accepted-task decision snapshots.
- Store source, final decision, safety gate, active-task check, quarantine check, rate-limit check, trigger-intent classification, issue-context state, and timestamp.
- Record decisions for both manual task creation and GitHub `issue_comment` webhook task creation.
- Expose persisted records through task detail and copied Markdown reports, while preserving timeline fallback for older tasks.
- Keep dashboard and report response shapes compatible with the existing pre-execution safety snapshot view.
- Cover migration, in-memory service, MyBatis service, manual creation, webhook creation, task detail, and task report behavior with backend tests.

## Non-Goals

- Do not change the safety policy or task creation order.
- Do not persist rejected-trigger decisions here; rejected triggers remain in the rejected-trigger audit tables.
- Do not expose raw model prompts, secrets, unsafe trigger text, or GitHub credentials.
- Do not remove timeline accepted-trigger evidence; it remains useful for chronological execution history.

## Validation

- `mvn -pl PatchPilot -Dtest=GitHubWebhookServiceTests test`
- `mvn -pl PatchPilot -Dtest=FixTaskPreExecutionDecisionMigrationTests,InMemoryFixTaskPreExecutionDecisionServiceTests,MyBatisFixTaskPreExecutionDecisionServiceTests,DefaultManualFixTaskServiceTests,TaskControllerTests test`
- `mvn -pl PatchPilot test`
- `git diff --check`
