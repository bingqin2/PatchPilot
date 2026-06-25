# 204 - Demo Command History Session Report

## Goal

Include the browser-local demo launch command history in copied, downloaded, and archived demo session reports. This closes the handoff gap after plan 203: operators can prove which controlled `/agent fix` commands were prepared during a practice or live demo session.

## Scope

- Convert the dashboard's `patchpilot.demoLaunchCommandHistory` entries into a bounded report context.
- Show prepared commands inside the demo session snapshot panel.
- Send prepared command context to session report copy, download, and archive actions.
- Add POST variants for current session report and report download while keeping GET compatibility.
- Add prepared command evidence to archived session report Markdown.
- Keep the feature read-only with respect to tasks, queue state, Git, GitHub comments, Pull Requests, model calls, and test execution.

## Out of Scope

- Server-side persistence of browser command history.
- Creating or replaying tasks from saved commands.
- Sharing command history between browsers or operators.

## Validation

- Backend focused tests for session report, session archive, and demo controller behavior.
- Frontend API tests for POST report/download/archive context.
- Frontend panel and App tests proving generated command history appears in the session panel and is sent with report actions.
- Full frontend test/build and backend regression tests before merge.
