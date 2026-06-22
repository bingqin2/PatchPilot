# 101 Node Package Manager Adapters

## Goal

Support Node.js repositories that use pnpm or yarn while preserving PatchPilot's language-adapter safety boundary. A supported repository must have `package.json`, a non-empty `scripts.test`, and the matching lockfile. Verification remains fixed to an allowlisted command.

## Scope

- Add `NodePnpmLanguageAdapter` for `package.json` plus `pnpm-lock.yaml`, returning `pnpm test`.
- Add `NodeYarnLanguageAdapter` for `package.json` plus `yarn.lock`, returning `yarn test`.
- Keep npm support through `NodeNpmLanguageAdapter`.
- Prefer pnpm/yarn adapters before npm when lockfiles are present.
- Extend `CommandExecutionGuard` to allow only `pnpm test` and `yarn test`.
- Install pnpm and yarn in the backend runtime image for Docker Compose verification.
- Update README, product docs, decision log, and execution log.

## Non-Goals

- Do not support bun in this step.
- Do not run arbitrary package-manager commands such as `pnpm run build`, `yarn install`, or user-selected scripts.
- Do not add monorepo package discovery.
- Do not change the model patch workflow.

## Validation

- Verify pnpm and yarn adapters detect lockfile-backed repositories with `scripts.test`.
- Verify missing lockfiles, invalid `package.json`, and missing `scripts.test` are rejected.
- Verify command guard allows only fixed pnpm/yarn test commands and rejects arbitrary package-manager commands.
- Verify Spring registers pnpm/yarn adapters and prefers pnpm/yarn before npm when lockfiles are present.
- Verify the runtime Dockerfile installs pnpm and yarn.
- Run the full backend test suite before handoff.
