# Basic Version Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build the foundation version of PatchPilot: a runnable Spring Boot backend with health checks, MySQL-ready configuration, root Maven aggregation, Docker packaging, and enough task API scaffolding for later GitHub webhook work.

**Architecture:** Keep one Spring Boot backend module under `PatchPilot/` and use the root Maven project as the parent aggregator. The basic version adds a small web surface, local/docker profiles, MySQL/Flyway/MyBatis-Plus dependencies, a minimal domain package structure, and a React frontend placeholder only at the documentation/scaffold level. GitHub, agent execution, repository cloning, and PR creation remain later phases.

**Tech Stack:** Java 17, Spring Boot 3.5.x, Maven multi-module parent, Spring Web, Spring Validation, Spring Boot Actuator, MySQL Connector/J, Flyway, MyBatis-Plus, JUnit 5, Mockito, Docker, Docker Compose, React + Vite + TypeScript planned for the frontend.

---

## Purpose / Big Picture

This plan turns the generated PatchPilot backend into a real project foundation. After this plan, a developer should be able to run all backend tests from the repository root, package the backend from the root, start MySQL with Docker Compose, and see a simple health surface from the backend.

This plan intentionally does not implement GitHub webhooks, LLM calls, repository cloning, patch generation, or PR creation. Those features depend on a stable backend foundation and should be implemented in later plans.

## Scope

In scope:

- Root Maven parent and backend module build consistency.
- Backend web/validation/actuator dependencies.
- MySQL/Flyway/MyBatis-Plus baseline dependencies.
- Local and docker Spring profiles.
- A small custom health endpoint in addition to Actuator.
- Basic package structure aligned with `docs/product/backend-code-standard.md`.
- Dockerfile and docker-compose verification.
- Documentation updates for foundation validation.

Out of scope:

- GitHub App authentication.
- GitHub webhook processing.
- Agent/model provider integration.
- Workspace clone/edit/test tools.
- React UI implementation.
- MySQL schema for real fix tasks.

## Current Project Context

Repository root:

```text
/Users/wangbingqin/Documents/agent
```

Current build files:

```text
pom.xml
PatchPilot/pom.xml
PatchPilot/Dockerfile
docker-compose.yml
```

Current backend files:

```text
PatchPilot/src/main/java/io/patchpilot/backend/PatchPilotApplication.java
PatchPilot/src/main/resources/application.properties
PatchPilot/src/test/java/io/patchpilot/backend/PatchPilotApplicationTests.java
```

Current docs:

```text
docs/product/spec.md
docs/product/architecture.md
docs/product/backend-code-standard.md
docs/product/roadmap.md
docs/progress/decisions.md
docs/progress/execution-log.md
```

## Target File Structure

After this plan, the foundation should include:

```text
pom.xml
PatchPilot/pom.xml
PatchPilot/Dockerfile
docker-compose.yml

PatchPilot/src/main/java/io/patchpilot/backend/PatchPilotApplication.java
PatchPilot/src/main/java/io/patchpilot/backend/observability/HealthController.java
PatchPilot/src/main/java/io/patchpilot/backend/common/response/ApiResponse.java
PatchPilot/src/main/resources/application.properties
PatchPilot/src/main/resources/application-local.properties
PatchPilot/src/main/resources/application-docker.properties
PatchPilot/src/test/java/io/patchpilot/backend/PatchPilotApplicationTests.java
PatchPilot/src/test/java/io/patchpilot/backend/observability/HealthControllerTests.java

docs/plans/001-basic-version-implementation.md
docs/progress/execution-log.md
```

## Implementation Tasks

### Task 1: Verify root Maven aggregation

**Files:**

- Inspect: `pom.xml`
- Inspect: `PatchPilot/pom.xml`

- [x] **Step 1: Confirm the root parent is an aggregator**

Expected root `pom.xml` has:

```xml
<packaging>pom</packaging>
<modules>
    <module>PatchPilot</module>
</modules>
```

- [x] **Step 2: Confirm the backend module inherits from the root parent**

Expected `PatchPilot/pom.xml` has:

```xml
<parent>
    <groupId>io.patchpilot</groupId>
    <artifactId>patchpilot-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

- [x] **Step 3: Run root Maven test**

Run from `/Users/wangbingqin/Documents/agent`:

```bash
mvn test
```

Expected result:

```text
Reactor Summary for PatchPilot Parent ... SUCCESS
PatchPilot Backend ... SUCCESS
BUILD SUCCESS
```

### Task 2: Add backend runtime dependencies

**Files:**

- Modify: `PatchPilot/pom.xml`

- [x] **Step 1: Add the web, validation, actuator, MySQL, Flyway, and MyBatis-Plus dependencies**

Update `PatchPilot/pom.xml` dependencies to include:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>

<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.14</version>
</dependency>
```

Keep `spring-boot-starter-test` for tests.

- [x] **Step 2: Run Maven dependency resolution through tests**

Run:

```bash
mvn test
```

Expected result:

```text
BUILD SUCCESS
```

If dependency resolution fails because a version changed, check the current MyBatis-Plus Spring Boot 3 starter version and record the decision in `docs/progress/decisions.md` before changing it.

### Task 3: Add application profiles

**Files:**

- Modify: `PatchPilot/src/main/resources/application.properties`
- Create: `PatchPilot/src/main/resources/application-local.properties`
- Create: `PatchPilot/src/main/resources/application-docker.properties`

- [x] **Step 1: Update base application properties**

Use:

```properties
spring.application.name=patchpilot-backend
server.port=8080
management.endpoints.web.exposure.include=health,info
management.endpoint.health.probes.enabled=true
```

- [x] **Step 2: Add local profile**

Create `application-local.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/patchpilot?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=patchpilot
spring.datasource.password=patchpilot
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.flyway.enabled=false
mybatis-plus.configuration.map-underscore-to-camel-case=true
```

Flyway is disabled for the foundation version because no schema migration exists yet. The first persistence plan should enable Flyway and add `V1__init_patchpilot.sql`.

- [x] **Step 3: Add docker profile**

Create `application-docker.properties`:

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/patchpilot?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=patchpilot
spring.datasource.password=patchpilot
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.flyway.enabled=false
mybatis-plus.configuration.map-underscore-to-camel-case=true
```

- [x] **Step 4: Run tests**

Run:

```bash
mvn test
```

Expected result:

```text
BUILD SUCCESS
```

### Task 4: Add a small health API

**Files:**

- Create: `PatchPilot/src/main/java/io/patchpilot/backend/common/response/ApiResponse.java`
- Create: `PatchPilot/src/main/java/io/patchpilot/backend/observability/HealthController.java`
- Create: `PatchPilot/src/test/java/io/patchpilot/backend/observability/HealthControllerTests.java`

- [x] **Step 1: Write the failing health controller test**

Create `HealthControllerTests.java`:

```java
package io.patchpilot.backend.observability;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
class HealthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_return_ok_health_response() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("patchpilot-backend"));
    }
}
```

- [x] **Step 2: Run the health test to verify it fails**

Run:

```bash
mvn -pl PatchPilot -Dtest=HealthControllerTests test
```

Expected result:

```text
Compilation failure or test failure because HealthController does not exist.
```

- [x] **Step 3: Add the API response record**

Create `ApiResponse.java`:

```java
package io.patchpilot.backend.common.response;

public record ApiResponse<T>(boolean success, T data, String message) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }
}
```

- [x] **Step 4: Add HealthController**

Create `HealthController.java`:

```java
package io.patchpilot.backend.observability;

import io.patchpilot.backend.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<HealthStatus> health() {
        return ApiResponse.ok(new HealthStatus("UP", "patchpilot-backend", Instant.now()));
    }

    public record HealthStatus(String status, String service, Instant timestamp) {
    }
}
```

- [x] **Step 5: Run the health test to verify it passes**

Run:

```bash
mvn -pl PatchPilot -Dtest=HealthControllerTests test
```

Expected result:

```text
Tests run: 1, Failures: 0, Errors: 0
BUILD SUCCESS
```

- [x] **Step 6: Run all tests from root**

Run:

```bash
mvn test
```

Expected result:

```text
BUILD SUCCESS
```

### Task 5: Validate Docker and compose setup

**Files:**

- Inspect: `PatchPilot/Dockerfile`
- Inspect: `docker-compose.yml`

- [x] **Step 1: Validate compose syntax**

Run:

```bash
docker compose config
```

Expected result:

```text
services:
  mysql:
  patchpilot-backend:
```

- [x] **Step 2: Build the backend jar from root**

Run:

```bash
mvn clean package
```

Expected result:

```text
PatchPilot Backend ... SUCCESS
BUILD SUCCESS
```

- [x] **Step 3: Build the backend Docker image**

Run:

```bash
docker compose build patchpilot-backend
```

Expected result:

```text
patchpilot-backend:local  Built
```

If Docker fails while resolving base images through a local mirror, record the mirror/DNS problem in `docs/progress/execution-log.md`. Do not change Dockerfile image names unless the mirror problem is resolved or a new base image decision is recorded.

Result: attempted on 2026-06-18. The first run failed while resolving `maven:3.9-eclipse-temurin-17` and `eclipse-temurin:17-jre` through `https://docker.mirrors.ustc.edu.cn` with EOF. After the base images were pulled successfully, rerunning `docker compose build patchpilot-backend` passed and built `patchpilot-backend:local`.

### Task 6: Update documentation after implementation

**Files:**

- Modify: `docs/progress/execution-log.md`
- Modify: `docs/plans/000-project-foundation.md`

- [x] **Step 1: Record validation commands**

Append an entry to `docs/progress/execution-log.md`:

```markdown
## 2026-06-18

Implemented the basic backend foundation.

Validation:

- `mvn test` from repository root: passed.
- `mvn clean package` from repository root: passed.
- `docker compose config`: passed.
- `docker compose build patchpilot-backend`: record the actual result.
```

- [x] **Step 2: Update foundation plan progress**

In `docs/plans/000-project-foundation.md`, mark completed items only after they are actually implemented and validated.

Do not mark GitHub, agent, database schema, or React UI items as complete in this basic version.

## Acceptance Checklist

The foundation version is accepted when:

- [x] Root `mvn test` passes.
- [x] Root `mvn clean package` passes.
- [x] `docker compose config` passes.
- [x] `docker compose build patchpilot-backend` passes.
- [x] `/health` returns a structured response in tests.
- [x] Spring Boot Actuator is available for standard health checks.
- [x] MySQL profile configuration exists but schema migration is deferred to the persistence phase.
- [x] `docs/progress/execution-log.md` records validation evidence.

## Notes For Later Plans

The next plan should be `docs/plans/001-github-webhook-mvp.md`. It should start from this foundation and implement only:

- GitHub webhook endpoint.
- Signature verification.
- `issue_comment.created` parsing.
- `/agent fix` detection.
- Fix task creation with a minimal durable or in-memory repository, depending on whether the persistence phase has landed.
