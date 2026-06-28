# 281 Final Acceptance Delivery Evidence Bundle

## Goal

Expose final acceptance share delivery and finalization evidence from the top-level demo evidence bundle, copied runbook, and dashboard bundle panel. Operators should be able to prove the reviewer-facing acceptance handoff from the first demo readout without opening a separate final acceptance panel.

## Scope

- Add `finalAcceptanceShareFinalization` to the backend demo evidence bundle read model.
- Include final acceptance share finalization in bundle aggregate status and next actions.
- Append final acceptance delivery receipt, target, freshness, archive, task, and next action lines to the copied demo runbook.
- Render a dashboard evidence card for final acceptance delivery/finalization.
- Keep legacy bundle fallbacks for older responses.

## Non-Goals

- Do not create new delivery receipts or archives.
- Do not change the final acceptance share package workflow.
- Do not call GitHub, the model provider, or task execution from this feature.

## Verification

- RED/GREEN backend tests for bundle aggregation, controller JSON, and runbook markdown.
- RED/GREEN frontend tests for the dashboard evidence card and legacy fallback.
- Full backend Maven tests and frontend test/build before merge.
