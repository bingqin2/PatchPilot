# Task Evidence Package Archive

## Goal

Let operators download and archive a point-in-time Markdown evidence package for one task, then reopen recent archived task evidence from the dashboard after the live task state changes.

## Why This Matters

PatchPilot can already show rich task detail and copy a task report, but demos and post-run reviews need a durable artifact per issue-to-PR attempt. A task evidence package gives one stable record for completed, failed, cancelled, pending-review, and unsupported tasks without requiring operators to reconstruct evidence from live panels, GitHub comments, or terminal output.

## Scope

- Add backend task evidence package archive storage with default in-memory and MySQL-backed repositories.
- Add APIs:
  - `GET /api/tasks/{taskId}/report/download`
  - `POST /api/tasks/{taskId}/evidence-packages`
  - `GET /api/tasks/{taskId}/evidence-packages`
  - `GET /api/tasks/evidence-packages/{archiveId}/report/download`
- Record protected admin audit evidence when an operator archives a task evidence package.
- Add frontend API helpers, typed contracts, and task detail controls for download/archive/list/download archived reports.
- Update docs and progress log.

## Side-Effect Contract

- Current report download is read-only over the existing task detail read model.
- Archive creation stores PatchPilot-local evidence and records protected admin audit evidence.
- Archive creation must not create tasks, call the model, run verification commands, mutate Git, push, open Pull Requests, or write GitHub comments.

## Acceptance Criteria

- Downloading a current task report returns a Markdown attachment named from the task id.
- Archiving a task creates a record with task id, repository, issue number, status, Pull Request URL, archived time, summary, and exact Markdown report.
- Listing archives is scoped to the selected task and ordered newest first.
- Downloading an archived package returns the archived report, not a freshly regenerated report.
- Dashboard task detail exposes `Download report`, `Archive evidence`, recent archive rows, and archived report download actions.
- Tests cover backend current download, archive/list/download, admin audit, frontend API helpers, and dashboard rendering/actions.
