# 122 Dashboard Operator Setup Checklist

## Goal

Add a single operator checklist near the top of the dashboard so a maintainer can tell whether the local PatchPilot environment is ready for a controlled issue-to-PR demo without reading multiple panels or running curl commands.

## Scope

- Add a dashboard checklist component that derives setup checks from already loaded dashboard data.
- Cover backend connectivity, required credentials, safety policy, adapter fixture health, queue health, and recent Pull Request evidence.
- Show ready versus attention counts and concise next actions.
- Keep the checklist read-only; it should not create tasks, change configuration, or call new backend APIs.
- Update frontend tests, README, frontend design notes, and the execution log.

## Verification

- App-level tests cover the default mixed setup state.
- App-level tests cover a fully ready setup state.
- Full frontend tests and production build pass.
