# Accepted Trigger Decision Audit

## Goal

Expose accepted `/agent fix` trigger decisions as a first-class audit stream. Operators should be able to inspect why recent tasks were allowed to execute without selecting each task or parsing timeline text.

## Scope

- Add a read API for recent persisted pre-execution decisions with task context.
- Include repository, issue, trigger user, command, task status, source, final decision, safety, active-task, quarantine, rate-limit, model intent, issue-context state, and timestamp.
- Keep task detail and copied task reports using the same persisted decision source.
- Add a dashboard panel for recent accepted trigger decisions with a link back to the related task.
- Keep rejected-trigger audit and quarantine operations separate from accepted-trigger audit records.
- Cover backend service/controller behavior and frontend API/dashboard rendering with tests.

## Non-Goals

- Do not change the task creation policy or execution order.
- Do not add new writes to GitHub, Git, queue, model, or repository tools.
- Do not expose secrets, raw model prompts, or unsafe rejected trigger bodies.
- Do not replace selected-task trigger evidence; this feature adds a global accepted-decision view.

## Validation

- `mvn -pl PatchPilot -Dtest=InMemoryFixTaskPreExecutionDecisionServiceTests,MyBatisFixTaskPreExecutionDecisionServiceTests,TaskControllerTests test`
- `npm test -- --run src/api.test.ts src/dashboard/components/AcceptedTriggerDecisionPanel.test.tsx src/App.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
