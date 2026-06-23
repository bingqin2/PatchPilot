# 127 Go Language Adapter

## Goal

Add first-class Go repository support so PatchPilot can safely run issue-to-PR tasks for Go modules without falling back to arbitrary shell execution.

This advances the multi-language target by adding a high-signal adapter with a stable project marker (`go.mod`) and one fixed verification command (`go test ./...`). It also improves demo readiness because the dashboard support matrix and fixture verification can show Go alongside Java, Node, and Python.

## Scope

- Detect Go module repositories through `go.mod`.
- Return adapter metadata `language=go`, `buildSystem=go`, and verification command `go test ./...`.
- Allow only the fixed Go verification command in `CommandExecutionGuard`.
- Add a minimal Go fixture under `docs/demo-repositories/go-module`.
- Include Go in the supported adapter catalog, fixture verification, Spring context coverage, and dashboard fixture data.
- Update README, product spec, architecture notes, frontend design notes, and progress logs.

## Non-Goals

- Do not support arbitrary `go test` package patterns.
- Do not run `go mod tidy`, `go env`, `go generate`, or dependency mutation commands.
- Do not add custom Go build, lint, or integration-test runners.

## Validation

- `mvn -pl PatchPilot -Dtest=GoLanguageAdapterTests,LanguageAdapterRegistryTests,CommandExecutionGuardTests test`
- `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,LanguageAdapterCatalogServiceTests,LanguageAdapterFixtureVerificationServiceTests test`
- `npm test -- App.test.tsx SupportedAdaptersPanel.test.tsx`
- `mvn -pl PatchPilot test`
- `npm test`
- `npm run build`
- `git diff --check`
