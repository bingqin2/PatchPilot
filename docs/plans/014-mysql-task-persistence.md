# MySQL Task Persistence Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:test-driven-development to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Persist PatchPilot fix tasks in MySQL so task history survives backend restarts.

**Architecture:** Add a Flyway migration for `fix_task`, then introduce a MyBatis-Plus entity, mapper, and MySQL-backed `FixTaskService` implementation. Keep `InMemoryFixTaskService` available for the default no-database profile, and make the MySQL service active only when a datasource profile such as `local` or `docker` is enabled.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus 3.5.14, Flyway, MySQL 8.4, JUnit 5, AssertJ.

## Global Constraints

- Do not change webhook request/response contracts.
- Do not add model provider calls.
- Do not require MySQL for the default profile or current Spring context smoke tests.
- Do not persist secrets or raw GitHub tokens.
- Database schema names use snake_case.
- Java domain APIs keep the existing `FixTaskService` and `FixTaskVo` shape for this phase.
- Persistence must preserve duplicate delivery detection across restarts when MySQL is enabled.

---

## Target Files

Create:

- `PatchPilot/src/main/resources/db/migration/V1__create_fix_task.sql`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/entity/FixTaskEntity.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/domain/bo/FixTaskCreationResult.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/mapper/FixTaskMapper.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/convert/FixTaskConvert.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/MyBatisFixTaskService.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/convert/FixTaskConvertTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/MyBatisFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/mapper/FixTaskMigrationTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookServiceTests.java`

Modify:

- `PatchPilot/src/main/java/io/patchpilot/backend/github/webhook/GitHubWebhookService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/FixTaskService.java`
- `PatchPilot/src/main/java/io/patchpilot/backend/task/service/impl/InMemoryFixTaskService.java`
- `PatchPilot/src/main/resources/application-local.properties`
- `PatchPilot/src/main/resources/application-docker.properties`
- `PatchPilot/src/test/java/io/patchpilot/backend/task/service/InMemoryFixTaskServiceTests.java`
- `PatchPilot/src/test/java/io/patchpilot/backend/github/webhook/GitHubWebhookControllerTests.java`
- `docs/progress/execution-log.md`
- `docs/plans/014-mysql-task-persistence.md`

## Task 1: Add Fix Task Migration

**Interfaces:**

- Flyway migration path: `PatchPilot/src/main/resources/db/migration/V1__create_fix_task.sql`.
- Table name: `fix_task`.
- Columns:
  - `id varchar(36) primary key`
  - `repository_owner varchar(255) not null`
  - `repository_name varchar(255) not null`
  - `issue_number bigint not null`
  - `installation_id bigint not null`
  - `trigger_user varchar(255) not null`
  - `trigger_comment text not null`
  - `delivery_id varchar(255) not null`
  - `comment_id bigint not null`
  - `status varchar(64) not null`
  - `failure_reason text null`
  - `created_at timestamp(6) not null`
  - unique key `uk_fix_task_delivery_id(delivery_id)`.

- [x] Write a failing migration test that reads `V1__create_fix_task.sql` and asserts it creates `fix_task`, includes the required columns, and defines `uk_fix_task_delivery_id`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests test` and verify it fails because the migration file does not exist.
- [x] Add `V1__create_fix_task.sql`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests test` and verify it passes.

## Task 2: Add Entity, Mapper, And Converter

**Interfaces:**

- `FixTaskEntity` maps to `fix_task`.
- `FixTaskMapper extends BaseMapper<FixTaskEntity>`.
- `FixTaskConvert#toVo(FixTaskEntity entity)` returns `FixTaskVo`.
- `FixTaskConvert#newEntity(String id, CreateFixTaskCommand command, Instant createdAt)` returns a `FixTaskEntity` with status `PENDING`.
- `FixTaskConvert#replaceStatus(FixTaskEntity current, FixTaskStatus status, String failureReason)` returns an entity preserving immutable task fields.

- [x] Write a failing converter test for `newEntity(...)` and `toVo(...)`.
- [x] Write a failing converter test that `replaceStatus(...)` preserves task fields while changing status and failure reason.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test` and verify it fails because converter/entity classes do not exist.
- [x] Add `FixTaskEntity`, `FixTaskMapper`, and `FixTaskConvert`.
- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskConvertTests test` and verify it passes.

## Task 3: Add MySQL-Backed FixTaskService

**Interfaces:**

- `MyBatisFixTaskService implements FixTaskService`.
- Constructor dependencies: `FixTaskMapper`.
- Annotate with `@Service` and `@Profile({"local", "docker"})`.
- `createFixTask(...)` inserts a new `PENDING` task unless delivery id already exists.
- Duplicate delivery ids return the existing task instead of inserting a second row.
- `markRunning`, `markRunningTests`, `markCompleted`, and `markFailed` update status through one private `replaceStatus(...)`.
- `listTasks()` returns newest first by `created_at desc`.
- `findTask(id)` returns `Optional<FixTaskVo>`.
- Missing task status updates throw `IllegalArgumentException("Task not found: <id>")`.

- [x] Write a failing service test using a fake mapper that verifies task creation inserts `PENDING`.
- [x] Write a failing service test that duplicate delivery id returns the existing task and does not insert twice.
- [x] Write a failing service test that status updates write the new status and failure reason.
- [x] Write a failing service test that list order is newest first.
- [x] Run `mvn -pl PatchPilot -Dtest=MyBatisFixTaskServiceTests test` and verify it fails because `MyBatisFixTaskService` does not exist.
- [x] Add `MyBatisFixTaskService`.
- [x] Run `mvn -pl PatchPilot -Dtest=MyBatisFixTaskServiceTests test` and verify it passes.

## Task 4: Keep Default Profile In-Memory And Enable Flyway For DB Profiles

**Interfaces:**

- `InMemoryFixTaskService` remains the default no-database service.
- Annotate `InMemoryFixTaskService` with `@Profile("default")`.
- `application-local.properties` sets `spring.flyway.enabled=true`.
- `application-docker.properties` sets `spring.flyway.enabled=true`.
- Existing Spring Boot default profile tests still start without a datasource.
- Existing webhook controller tests keep using default profile and in-memory service.

- [x] Update `InMemoryFixTaskServiceTests` if needed so direct construction still works.
- [x] Update local and docker profile Flyway settings.
- [x] Run `mvn -pl PatchPilot -Dtest=PatchPilotApplicationTests,GitHubWebhookControllerTests,InMemoryFixTaskServiceTests test` and verify it passes.

## Task 5: Validate And Document

- [x] Run `mvn -pl PatchPilot -Dtest=FixTaskMigrationTests,FixTaskConvertTests,MyBatisFixTaskServiceTests,InMemoryFixTaskServiceTests,PatchPilotApplicationTests,GitHubWebhookControllerTests test`.
- [x] Run `mvn test`.
- [x] Run `mvn clean package`.
- [x] Record validation evidence in `docs/progress/execution-log.md`.
- [x] Mark this plan complete.

## Acceptance Checklist

- [x] MySQL profile has a `fix_task` table migration.
- [x] MySQL-backed service persists tasks through `FixTaskMapper`.
- [x] Duplicate webhook delivery ids are deduplicated by persistent data.
- [x] Default profile still starts without MySQL.
- [x] Local and Docker profiles enable Flyway migrations.
- [x] Existing task APIs keep the same JSON shape.
- [x] Full Maven verification passes.
