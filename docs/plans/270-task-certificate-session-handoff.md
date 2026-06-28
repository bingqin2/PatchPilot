# 270 Task Certificate Session Handoff

## Goal

Carry the latest task evidence acceptance certificate into the demo session handoff path, not only the top-level evidence bundle. A reviewer should be able to open the session report, handoff package, or dashboard session panel and see whether the specific task evidence certificate is archived, certified, linked to a task, and tied to a Pull Request.

## Scope

- Add task evidence certificate proof to demo session report Markdown.
- Add a task evidence certificate check to structured handoff readiness.
- Include the same check in handoff package Markdown and embedded session reports.
- Render task certificate status, archive id, target task, Pull Request, and next action in the dashboard demo session panel.
- Preserve the existing read-only contract: no task creation, model calls, Git mutation, GitHub writes, delivery sends, or archive creation from these read paths.

## Out of Scope

- Changing certificate generation or archive rules.
- Creating new persistence tables.
- Sending handoff evidence externally.
- Replacing the dedicated task evidence archive review panel.

## Validation

- RED backend tests first for missing task certificate Markdown and handoff readiness check.
- RED frontend test first for the missing session panel task certificate facts.
- Focused backend and frontend tests after implementation.
- Full backend and frontend regression, production build, and whitespace check before merge.
