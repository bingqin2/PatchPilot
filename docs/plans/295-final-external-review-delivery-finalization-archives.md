# 295 Final External Review Delivery Finalization Archives

## Goal

Freeze the READY final external-review package delivery finalization as durable PatchPilot-local evidence. After the latest frozen reviewer-facing package has a fresh delivery receipt, operators should be able to archive that delivery finalization, reopen it later, download the Markdown report, and see the latest archived proof from the first demo evidence readout.

## Scope

- Persist final external-review package delivery finalization archives in memory and MySQL.
- Create archives only when the current delivery finalization is `READY` and finalized.
- Store linked package archive, delivery receipt, completion evidence ids, task, Pull Request, receipt freshness, checks, evidence notes, generated time, archived time, and Markdown report.
- Expose create, list, and archive report download endpoints.
- Repeat the latest archive in the top-level evidence bundle and copied runbook.
- Add dashboard controls to archive the current finalization, list recent archives, download archived reports, and refresh evidence after archive creation.

## Non-Goals

- Do not send external messages.
- Do not create tasks, call the model, run tests, mutate Git, record receipts, push branches, create Pull Requests, or write GitHub comments.
- Do not replace the live read-only delivery finalization gate; archive records are durable snapshots of that gate.

## Verification

- Backend tests cover READY-only archive creation, in-memory ordering, MyBatis conversion and migration, controller create/list/download routes, evidence-bundle aggregation, and runbook export.
- Frontend tests cover archive API helpers, final acceptance dashboard archive controls/history/downloads, and top-level evidence-bundle archive rendering.
- Run focused backend tests, targeted frontend tests, frontend production build, and `git diff --check` before merging.
