# 276 Launch Acceptance Final Handoff Proof

## Goal

Carry final handoff report package archive proof from the launch evidence package into the launch acceptance closeout, closeout archives, launch acceptance certificate, certificate archives, and dashboard panel.

## Why This Matters

The launch package already proves the post-demo final handoff report package archive is ready. The final accepted closeout and certified launch record should preserve the same archive proof so external review does not depend on reopening an earlier package panel.

## Scope

- Add final handoff report package archive status, ready flag, archive id, and summary fields to closeout and certificate read models.
- Persist those fields in closeout and certificate archives through Flyway migrations, entities, converters, and repository coverage.
- Include the proof in closeout and certificate checks, evidence notes, download actions, and Markdown reports.
- Render the proof in the launch evidence package dashboard closeout/certificate sections and recent archive rows.
- Update README, product specs, frontend design docs, execution log, and regression tests.

## Non-Goals

- Do not create final handoff report package archives automatically.
- Do not change launch evidence package archive creation semantics.
- Do not mutate GitHub, send messages, run verification commands, create tasks, record receipts, or call the model from read-only closeout/certificate endpoints.

## Validation

- Backend RED tests first for closeout/certificate read models, archives, converters, migrations, repositories, controller JSON, Markdown, and download actions.
- Frontend RED tests first for closeout/certificate proof rendering.
- Full backend tests, full frontend tests, frontend build, and `git diff --check` before merge.
