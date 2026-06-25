# 188 Pull Request Patch Review Evidence Summary

## Goal

Make generated Pull Requests carry the latest model patch-review evidence. A maintainer reviewing the PR should be able to see why PatchPilot considered the generated edit acceptable without opening the dashboard or task report.

## Scope

- Add optional patch-review evidence to `PullRequestTool`.
- Include decision, reason, confidence, required follow-up, edited files, and review time when a latest patch review exists.
- Wire `NoopFixTaskExecutor` to read the latest patch review before creating a Pull Request and pass it to `PullRequestTool`.
- Keep PR bodies unchanged when no patch review record exists.

## Out of Scope

- No full raw diff, model prompt, or model response in PR bodies.
- No issue-comment changes in this slice.
- No frontend changes. Dashboard task detail already shows patch-review evidence.
- No change to patch-review generation or rejection behavior.

## Verification

- Focused PR body tests for patch-review evidence formatting.
- Executor tests proving the latest patch-review record is handed to PR creation.
- Full backend regression verification before handoff.
