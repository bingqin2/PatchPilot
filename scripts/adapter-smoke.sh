#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODE="${1:---detection}"

print_fixtures() {
  cat <<'TABLE'
PatchPilot adapter demo fixtures:

  java-maven     -> java/maven      -> mvn test
  java-gradle    -> java/gradle     -> gradle test
  node-npm       -> node/npm        -> npm test
  node-pnpm      -> node/pnpm       -> pnpm test
  node-yarn      -> node/yarn       -> yarn test
  python-pytest  -> python/pytest   -> python3 -m pytest
  python-poetry  -> python/poetry   -> poetry run pytest
  python-uv      -> python/uv       -> uv run pytest

Fixtures live in docs/demo-repositories.
This smoke script validates adapter detection only; it does not clone repositories,
call GitHub, call the model provider, run Docker, push branches, or create PRs.
TABLE
}

usage() {
  cat <<'USAGE'
Usage:
  scripts/adapter-smoke.sh [--detection|--backend|--help]

Options:
  --detection  Run only the adapter fixture detection smoke test. This is the default.
  --backend    Run a wider backend adapter test set after listing fixtures.
  --help       Show this help.
USAGE
}

case "$MODE" in
  --help|-h)
    usage
    exit 0
    ;;
  --detection)
    print_fixtures
    cd "$ROOT_DIR"
    mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests#should_detect_adapter_demo_fixtures test
    ;;
  --backend)
    print_fixtures
    cd "$ROOT_DIR"
    mvn -pl PatchPilot -Dtest=LanguageAdapterRegistryTests,JavaMavenLanguageAdapterTests,JavaGradleLanguageAdapterTests,NodeNpmLanguageAdapterTests,NodePnpmLanguageAdapterTests,NodeYarnLanguageAdapterTests,PythonPytestLanguageAdapterTests,PythonPoetryLanguageAdapterTests,PythonUvLanguageAdapterTests,CommandExecutionGuardTests test
    ;;
  *)
    usage >&2
    echo "Unknown option: $MODE" >&2
    exit 2
    ;;
esac
