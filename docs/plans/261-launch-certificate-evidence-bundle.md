# 261 - Launch Certificate Evidence Bundle

## Goal

Surface the latest archived launch acceptance certificate in the top-level demo evidence bundle and copied runbook so operators can prove final external-review readiness without opening the launch evidence panel first.

## Scope

- Add a launch acceptance certificate evidence read model to the demo evidence bundle.
- Read the latest certificate archive from the certificate archive repository.
- Make the evidence bundle and runbook report certificate archive id, certified state, linked closeout/archive evidence, Pull Request, delivery receipt, freshness, summary, next action, and download actions.
- Show the certificate evidence in the dashboard evidence bundle panel.
- Update README, product spec, frontend design notes, and execution log.

## Acceptance Criteria

- The evidence bundle reports `READY` only when the latest certificate archive is `READY` and certified.
- Missing or uncertified certificate archive evidence produces a clear next action.
- The copied demo runbook includes the latest certificate archive proof.
- The dashboard evidence bundle shows the certificate archive status, certified state, linked closeout archive, delivery receipt, and Pull Request link.
- Backend and frontend tests cover ready, missing, and display paths.
