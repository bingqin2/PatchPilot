# 186 GitHub Feedback Dashboard Deep Links

## Goal

Add optional Dashboard deep links to GitHub issue comments and Pull Request bodies so maintainers can jump from GitHub feedback to the matching PatchPilot task detail page.

## Scope

- Add a non-secret `patchpilot.dashboard.base-url` setting backed by `PATCHPILOT_DASHBOARD_BASE_URL`.
- Generate task links as `<base-url>/tasks/{taskId}` with safe slash handling.
- Include the link in task status issue comments and Pull Request bodies when configured.
- Omit the link when no Dashboard base URL is configured.
- Expose whether the Dashboard base URL is configured in the configuration summary and Dashboard configuration panel.

## Out of Scope

- No new frontend route. The dashboard already supports `/tasks/{taskId}`.
- No public exposure of admin tokens or other secrets.
- No GitHub App installation changes.

## Verification

- Focused backend tests for issue comments, Pull Request bodies, and configuration summary.
- Focused frontend tests for configuration panel/API typing.
- Full backend and frontend test/build checks before handoff.
