# 269 Task Certificate Evidence Bundle

## Goal

Surface the latest archived task evidence acceptance certificate in the top-level demo evidence bundle and copied runbook so operators can prove task-level review readiness without opening the task evidence archive review panel first.

## Scope

- Add a task evidence acceptance certificate read model to the demo evidence bundle.
- Read the latest task evidence acceptance certificate archive from the existing task evidence certificate archive repository.
- Require the top-level evidence bundle to include a certified task evidence certificate before reporting `READY`.
- Make the copied runbook report certificate archive id, linked closeout archive, linked task evidence archive, delivery receipt, task id, Pull Request, summary, next action, and download actions.
- Show the certificate evidence in the dashboard evidence bundle panel.
- Update README and the execution log.

## Non-Goals

- Do not create task evidence certificate archives from the evidence bundle endpoint.
- Do not send external messages, create tasks, call the model, run tests, mutate Git, push branches, open Pull Requests, or write GitHub comments.
- Do not replace the dedicated task evidence archive review, share center, finalization, closeout, or certificate panels.

## Acceptance Criteria

- The evidence bundle reports `READY` only when the latest task evidence acceptance certificate archive is `READY` and certified.
- Missing or uncertified task certificate archive evidence produces a clear next action.
- The copied demo runbook includes the latest task certificate archive proof.
- The dashboard evidence bundle shows the task certificate archive status, certified state, linked closeout/archive evidence, delivery receipt, task id, and Pull Request link.
- Backend and frontend tests cover ready, missing, runbook, REST serialization, and display paths.
