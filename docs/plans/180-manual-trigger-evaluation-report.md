# Manual Trigger Evaluation Report

## Goal

Give operators a copyable Markdown report for manual `/agent fix` dry-runs, so accepted and blocked trigger decisions can be shared in demo notes, issue comments, or review threads without creating tasks or mutating GitHub.

## Scope

- Add a dashboard copy action to `ManualTaskForm` when a trigger evaluation result is visible.
- Include the evaluated repository, issue, trigger user, command, source, status, gate decisions, issue-context state, blocked reason/category, and next action.
- Cover both allowed and blocked evaluations in frontend tests.
- Document the evidence-report behavior in the README and frontend design docs.

## Non-Goals

- Do not add a backend endpoint; the report is derived from the existing `POST /api/tasks/evaluate-trigger` response and current form input.
- Do not create tasks, queue items, rejected-trigger audit rows, GitHub comments, webhook diagnostics, or rate-limit records.
- Do not alter trigger gate decisions.

## Validation

- `npm test -- --run src/dashboard/components/ManualTaskForm.test.tsx`
- `npm test`
- `npm run build`
- `mvn -pl PatchPilot test`
- `git diff --check`
