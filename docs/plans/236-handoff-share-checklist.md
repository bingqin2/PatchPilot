# 236 - Handoff Share Checklist

## Goal

Make the post-demo handoff state explicit before an operator shares evidence. The feature converts the latest handoff package archive summary into a small, read-only share checklist with status, checks, next action, and copyable Markdown evidence.

## Scope

- Add a backend read model for handoff share readiness.
- Expose `GET /api/demo/handoff-share-checklist`.
- Render the checklist in the demo session snapshot panel.
- Let operators copy the checklist Markdown from the dashboard.
- Refresh the checklist after archiving a new handoff package.
- Document the side-effect boundary and verification path.

## Non-Goals

- Do not create tasks, queue work, call the model, run tests, mutate Git, or write to GitHub.
- Do not add another handoff archive write path.
- Do not replace the existing handoff package or archive summary endpoints.

## Implementation Notes

- The service consumes `DemoHandoffPackageArchiveService.getArchiveSummary()`.
- The overall checklist status is `BLOCKED` if any check is blocked, `NEEDS_ATTENTION` if any check needs attention, otherwise `READY`.
- The dashboard treats the checklist as evidence, not as a trigger surface.

## Verification

- RED: backend tests failed because `DemoHandoffShareChecklist*` types and service did not exist; frontend tests failed because `getDemoHandoffShareChecklist` and the panel controls did not exist.
- GREEN: add service, endpoint, API helper, App wiring, dashboard panel, and documentation.
- Final checks: full backend tests, full frontend tests, frontend production build, and whitespace diff check.
