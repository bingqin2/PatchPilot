# Unsupported Repository Preflight

## Goal

Fail unsupported repositories before patch generation, test execution, Git mutation, or Pull Request creation. This makes the current Java/Maven-only boundary explicit and creates the adapter registry needed for future Gradle, Node.js, and Python support.

## Scope

- Add a `LanguageAdapterRegistry` that selects the first supported language adapter.
- Run repository language detection immediately after workspace preparation.
- Record the language preflight as an audited tool call.
- Stop execution with a clear unsupported-repository failure reason when no adapter supports the checked-out repository.
- Keep the existing Java/Maven adapter as the only supported execution adapter for now.
- Update executor tests, architecture docs, product scope, and operator docs.

## Non-Goals

- Add Gradle, Node.js, Python, or Docker sandbox execution in this step.
- Replace `MavenTestRunner` with a fully generic runner yet.
- Infer language support from GitHub repository metadata before cloning.

## Validation Plan

- Unit test adapter registry selection and unsupported fallback behavior.
- Verify the workspace executor fails unsupported repositories before patch workflow, diff, tests, commit, push, and PR creation.
- Verify existing Java/Maven task execution, Maven failure, Git failure, and cancellation paths still pass with the new preflight step.
- Run full backend tests before merging.
