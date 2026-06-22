# Generated Diff Risk Gate

## Goal

Block high-risk generated patches after PatchPilot writes a diff but before tests, commits, pushes, or Pull Request creation.

This closes the safety gap between trigger-time command validation and post-generation repository mutation. A request can be valid, but the generated patch can still be too broad, touch sensitive files, introduce secret-like values, or include binary changes. Those cases should fail as audited task failures instead of reaching GitHub.

## Scope

- Add a deterministic `GeneratedDiffRiskGate` that evaluates the final workspace diff.
- Reject generated diffs that touch sensitive paths such as `.env`, private keys, and GitHub Actions workflows.
- Reject secret-like added lines, binary patches, too many changed files, or too many changed lines.
- Run the gate after `DiffTool` and before adapter verification, commit, push, and PR creation.
- Record the gate as an audited tool call so reports, task detail APIs, and the dashboard can explain the failure.
- Surface failed risk-gate evidence in the dashboard task detail evidence strip.
- Update README, architecture, target-state, backend standard, frontend design notes, and execution log.

## Out of Scope

- Model-based diff approval.
- Human approval workflows for high-risk diffs.
- Per-repository risk thresholds.
- Auto-rewriting unsafe patches into safe ones.

## Validation

- Backend unit tests for allowed diffs and blocked sensitive path, secret-like line, binary diff, and large diff cases.
- Executor tests proving a rejected generated diff stops before verification, test-run recording, commit, push, and PR creation.
- Frontend component tests proving risk-gate failures are visible in task detail evidence.
- Full backend and frontend test suites plus production frontend build.
