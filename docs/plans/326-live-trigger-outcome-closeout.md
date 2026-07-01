# Live Trigger Outcome Closeout

## Goal

Close the loop after a real GitHub `/agent fix` trigger by producing an operator-facing closeout that answers whether the trigger created a task, whether it finished, whether a Pull Request exists, and which archived launch package was used.

## Scope

- Add a backend closeout API under `/api/demo/live-trigger-outcome-closeout`.
- Correlate the current repository, issue, user, and exact trigger comment with the latest or requested live trigger launch package archive.
- Find the newest matching `FixTaskVo` and classify the outcome as `READY`, `NEEDS_ATTENTION`, or `BLOCKED`.
- Return evidence notes, next actions, side-effect contract, and a Markdown report download.
- Add dashboard controls inside the existing Live launch gate panel so an operator can generate and download the outcome report after posting the real comment.

## Design

The closeout is read-only. It uses `DemoLiveTriggerLaunchPackageArchiveRepository` as the launch-package evidence source and `FixTaskService.listTasks(FixTaskListQuery)` as the task evidence source. The API accepts the same trigger fields as the launch package and an optional `launchPackageArchiveId`; when omitted, it uses the most recent archive.

`READY` means a matching task is `COMPLETED` and has a Pull Request URL. `NEEDS_ATTENTION` means a task exists but is failed, cancelled, still running, or missing a PR. `BLOCKED` means no launch package archive or no matching task can be found.

## Verification

- Backend service tests cover READY, task missing, and failed task outcomes.
- Backend controller tests cover admin protection, JSON response, validation, and Markdown download.
- Frontend API tests cover the new POST and report download endpoints.
- Frontend component tests cover generating the closeout, rendering the PR/evidence, downloading the report, and displaying errors.
- Final verification runs focused backend tests, focused frontend tests, full backend tests, frontend tests/build, `git diff --check`, and a secret scan before commit.
