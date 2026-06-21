# 075 Dashboard Last Refresh Time

## Goal

Show when dashboard top-level data last refreshed successfully.

## Scope

- Track the timestamp after a successful top-level dashboard refresh.
- Display `Last refreshed` under the dashboard title.
- Preserve a machine-readable `datetime` value for testing and accessibility.

## Non-Goals

- Add automatic polling.
- Display per-panel refresh times.
- Change backend APIs.

## Validation

- Add App coverage for the visible and machine-readable last refresh timestamp.
- Run the full frontend test suite.
- Run the frontend production build.
