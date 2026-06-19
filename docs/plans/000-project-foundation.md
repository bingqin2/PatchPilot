# Project Foundation

This ExecPlan is a living document. Keep `Progress`, `Surprises & Discoveries`, `Decision Log`, and `Outcomes & Retrospective` updated as work proceeds.

## Purpose / Big Picture

This plan establishes the foundation for PatchPilot, a Java/Spring Boot GitHub issue-to-PR agent backend.

After this work, a new contributor should understand the product goal, run backend tests, start the application locally, and see a basic health check prove that the backend is alive.

This phase does not implement the full agent workflow. It creates the project baseline that later phases can build on safely.

## Progress

- [x] Create product documents under `docs/product`.
- [x] Create agent governance documents under `docs/agent`.
- [x] Create progress tracking documents under `docs/progress`.
- [x] Add or verify root ignore rules.
- [x] Add Spring Web dependency.
- [x] Add Spring Boot Actuator dependency.
- [x] Add local profile configuration.
- [x] Add a health endpoint if Actuator is not enough for the demo.
- [x] Add tests for application startup and health.
- [x] Run Maven tests.
- [x] Record validation results in `docs/progress/execution-log.md`.

## Surprises & Discoveries

- Observation: The generated backend currently contains a minimal Spring Boot application and test.
  Evidence: `PatchPilotApplication.java`, `PatchPilotApplicationTests.java`, and `pom.xml` exist in the backend project.

- Observation: The current Maven project uses Spring Boot 3.5.15 and Java 17.
  Evidence: `pom.xml` defines `spring-boot-starter-parent` version `3.5.15` and `java.version` as `17`.

- Observation: Mockito's inline mock maker cannot self-attach in the local macOS/JDK test environment.
  Evidence: `mvn test` failed until test resources selected `mock-maker-subclass`.

- Observation: Docker image builds can be blocked by configured registry mirrors before Dockerfile execution.
  Evidence: `docker compose build patchpilot-backend` initially failed against `https://docker.mirrors.ustc.edu.cn`, then passed after the base images were pulled successfully.

## Decision Log

- Decision: Keep Phase 0 focused on documentation, local runtime, and health validation.
  Rationale: The GitHub agent workflow has several high-risk integrations. The repository should first be understandable and runnable.
  Date/Author: 2026-06-18 / Codex

- Decision: Use the `docs/product`, `docs/plans`, `docs/progress`, and `docs/agent` layout.
  Rationale: This follows the reference documentation structure from `/Users/wangbingqin/Documents/coding/docs`.
  Date/Author: 2026-06-18 / Codex

## Outcomes & Retrospective

The basic backend foundation is implemented. The root Maven build runs tests and packages the Spring Boot backend with Java 17, `/health` is covered by MockMvc, Actuator health is configured, Docker Compose syntax is valid, and the backend Docker image builds as `patchpilot-backend:local`.

## Context and Orientation

The backend project currently lives at:

```text
/Users/wangbingqin/Documents/agent/PatchPilot
```

Important files:

```text
pom.xml
src/main/java/io/patchpilot/backend/PatchPilotApplication.java
src/main/resources/application.properties
src/test/java/io/patchpilot/backend/PatchPilotApplicationTests.java
```

The docs directory should live at:

```text
/Users/wangbingqin/Documents/agent/docs
```

The term "health endpoint" means a small HTTP endpoint or Actuator endpoint that returns success when the backend is running. It lets future deployment and demo scripts verify basic liveness.

## Plan of Work

First, copy the accepted documentation structure into `docs/`. Then add backend runtime dependencies required for a web application and health checks.

Next, create local profile configuration and a minimal health surface. After the application starts, add tests that prove the Spring context and health endpoint work.

Finally, run validation commands and record exact results in the execution log.

## Concrete Steps

Run commands from the backend project root unless otherwise stated:

```bash
cd /Users/wangbingqin/Documents/agent/PatchPilot
```

Inspect current project:

```bash
find . -maxdepth 3 -type f | sort
mvn test
```

After dependency and health changes, run:

```bash
mvn test
```

Start the application locally:

```bash
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run
```

In another terminal, verify health:

```bash
curl -i http://localhost:8080/actuator/health
```

If a custom `/health` endpoint is added:

```bash
curl -i http://localhost:8080/health
```

## Validation and Acceptance

This plan is accepted when:

- Documentation exists under the accepted `docs/` layout.
- `mvn test` passes.
- The backend starts with the local profile.
- A health endpoint returns HTTP 200.
- `docs/progress/execution-log.md` records the validation commands and results.

## Idempotence and Recovery

Adding documentation is safe to repeat as long as new documents do not overwrite accepted history without review.

If dependency changes break Maven resolution, inspect `pom.xml` and revert only the dependency added by the current task.

If the backend cannot start because port 8080 is in use, either stop the conflicting process or set `SERVER_PORT` for local testing and record the change.

## Artifacts and Notes

Expected artifacts:

```text
docs/product/spec.md
docs/product/architecture.md
docs/product/backend-code-standard.md
docs/product/target-state.md
docs/product/roadmap.md
docs/product/milestones.md
docs/progress/decisions.md
docs/progress/execution-log.md
docs/agent/vibe-coding.md
docs/agent/harness-engineering.md
docs/plans/000-project-foundation.md
```

Expected backend artifacts after implementation:

```text
pom.xml
src/main/resources/application-local.properties
src/main/java/io/patchpilot/backend/observability/HealthController.java
src/test/java/io/patchpilot/backend/observability/HealthControllerTests.java
```

## Interfaces and Dependencies

Expected dependencies for this phase:

- Spring Web for a simple web surface.
- Spring Boot Actuator for standard health checks.
- Spring Boot Test for context and controller tests.

No GitHub, model provider, database, queue, or agent dependencies are required in Phase 0.
