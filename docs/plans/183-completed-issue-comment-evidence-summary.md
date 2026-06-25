# Completed Issue Comment Evidence Summary

## Goal

Make successful GitHub issue feedback as reviewable as the generated Pull Request. When PatchPilot completes a task, the issue status comment should show the PR link plus the adapter and verification evidence that made the run safe to review.

## Scope

- Extend completed issue comments with detected language, build system, allowlisted verification command, and adapter detection reason when available.
- State that the Pull Request was opened only after adapter-selected verification passed.
- State that verification commands come from repository adapters, not arbitrary issue text.
- State that PatchPilot does not auto-merge Pull Requests.
- Cover direct completed-comment formatting and worker handoff with backend tests.
- Update product documentation and progress logs.

## Non-Goals

- Do not change rejected-trigger or failed-task issue comment wording beyond existing behavior.
- Do not include full model prompts, raw diffs, or test logs in GitHub issue comments.
- Do not change dashboard rendering in this slice.
- Do not add auto-merge behavior.

## Validation

- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests test`
- `mvn -pl PatchPilot -Dtest=FixTaskWorkerTests#should_execute_task_and_mark_completed test`
- `mvn -pl PatchPilot -Dtest=IssueCommentToolTests,FixTaskWorkerTests test`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
