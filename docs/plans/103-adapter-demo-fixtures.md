# 103 Adapter Demo Fixtures

## Goal

Add small repository fixtures that demonstrate every currently supported language adapter shape. The fixtures should make multi-language support reviewable and testable without requiring external GitHub repositories.

## Scope

- Add `docs/demo-repositories/` with one fixture per supported adapter.
- Include minimal manifests, lockfiles, source files, tests, and fixture README files.
- Cover Java/Maven, Java/Gradle, Node/npm, Node/pnpm, Node/yarn, Python/pytest, Python/Poetry, and Python/uv.
- Add a backend regression test that runs `LanguageAdapterRegistry.detect(...)` against every fixture and verifies the selected `language`, `buildSystem`, and verification command.
- Update README, roadmap, target-state, and execution log.

## Non-Goals

- Do not execute every fixture's verification command in the main backend test suite.
- Do not create external GitHub repositories.
- Do not add full demo issues or benchmark scoring in this step.
- Do not add unsupported adapter fixtures.

## Validation

- Verify every fixture is detected by the expected adapter.
- Run the full `LanguageAdapterRegistryTests` class.
- Run the full backend test suite.
- Run `git diff --check`.
