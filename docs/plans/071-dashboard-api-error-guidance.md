# Dashboard API Error Guidance

## Goal

Show actionable dashboard errors when the backend or Vite proxy returns an empty, non-JSON, or unreachable response.

## Scope

- Improve frontend API helper error handling only.
- Keep successful API response parsing unchanged.
- Preserve backend-provided JSON error messages when available.
- Do not change backend endpoints, Vite proxy behavior, or dashboard layout.

## Error Handling Design

The API helper should return a consistent operator-facing message when requests cannot produce the expected PatchPilot JSON envelope:

```text
Backend request failed. Check that PatchPilot backend is running and the frontend proxy target is correct.
```

This covers:

- Backend process not running.
- Vite proxy target pointing at the wrong port.
- Empty proxy responses.
- HTML or other non-JSON error pages.

## Testing

- API unit test for an empty response that throws during JSON parsing.
- API unit test for a non-JSON response that throws during JSON parsing.
- Existing API helper tests must continue to verify request paths and successful payload parsing.

## Acceptance Criteria

- Operators no longer see raw `Unexpected end of JSON input` in the dashboard.
- Backend JSON error messages still surface when the backend returns a valid error envelope.
- Full frontend tests and production build pass.
