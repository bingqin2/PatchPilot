# 090 Rejected Trigger Audit Log

## Goal

Make rejected `/agent fix` attempts visible without turning them into executable tasks.

## Scope

- Record rejected webhook and manual triggers with repository, issue, user, command, reason, source, delivery id, and timestamp.
- Store the audit log in memory for default tests and in MySQL for local/docker/IDEA profiles.
- Expose `GET /api/rejected-triggers` with a bounded `limit` parameter for operator inspection.
- Keep rejected trigger records separate from task records so task lists continue to represent executable work.

## Design

Rejected trigger auditing is owned by `RejectedTriggerAuditService`. `GitHubWebhookService` records an audit row when `CommandSafetyGate` rejects a triggering issue comment. `DefaultManualFixTaskService` records an audit row when dashboard/manual task creation is rejected by the same safety gate. Non-triggering comments remain ignored without an audit entry.

## Validation

- Service tests cover in-memory and MyBatis audit recording/listing.
- Controller tests cover `GET /api/rejected-triggers` and limit validation.
- Webhook and manual task tests verify rejected requests are audited before task creation.
- Migration tests verify the MySQL table shape.
