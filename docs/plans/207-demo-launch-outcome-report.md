# 207 - Demo Launch Outcome Report

## Goal

Add a copyable Markdown outcome report for each tracked demo launch command in the dashboard.

## Why This Matters

Plan 206 made the post-launch state visible by correlating browser-local prepared commands with webhook deliveries, task rows, and Pull Request URLs. Operators still need a compact handoff artifact after a live demo or smoke run. The outcome report should preserve the exact command, delivery evidence, task outcome, Pull Request link, and next action without asking the operator to manually combine several panels.

## Scope

- Add a `Copy outcome report` action to each demo launch tracker row.
- Include repository, issue, trigger user, exact command, prepared timestamp, and launch state.
- Include matched webhook status, delivery id, outcome type, and message.
- Include matched task id, task status, failure reason, completion timestamp, and Pull Request URL.
- Include the same next operator action shown in the tracker row.
- Keep the feature read-only: no new backend endpoint, no task creation, no GitHub mutation, no queue mutation, and no localStorage writes.

## Out of Scope

- Persisting outcome reports on the backend.
- Posting outcome reports to GitHub automatically.
- Downloading outcome reports as files.
- Aggregating multiple launches into one report.

## Validation

- Component test proving a successful tracked launch copies webhook, task, Pull Request, and next-action evidence.
- App-level test proving a browser-local prepared command can copy the same outcome report from the full dashboard.
- Full frontend test/build verification.
- Backend regression check to keep the repository baseline clean even though this slice is frontend-only.
