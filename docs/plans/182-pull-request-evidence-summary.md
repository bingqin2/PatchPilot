# Pull Request Evidence Summary

## Goal

Make every PatchPilot-created Pull Request self-explanatory for human review. A successful PR should show which task created it, who triggered it, which adapter was selected, which allowlisted verification command passed, and which safety boundary still applies.

## Scope

- Extend `PullRequestTool` PR bodies with task id, trigger user, branch, adapter language, build system, verification command, and detection reason.
- State that PatchPilot opens the PR only after adapter-selected verification passes.
- State that verification commands come from repository adapters, not arbitrary issue text.
- State that PatchPilot does not auto-merge Pull Requests.
- Pass adapter-enriched task context from the executor into PR creation so real runs include detection evidence.
- Cover both PR body formatting and executor handoff with backend tests.
- Update product documentation and progress logs.

## Non-Goals

- Do not change the GitHub Pull Request API client contract.
- Do not add auto-merge behavior.
- Do not include full model prompts, raw diffs, or secrets in PR bodies.
- Do not change dashboard PR rendering in this slice.

## Validation

- `mvn -pl PatchPilot -Dtest=PullRequestToolTests test`
- `mvn -pl PatchPilot -Dtest=WorkspaceFixTaskExecutorTests#should_prepare_task_repository_and_run_maven_tests test`
- `mvn -pl PatchPilot -Dtest=PullRequestToolTests,WorkspaceFixTaskExecutorTests test`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
