# 100 Adapter Filtering And Metrics

## Goal

Turn adapter metadata into an operational filter. Operators should be able to narrow task history, status counts, and dashboard metrics by selected `language` and `buildSystem` after PatchPilot starts supporting multiple repository ecosystems.

## Scope

- Add optional `language` and `buildSystem` filters to task list queries.
- Apply adapter filters in both in-memory and MyBatis-backed task services.
- Accept adapter filters in `GET /api/tasks` and `GET /api/tasks/status-counts`.
- Accept the same investigation scope in metrics endpoints so counts, failure causes, model usage, and latency match the filtered task list.
- Add dashboard language and build-system filters with URL persistence, clear-filter support, and load-more propagation.
- Update README, frontend design notes, and progress logs.

## Out Of Scope

- Adding new language adapters.
- Changing adapter selection order.
- Adding charts or historical trend visualizations.

## Acceptance Checks

- `GET /api/tasks?language=node&buildSystem=npm` returns only matching tasks.
- `GET /api/tasks/status-counts?language=node&buildSystem=npm` counts only the adapter scope while ignoring active status.
- `GET /api/tasks/metrics/summary?language=node&buildSystem=npm` summarizes only matching tasks.
- Dashboard adapter filters are restored from the URL and included in task, count, metrics, and pagination requests.
- `Clear filters` removes adapter filters while preserving selected task route, hash, unrelated query parameters, and active sort.
