# Node Npm Language Adapter

## Goal

Support Node.js repositories that use npm and define a `package.json` `scripts.test` command. These repositories should pass language preflight, run a fixed allowlisted `npm test` verification command, and continue through the existing issue-to-PR workflow.

## Scope

- Add `NodeNpmLanguageAdapter` for `package.json` detection.
- Require `scripts.test` to be present and non-empty before marking the repository supported.
- Return the fixed verification command `npm test`.
- Register the Node/npm adapter after Java/Maven and Java/Gradle adapters.
- Extend `CommandExecutionGuard` to allow only `npm test`.
- Install `nodejs` and `npm` in the backend runtime image for Docker Compose execution.
- Update tests, supported-repository documentation, command allowlist documentation, and progress logs.

## Non-Goals

- Support pnpm, yarn, bun, or custom package managers in this step.
- Run arbitrary npm scripts such as `npm run lint`, `npm run build`, or model-selected commands.
- Install dependencies automatically before verification.
- Add frontend changes or adapter-aware dashboard labels.

## Validation Plan

- Verify `package.json` with `scripts.test` is detected as Node/npm.
- Verify missing, invalid, or no-test-script `package.json` files are unsupported.
- Verify the command guard allows `npm test` and still rejects arbitrary commands.
- Verify the generic verification runner can execute `npm test`.
- Verify Spring registers the Node/npm adapter.
- Verify the runtime Dockerfile includes `nodejs` and `npm`.
- Run full backend tests and whitespace checks before merging.
