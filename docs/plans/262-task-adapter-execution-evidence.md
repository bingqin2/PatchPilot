# 262 - Task Adapter Execution Evidence

## Goal

Make every task detail and copied task report show one structured adapter execution evidence block so operators can prove why a task selected a supported language/build system and which allowlisted verification command ran.

## Scope

- Add a backend task-detail evidence read model derived from existing task adapter metadata and repository-support guidance.
- Include adapter evidence in `GET /api/tasks/{taskId}/detail`.
- Include the same adapter evidence in `GET /api/tasks/{taskId}/report`.
- Show the evidence in the dashboard task detail panel with supported, pending, and unsupported states.
- Update README, product spec, frontend design notes, and execution log.

## Acceptance Criteria

- Supported tasks show status `SUPPORTED`, language, build system, verification command, detection reason, and the safety note that the command came from a registered adapter.
- Tasks without adapter metadata show status `PENDING` with an operator action explaining that adapter preflight has not recorded evidence yet.
- Unsupported-repository failures show status `UNSUPPORTED`, reuse repository-support guidance, list supported adapter options, and state that model patch generation, tests, Git mutation, push, and PR creation did not run.
- The copied task report includes the structured adapter evidence section.
- The dashboard task detail panel displays the same evidence without requiring raw timeline or tool-call inspection.
- Backend and frontend tests cover supported, pending, and unsupported paths.
