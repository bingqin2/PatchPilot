# 275 Launch Package Final Handoff Archive Proof

## Goal

Carry final handoff report package archive proof into the launch evidence package, archived launch package rows, launch evidence share center, and dashboard panel so the final shareable launch artifact proves post-demo closeout archive readiness.

## Why This Matters

The evidence bundle reports whether the final handoff report package archive exists, but the operator's final shareable artifact is the launch evidence package. Reviewers should not need a separate evidence-bundle lookup to confirm that the post-demo handoff package was archived and download-ready.

## Scope

- Add final handoff report package archive identifiers and readiness fields to launch evidence package and archive VOs.
- Persist those fields in launch evidence package archives through Flyway, entity, converter, and repository coverage.
- Include final handoff archive proof in launch evidence package Markdown and share-center notes/actions.
- Render the proof in the launch evidence package dashboard panel and archived launch package list.
- Update README, execution log, and tests.

## Non-Goals

- Do not create final handoff report package archives automatically.
- Do not change launch acceptance closeout or certificate semantics in this slice.
- Do not mutate GitHub, send messages, run verification commands, create tasks, or call the model from read-only package/share-center endpoints.

## Validation

- Backend RED tests first for package JSON/Markdown, archive persistence, migration columns, and share-center evidence.
- Frontend RED tests first for launch package and archived package rendering.
- Full backend tests, full frontend tests, frontend build, and `git diff --check` before merge.
