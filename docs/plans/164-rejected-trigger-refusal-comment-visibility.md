# 164 Rejected Trigger Refusal Comment Visibility

## Goal

Make refusal comments visible from the quick trigger-decision view. Operators should be able to select a task, compare accepted and rejected trigger decisions, and open the GitHub refusal comment for a rejected trigger without scrolling to the full rejected-trigger audit panel.

## Scope

- Reuse existing `RejectedTriggerAudit.commentUrl` data loaded by the dashboard.
- Show a `Refusal comment` link on rejected rows in `TriggerDecisionPanel` when the URL is present.
- Keep rows without a refusal comment link compact and unchanged.
- Preserve `RejectedTriggerPanel` as the full audit, retry, quarantine, and operator safety workspace.
- Update frontend documentation and progress notes.

## Out of Scope

- No backend API, database, or schema changes.
- No new GitHub comment creation behavior.
- No retry, quarantine, or redelivery controls in `TriggerDecisionPanel`.

## Verification

- Add a failing `TriggerDecisionPanel` component test that expects a `Refusal comment` link on rejected trigger rows with `commentUrl`.
- Implement the link and matching compact metadata styling.
- Run focused component tests, then full frontend tests and production build.
