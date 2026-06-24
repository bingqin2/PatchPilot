# 162 Trigger Decision Visibility

## Goal

Make trigger decisions visible in one dashboard location. Operators should be able to compare the selected task's accepted-trigger evidence with recent rejected `/agent fix` decisions without jumping between the task timeline and the rejected-trigger audit panel.

## Scope

- Add a `TriggerDecisionPanel` to the React dashboard.
- Show the selected task's `TRIGGER_ACCEPTED` timeline message when available.
- Show recent rejected trigger rows with category, repository, command, reason, and timestamp.
- Show a compact rejected-trigger category summary from the existing rejected-trigger summary API.
- Reuse existing task detail, timeline, rejected-trigger, and summary data. Do not add backend endpoints, database tables, or task-triggering behavior.
- Keep the existing rejected-trigger panel as the full audit and quarantine operations surface.

## Out of Scope

- No backend schema changes.
- No new model calls or trigger-classification behavior.
- No quarantine create/release controls in the new panel.
- No replacement for the detailed task timeline or rejected-trigger audit panel.

## Verification

- Add a failing dashboard integration test that expects a `Trigger decisions` region.
- Add component coverage for accepted evidence, rejected decisions, and empty states.
- Run focused frontend tests for the dashboard and new panel.
- Run the full frontend test suite and production build before handoff.
