# 035 Code Simplification Lombok Cleanup

## Goal

Reduce low-value Java boilerplate without changing PatchPilot behavior or broadening the current architecture.

## Scope

- Replace entity getter/setter boilerplate with Lombok `@Data` where the class is a plain persistence entity.
- Replace pure dependency-injection constructors with Lombok `@RequiredArgsConstructor`.
- Use Spring's existing `StringUtils` for repeated blank-string checks and trimmed configuration values.
- Do not add Hutool for this pass; introduce it only when repeated string, collection, or file helpers justify a new dependency.
- Keep explicit constructors when they provide overloads, default helper instances, package-private test seams, or non-trivial initialization.
- Do not rename packages, change database mappings, alter API contracts, or modify task execution behavior.
- Keep `.idea/` and `.DS_Store` ignored and untracked.

## Tasks

- [x] Verify `.idea/` and `.DS_Store` are ignored and not tracked by Git.
- [x] Remove duplicated getters/setters from task persistence entities.
- [x] Add `@RequiredArgsConstructor` to simple DI classes across controllers, tools, workflows, runner guard, and task services.
- [x] Replace repeated string blank checks with Spring `StringUtils` where it improves readability.
- [x] Leave explicit constructors in classes where they preserve test seams or default object construction.
- [x] Run focused tests covering modified entities, services, controllers, tools, workflows, and runner guard.
- [x] Run full backend tests.

## Acceptance Criteria

- [x] Entity conversion and MyBatis service tests continue to pass.
- [x] Spring context tests continue to pass with Lombok-generated constructors.
- [x] String handling cleanup does not change validation, token, or webhook behavior.
- [x] Full backend test suite passes.
- [x] No `.idea/` or `.DS_Store` files are tracked.
