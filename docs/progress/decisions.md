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
