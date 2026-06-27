# Plan 252: Demo Launch Evidence Share Center

## Goal

Add a final read-only share center for archived demo launch evidence packages, so operators can decide whether the latest final launch archive is safe to share and download one portable Markdown report.

## Scope

- Add a backend `DemoLaunchEvidenceShareCenterVo` read model derived from recent `DemoLaunchEvidencePackageArchiveVo` records.
- Expose read-only API endpoints to inspect and download the launch evidence share center.
- Add dashboard API wiring and panel UI that surfaces the latest archive, send/no-send status, download actions, evidence notes, and Markdown download.
- Document the side-effect contract and operator workflow.

## API Contract

- `GET /api/demo/launch-evidence-share-center`: returns the latest archived final launch evidence sharing status.
- `GET /api/demo/launch-evidence-share-center/report/download`: downloads the same share-center report as Markdown.

Both endpoints are read-only. They do not create tasks, call the model, run tests, archive records, mutate Git, send messages, or write to GitHub.

## Validation

- Backend targeted tests cover ready, missing-archive, and not-ready share-center derivation plus controller JSON/download responses.
- Frontend targeted tests cover API helpers, dashboard refresh wiring, share-center rendering, and report download.
- Full backend and frontend test suites plus frontend production build should pass before merge.
