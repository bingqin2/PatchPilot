# 325 Live Trigger Launch Package Archives

## Goal

Persist live trigger launch packages as operator evidence so a demo run can prove exactly which issue/comment package was approved before posting a real `/agent fix` comment.

## Scope

- Add backend archive support for live trigger launch packages.
- Store a frozen package snapshot with archive id, package generation time, archive time, report body, status, repository, issue, trigger user/comment, operator handoff evidence, and side-effect contract.
- Add admin-protected endpoints to create, list, and download package archives.
- Add typed frontend API helpers and dashboard actions to archive the current package, display recent archives, and download archived Markdown reports.
- Keep archive creation read-only with respect to GitHub, Git, model calls, task execution, and Pull Request creation.
- Update execution progress documentation and run backend/frontend verification.

## API Shape

- `POST /api/demo/live-trigger-launch-package/archives`: build the package from the supplied live trigger input and archive the frozen report.
- `GET /api/demo/live-trigger-launch-package/archives`: list recent package archives, newest first.
- `GET /api/demo/live-trigger-launch-package/archives/{archiveId}/report/download`: download the archived Markdown report.

## Safety Contract

Archive creation writes only PatchPilot's local archive record. It must not create tasks, enqueue work, call the model directly, mutate Git, push branches, open Pull Requests, write GitHub comments, or expose secrets.

## Validation Checklist

- [x] Backend RED test proves archive service/controller are missing.
- [x] Frontend RED test proves archive API/panel behavior is missing.
- [x] Focused backend tests pass.
- [x] Focused frontend tests pass.
- [x] App smoke tests pass.
- [x] Full backend tests pass.
- [x] Full frontend tests pass.
- [x] Frontend production build passes.
- [x] Diff check and secret scan pass.
