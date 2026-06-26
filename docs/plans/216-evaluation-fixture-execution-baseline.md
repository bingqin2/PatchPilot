# 216 - Evaluation Fixture Execution Baseline

## Goal

Turn the checked-in evaluation fixture catalog into executable local demo evidence without creating tasks, calling the model, mutating Git, or writing to GitHub. Operators should be able to run adapter-selected verification commands for supported fixture cases and copy a baseline report from the dashboard.

## Scope

- Add `POST /api/evaluation/fixture-baseline`.
- Execute only supported-fix cases whose fixture readiness already passes.
- Select commands from the detected language adapter, not from issue text.
- Skip safety-rejection cases because those are validated through trigger gates.
- Return aggregate counts, per-case command output snippets, side-effect contract, next action, and Markdown report.
- Add a dashboard action to run the baseline on demand and render the latest result in the evaluation panel.

## Non-Goals

- No task creation, queueing, model calls, Git commits, pushes, Pull Requests, or GitHub issue comments.
- No arbitrary fixture path or command input from the API request.
- No persisted benchmark run records in this slice.

## Validation

- Backend service and controller tests cover passing and failing baseline runs.
- Frontend API, panel, and dashboard tests cover manual trigger behavior, loading/error states, and report copy.
- Full backend and frontend regression checks before merging.
