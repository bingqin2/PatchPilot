# 105 Supported Adapters API And Dashboard Panel

## Goal

Expose PatchPilot's currently supported language adapters through a backend read API and show the same matrix in the React dashboard. Operators should be able to confirm the supported repository shapes, fixed verification commands, and demo fixtures without reading source code or backend logs.

## Scope

- Add `GET /api/language-adapters`.
- Return language, build system, verification command, detection signals, demo fixture path, and support status.
- Add a dashboard panel that renders the supported adapter matrix.
- Keep adapter API failures local to the panel so core task, queue, and configuration data still loads.
- Update README, frontend design notes, and progress logs.

## Non-Goals

- Adding new language adapters.
- Editing adapter detection logic.
- Making browser-side configuration editable.

## Validation

- Backend service and controller tests for the supported adapter catalog.
- Frontend API, component, and dashboard tests.
- Full backend tests, frontend tests, production build, and whitespace check before handoff.
