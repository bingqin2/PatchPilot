# Plan 251: Demo Launch Evidence Package Archive

## Goal

Persist the final demo launch evidence package as point-in-time local evidence, so operators can preserve, reopen, and download the exact shareable package after dashboard state, webhook deliveries, tasks, or handoff records change.

## Scope

- Add backend archive storage for `DemoLaunchEvidencePackageVo`.
- Support in-memory archives in the default profile and MySQL-backed archives for `local`, `docker`, and `idea`.
- Expose endpoints to create, list, and download archived launch evidence packages.
- Add dashboard archive controls and recent archive rows to the launch evidence package panel.
- Record protected admin audit evidence when an archive is created.

## API Contract

- `POST /api/demo/launch-evidence-package/archives`: capture the current launch evidence package.
- `GET /api/demo/launch-evidence-package/archives`: list the latest 20 archives.
- `GET /api/demo/launch-evidence-package/archives/{archiveId}/report/download`: download the exact archived Markdown report.

Archive creation is PatchPilot-local evidence only. It does not create tasks, call the model, run tests, mutate Git, send messages, or write to GitHub.

## Validation

- Backend targeted tests cover archive service behavior, in-memory repository trimming, MyBatis repository conversion, Flyway migration SQL, and controller create/list/download responses.
- Frontend targeted tests cover API helpers, dashboard refresh wiring, panel archive action, recent archive rows, and archived report download.
