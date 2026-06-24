# 161 Trigger Decision Evidence

## Goal

Make accepted `/agent fix` triggers explain why they were allowed to become tasks. Operators should be able to inspect a task timeline and see whether the deterministic safety gate, issue context loading, and model trigger classification were involved.

## Scope

- Add a dedicated `TRIGGER_ACCEPTED` timeline event before the existing task-created event.
- Record deterministic safety-gate outcome, issue-context load status, and model trigger-classification outcome in one concise timeline message.
- Apply the same evidence behavior to GitHub webhook triggers and dashboard manual task creation.
- Keep dangerous, unauthorized, quarantined, and rate-limited triggers rejected before task creation.
- Update frontend timeline event typing so the dashboard accepts the new backend event type.

## Out of Scope

- No new database tables or migrations.
- No dashboard redesign for trigger-decision drilldown.
- No changes to rejection categories or rate-limit/quarantine policy.

## Verification

- Add failing tests for webhook and manual accepted trigger evidence.
- Run targeted backend tests for both trigger entry points.
- Run full backend and frontend verification before handoff.
