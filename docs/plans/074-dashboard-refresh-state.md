# 074 Dashboard Refresh State

## Goal

Make dashboard refresh progress visible so operators know when top-level data is still loading.

## Scope

- Disable the top-level refresh button while dashboard refresh is in flight.
- Change the refresh button label and accessible name while refreshing.
- Show a compact `Dashboard refreshing` status region.
- Hide the status region once refresh completes.

## Non-Goals

- Add automatic polling.
- Add real-time push updates.
- Change backend APIs or queue behavior.

## Validation

- Add App coverage for the in-flight refresh state and restored idle state.
- Run the full frontend test suite.
- Run the frontend production build.
