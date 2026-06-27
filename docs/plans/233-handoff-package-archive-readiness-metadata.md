# 233 - Handoff Package Archive Readiness Metadata

## Goal

Make archived demo handoff packages self-describing in archive lists. Operators should know whether an archived package was handoff-ready, what action remained, and how many checks were ready, warning, or blocked without opening the Markdown report.

## Scope

- Persist handoff readiness status, summary, overall next action, and check counts on `demo_handoff_package_archive`.
- Reuse the same backend handoff readiness calculation used by structured readiness responses and generated handoff packages.
- Preserve existing package archive fields and old in-memory/default-profile behavior.
- Show archived handoff readiness metadata in the dashboard's recent handoff package archive list.
- Keep archive creation PatchPilot-local and admin-audited.

## Non-Goals

- Do not change readiness scoring rules.
- Do not persist browser-local command or outcome history as separate tables.
- Do not create tasks, call the model, run verification commands, mutate Git, write GitHub comments, or open Pull Requests.
- Do not move session report archive metadata into handoff package archive rows.

## Validation

- Backend focused tests cover archive service metadata capture, MyBatis conversion/persistence, Flyway schema migration, and controller JSON fields.
- Frontend focused tests cover API parsing and dashboard rendering for archived handoff readiness metadata.
- Full backend and frontend regression checks must pass before merging.
