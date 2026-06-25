# 205 - Demo Launch Package

## Goal

Add a copyable demo launch package after launch preflight succeeds or blocks. The package should give an operator one concise Markdown artifact that captures the exact GitHub issue comment, issue URL, preflight decision, and browser-local prepared command evidence before a live `/agent fix` comment is posted.

## Why This Matters

Plans 201-204 made the launch path safer: compose the command, reuse browser-local command history, and include that history in session reports. The remaining demo gap is the moment immediately before posting on GitHub. A launch package gives the operator a final handoff artifact that proves what will be posted and which readiness/trigger gates were checked.

## Scope

- Add a `Copy launch package` action to `DemoLaunchPreflightPanel`.
- Include repository, issue number, GitHub issue URL, trigger user, and exact `/agent fix` comment.
- Include readiness status, trigger evaluation status, source, issue-context state, blocked category/reason, and next actions.
- Include up to five prepared launch commands from this browser's `patchpilot.demoLaunchCommandHistory` context.
- Wire App-level prepared command history into the launch preflight panel.
- Keep the feature browser-local and read-only with respect to tasks, queue state, Git, GitHub comments, Pull Requests, model calls, and backend persistence.

## Out of Scope

- New backend endpoints.
- Server-side persistence of launch packages.
- Posting comments to GitHub from the dashboard.
- Replaying prepared commands automatically.

## Validation

- Frontend panel test proving `Copy launch package` exports issue URL, exact comment, preflight evidence, and prepared command evidence.
- App test proving composed command history flows into the launch package after applying a command to preflight.
- Full frontend regression, production build, backend regression, and diff whitespace checks before merge.
