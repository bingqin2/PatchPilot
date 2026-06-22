# 106 Node Bun Language Adapter

## Goal

Add first-class Bun support to PatchPilot's explicit language-adapter system. A Bun repository should be detected before the broader npm adapter, verified only through `bun test`, shown in the supported-adapter API/dashboard, and covered by demo fixtures.

## Scope

- Add a Node/Bun language adapter for `package.json` projects with `bun.lockb` or `bun.lock` and a non-empty `scripts.test`.
- Allow only the fixed command `bun test`.
- Install Bun in the backend runtime image used by Docker Compose.
- Add a `docs/demo-repositories/node-bun` fixture and registry coverage.
- Add Bun to the supported adapter catalog returned by `GET /api/language-adapters`.
- Update frontend supported-adapter test data and docs.

## Non-Goals

- Running arbitrary Bun package scripts.
- Adding Bun install/sync/package management commands.
- Adding broader JavaScript framework detection.

## Validation

- Adapter, registry, Spring registration, command allowlist, runtime packaging, catalog, frontend component, and dashboard tests.
- Full backend test suite, frontend test suite, frontend production build, and whitespace check before handoff.
