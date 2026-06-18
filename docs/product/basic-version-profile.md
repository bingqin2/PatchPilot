# Basic Version Profile

## Purpose

The basic version exists to make PatchPilot runnable, testable, and ready for the first GitHub webhook feature. It is not the full agent product.

## Included In The Basic Version

- Root Maven aggregation.
- One Spring Boot backend module.
- Dockerfile for the backend.
- Docker Compose with MySQL and backend services.
- Spring Web dependency.
- Spring Validation dependency.
- Spring Boot Actuator dependency.
- MySQL driver dependency.
- Flyway dependency.
- MyBatis-Plus dependency.
- Local and docker Spring profiles.
- A small `/health` endpoint.
- Root-level Maven test and package commands.

## Not Included In The Basic Version

- GitHub App authentication.
- GitHub webhook signature verification.
- `/agent fix` parsing.
- Fix task persistence.
- Agent/model calls.
- Repository clone/edit/test automation.
- Pull Request creation.
- React UI implementation.
- Docker sandbox for executing user repositories.

## Foundation Commands

Run from repository root:

```bash
mvn test
mvn clean package
docker compose config
docker compose build patchpilot-backend
```

## Expected Runtime Shape

```text
React frontend later
      |
PatchPilot backend
      |
MySQL
```

The backend starts as one deployable Spring Boot service. API/worker split should happen only after the issue-to-PR workflow is proven.
