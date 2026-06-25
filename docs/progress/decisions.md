# Decision Log

This file records project-level decisions that affect future implementation. Feature-specific decisions should also be recorded in the relevant ExecPlan under `docs/plans/`.

## 2026-06-18

Decision: Build PatchPilot as an AI GitHub issue-to-PR backend instead of a generic chatbot.

Reason: The project should demonstrate backend engineering depth for internships: GitHub integration, async task execution, repository automation, tool calling, testing, safety boundaries, and observability.

Impact: The MVP focuses on GitHub App webhook handling, `/agent fix`, Java/Maven repositories, patch generation, test execution, and Pull Request creation.

## 2026-06-18

Decision: Use Java and Spring Boot as the primary backend stack.

Reason: The project is intended to align with a Java backend internship profile.

Impact: Agent concepts from Python projects should be translated into Java backend patterns instead of embedding Python notebooks or demos directly.

## 2026-06-18

Decision: Start with a modular monolith.

Reason: The first technical risk is proving a safe issue-to-PR workflow, not distributed deployment.

Impact: The backend starts as one Spring Boot service with clear internal modules. API/worker split remains a future evolution after the MVP works.

## 2026-06-18

Decision: Use GitHub App webhook commands as the first trigger, not a browser extension.

Reason: GitHub App permissions, webhooks, and comments are closer to a real product backend and simpler to secure than injecting UI into GitHub pages.

Impact: The first trigger is `/agent fix` on an issue comment. A Chrome extension can be added later as a demo-friendly trigger over the same backend API.

## 2026-06-18

Decision: Do not automatically merge Pull Requests.

Reason: AI-generated code changes require human review. Auto-merge would create unnecessary safety and trust risks.

Impact: PatchPilot creates reviewable Pull Requests only. Merge decisions stay with repository maintainers.

## 2026-06-18

Decision: Start with Java/Maven repositories.

Reason: Narrowing the repository type makes the first MVP testable and credible.

Impact: The first test runner supports `./mvnw test` and `mvn test`. Gradle and other languages are future phases.

## 2026-06-18

Decision: Use MySQL as the project database baseline.

Reason: MySQL is common in Java backend roles and fits the project goal of demonstrating practical backend engineering.

Impact: Persistence plans should target MySQL with Flyway migrations and MyBatis-Plus data access. H2 may be used only for narrow tests if it lowers setup cost.

## 2026-06-18

Decision: Use React for the frontend.

Reason: React is widely recognized, works well for an operations dashboard, and can show task status, traces, test output, and Pull Request links clearly.

Impact: The frontend should start as a React + Vite + TypeScript dashboard after backend task APIs exist. The first backend trigger remains GitHub issue comments, not the React UI.

## 2026-06-18

Decision: Use local self-hosted deployment first and defer public hosted usage.

Reason: The project should prove the issue-to-PR workflow on personal repositories before exposing a hosted GitHub App to external users.

Impact: Current implementation plans should optimize for local Docker Compose, personal GitHub App setup, and tunnel-based webhook testing. Public hosted service requirements remain in the target state and roadmap as a later maturity phase.

## 2026-06-22

Decision: Add Java/Gradle as the second supported repository adapter after Java/Maven.

Reason: Gradle support moves PatchPilot toward multi-build-system Java coverage while keeping execution behind explicit language adapters and fixed verification command allowlists.

Impact: Java repositories using `gradlew`, `build.gradle`, or `build.gradle.kts` can pass repository preflight and run `./gradlew test` or `gradle test`. Node.js and Python remain future adapter work.

## 2026-06-22

Decision: Add Node/npm as the first supported JavaScript repository adapter.

Reason: npm is the simplest controlled Node.js path and moves PatchPilot toward multi-language support without allowing arbitrary package-manager commands.

Impact: Node.js repositories with `package.json` and a non-empty `scripts.test` can pass repository preflight and run `npm test`. pnpm, yarn, and Python remain future adapter work.

## 2026-06-22

Decision: Add Python/pytest as the first supported Python repository adapter.

Reason: pytest is the simplest controlled Python verification path and expands PatchPilot beyond JavaScript while keeping execution behind explicit adapter detection and a fixed command allowlist.

Impact: Python repositories with pytest configuration or a pytest dependency can pass repository preflight and run `python3 -m pytest`. Poetry, tox, nox, uv, and custom Python runners remain future adapter work.

## 2026-06-22

Decision: Add pnpm and yarn as explicit Node.js package-manager adapters instead of treating every Node project as npm.

Reason: Many Node repositories use lockfile-specific package managers. Detecting `pnpm-lock.yaml` and `yarn.lock` before npm improves real repository coverage without allowing arbitrary package-manager commands.

Impact: Node.js repositories with matching lockfiles and a non-empty `scripts.test` can pass repository preflight and run `pnpm test` or `yarn test`. The command guard still rejects arbitrary pnpm/yarn scripts and install commands.

## 2026-06-22

Decision: Add Poetry and uv as explicit Python project-runner adapters instead of expanding the plain pytest adapter.

Reason: Poetry and uv are common Python project managers, but allowing their install or dependency-management commands would widen execution risk. Separate adapters can require pytest signals and return fixed verification commands.

Impact: Python repositories with Poetry or uv plus pytest configuration or dependency can pass repository preflight and run `poetry run pytest` or `uv run pytest`. The command guard still rejects install, sync, pip, lock, and arbitrary Python runner commands.

## 2026-06-24

Decision: Scope local repository preflight to configured backend-local roots.

Reason: Repository preflight is intentionally an operator diagnostic, but it accepts local paths. Limiting it to configured roots keeps demos and prepared workspaces usable without exposing broad filesystem inspection through the dashboard or API.

Impact: `POST /api/repository-preflight` rejects paths outside `patchpilot.repository-preflight.allowed-root-dirs` before adapter detection. Operators can see normalized allowed roots in `/api/configuration/summary` and the dashboard configuration panel.


## 2026-06-25

Decision: Treat AI infrastructure as an explicit target scope, but implement it incrementally behind the issue-to-PR workflow.

Reason: PatchPilot should demonstrate more than a thin model API wrapper. The resume value comes from model-provider boundaries, prompt governance, structured outputs, tool traces, cost and latency observability, safety gates, and evaluation evidence. Implementing all of that before the basic workflow is stable would create scope risk.

Impact: `docs/product/ai-infrastructure-target.md` defines the broader AI infrastructure direction. Current implementation work should still prioritize a reliable local self-hosted issue-to-PR path, then add AI infrastructure slices when they improve safety, debuggability, model quality, or demo evidence.
