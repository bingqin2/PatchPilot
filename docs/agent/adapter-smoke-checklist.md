# Adapter Smoke Checklist

Use this checklist when you want a quick local demonstration that PatchPilot's supported repository shapes still map to the expected language adapters and verification commands.

## What This Checks

- The demo fixtures under `docs/demo-repositories/` are present.
- `LanguageAdapterRegistry` selects the expected adapter for each fixture.
- Each fixture maps to the expected `language`, `buildSystem`, and allowlisted verification command.
- The wider backend mode also checks individual adapter tests and command-guard rules.

## What This Does Not Check

- It does not call GitHub.
- It does not require a GitHub token.
- It does not call the model provider.
- It does not start Docker Compose.
- It does not clone external repositories, push branches, create commits, or open Pull Requests.
- It does not run every fixture's real Maven, Gradle, npm, pnpm, yarn, pytest, Poetry, or uv command.

## Commands

Run the fast adapter detection smoke:

```bash
cd /Users/wangbingqin/Documents/agent
scripts/adapter-smoke.sh
```

Run the wider backend adapter smoke:

```bash
cd /Users/wangbingqin/Documents/agent
scripts/adapter-smoke.sh --backend
```

Show usage:

```bash
scripts/adapter-smoke.sh --help
```

## Expected Result

The script prints the supported fixture matrix and Maven reports `BUILD SUCCESS`. A passing smoke means adapter detection has not drifted from the documented supported repository shapes.

If the smoke fails, check:

- The fixture still has the manifest or lockfile required by its adapter.
- The expected verification command is still allowlisted.
- Adapter order still prefers specific package managers before broader adapters, such as pnpm before npm and Poetry before plain pytest.
