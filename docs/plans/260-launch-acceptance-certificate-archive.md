# 260 - Launch Acceptance Certificate Archive

## Goal

Freeze the final launch acceptance certificate into a durable archive so reviewers can inspect a stable, shareable acceptance proof after the live certificate read model changes.

## Scope

- Add backend archive VO/entity/mapper/converter/repository/service layers for launch acceptance certificates.
- Add MySQL persistence with Flyway and in-memory default storage.
- Add protected archive creation, archive listing, and archived Markdown download endpoints.
- Add dashboard archive controls and history for certificate evidence.
- Update product/frontend docs and execution log.

## Acceptance Criteria

- Operators can archive the current launch acceptance certificate from the dashboard.
- The archive captures certified status, summary, evidence ids, generated time, archived time, download actions, and Markdown report.
- The latest 20 archives are listed newest first.
- Archived certificate reports can be downloaded by archive id.
- The feature is covered by backend, frontend, migration, and controller tests.
