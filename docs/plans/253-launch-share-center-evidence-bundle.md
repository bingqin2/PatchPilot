# Plan 253: Launch Share Center Evidence Bundle

## Goal

Surface the final launch evidence share center in the top-level demo evidence bundle so operators can decide from the first dashboard readout whether the latest archived launch evidence is ready to share.

## Scope

- Add launch evidence share-center fields to the backend `DemoEvidenceBundleVo`.
- Reuse `DemoLaunchEvidenceShareCenterService` inside `DemoEvidenceBundleService` so the bundle and standalone share center share one source of truth.
- Render a `Launch evidence share center` record in the dashboard evidence bundle panel with status, summary, next action, latest archive/session/PR evidence, and download actions.
- Update docs and execution log to describe the first-screen share/no-share decision.

## API Contract

`GET /api/demo/evidence-bundle` should include:

- `launchEvidenceShareCenterStatus`
- `launchEvidenceShareCenterReady`
- `launchEvidenceShareCenterSummary`
- `launchEvidenceShareCenterNextAction`
- `launchEvidenceShareCenterArchiveCount`
- `launchEvidenceShareCenterLatestArchiveId`
- `launchEvidenceShareCenterLatestSessionId`
- `launchEvidenceShareCenterLatestPullRequestUrl`
- `launchEvidenceShareCenterDownloadActions`

This remains read-only. It must not create tasks, call the model, run tests, create archives, mutate Git, send messages, or write to GitHub.

## Validation

- Backend tests first fail because the evidence bundle lacks launch share-center fields, then pass after service and VO updates.
- Frontend tests first fail because the evidence bundle panel lacks the launch share-center record, then pass after rendering updates.
- Full backend tests, frontend tests, frontend production build, and `git diff --check` must pass before merge.
