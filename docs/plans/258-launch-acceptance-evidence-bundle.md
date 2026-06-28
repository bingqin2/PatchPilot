# 258 - Launch Acceptance Evidence Bundle

## Goal

Make the final launch acceptance closeout visible in the top-level demo evidence bundle, runbook, and dashboard. A reviewer should not need to open the launch package or closeout archive panels to confirm whether final launch acceptance evidence has been archived and accepted.

## Scope

- Add a compact launch acceptance closeout evidence read model to the demo evidence bundle.
- Read the latest closeout archive directly from the archive repository to avoid a service dependency cycle.
- Downgrade the evidence bundle when no accepted closeout archive exists.
- Render the closeout evidence in the dashboard bundle panel.
- Include the closeout archive in the generated demo runbook.

## Out of Scope

- Creating or mutating closeout archives from the evidence bundle endpoint.
- Changing the closeout archive persistence schema.
- Reworking the broader launch evidence package workflow.

## Verification

- Backend fail-first tests for bundle and runbook closeout evidence.
- Frontend fail-first tests for dashboard closeout display.
- Full backend Maven tests.
- Full frontend Vitest suite and production build.
