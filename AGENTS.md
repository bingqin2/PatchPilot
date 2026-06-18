# Repository Guidelines

## Project Structure & Module Organization

PatchPilot is a Maven/Spring Boot backend project for an AI GitHub issue-to-PR agent. The root `pom.xml` is the parent build; the active backend module lives in `PatchPilot/`. Java source is under `PatchPilot/src/main/java/io/patchpilot/backend`, configuration under `PatchPilot/src/main/resources`, and tests under `PatchPilot/src/test/java`. Product, architecture, execution plans, and progress logs live in `docs/`; start with `docs/product/spec.md`, `docs/product/architecture.md`, and `docs/product/backend-code-standard.md`. `docker-compose.yml` defines local MySQL and backend services. `frontend/` is reserved for the planned React/Vite dashboard.

## Build, Test, and Development Commands

- `cd PatchPilot && ./mvnw test`: run backend unit and Spring context tests.
- `cd PatchPilot && ./mvnw spring-boot:run`: start the backend locally.
- `mvn -pl PatchPilot test`: run the backend module tests from the repository root when Maven is installed.
- `docker compose up --build`: start MySQL and the backend container for integration-style local checks.

Use `mvn` only if the wrapper is unavailable or not executable.

## Coding Style & Naming Conventions

Target Java 17 and Spring Boot 3.5.x. Use standard Java formatting with 4-space indentation. Follow package-by-domain structure from `docs/product/backend-code-standard.md`: controllers stay thin, services own business transitions, and external systems are hidden behind provider/client interfaces. Prefer constructor injection with `private final` dependencies. Use suffixes consistently: `*Controller`, `*Service`, `*ServiceImpl`, `*Client`, `*Tool`, `*Runner`, `*Properties`, `*Dto`, `*Vo`, `*Bo`, `*Entity`, and `*Query`. Application configuration should use the `patchpilot` prefix; never commit secrets.

## Testing Guidelines

The current test stack is JUnit 5 with Spring Boot Test. Keep tests in the matching package under `PatchPilot/src/test/java` and name test classes `*Tests`. Add focused unit tests for service logic and Spring tests only where container context matters. Run `./mvnw test` before handing off changes. When MySQL-backed features are added, prefer Testcontainers for integration tests.

## Commit & Pull Request Guidelines

No Git history is available in this checkout to infer a project-specific convention. Use short imperative commit subjects, for example `Add webhook signature validation`. Keep commits scoped to one behavior or documentation change. Pull requests should include a concise summary, linked issue or plan, test evidence, configuration changes, and screenshots only for UI-facing work. Record architectural deviations or durable decisions in `docs/progress/decisions.md`.

## Agent-Specific Instructions

For long-running implementation work, create or update an execution plan in `docs/plans/` and log meaningful progress in `docs/progress/execution-log.md`. Do not execute arbitrary model-generated shell commands; follow the allowlisted command policy in the backend standard.
