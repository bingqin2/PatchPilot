# 036 Self-Hosted README And Env Docs

## Goal

Make PatchPilot easier to run and demo from a fresh checkout by consolidating self-hosted setup, environment variables, webhook setup, and smoke-test steps.

## Scope

- Rewrite `README.md` around current working capabilities.
- Add `.env.example` for local self-hosted configuration.
- Ensure Docker Compose passes GitHub, workspace, and model provider environment variables to the backend.
- Add an end-to-end smoke test checklist for local demos.
- Update existing local tunnel and IDEA guides where they are stale.
- Do not change backend runtime behavior beyond environment variable wiring.

## Tasks

- [x] Document current workflow and runtime prerequisites in `README.md`.
- [x] Add `.env.example` with GitHub, model provider, server, and workspace settings.
- [x] Ignore local `.env` files while keeping `.env.example` tracked.
- [x] Pass model provider variables through `docker-compose.yml`.
- [x] Add `docs/agent/smoke-test-checklist.md`.
- [x] Update temporary URL and IDEA local run docs.
- [x] Run backend tests.

## Acceptance Criteria

- [x] A new user can identify required configuration from `.env.example`.
- [x] README includes Docker Compose, Cloudflare Tunnel, webhook, trigger, and task-inspection commands.
- [x] Local secrets are ignored by Git.
- [x] `mvn -pl PatchPilot test` passes.
