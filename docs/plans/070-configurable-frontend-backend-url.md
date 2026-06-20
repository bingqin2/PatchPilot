# Configurable Frontend Backend URL

## Goal

Let the React dashboard proxy API requests to the correct backend port without editing `frontend/vite.config.ts`.

## Scope

- Keep Vite development server on `127.0.0.1:5173`.
- Read the backend proxy target from environment variables.
- Read the repository root `.env` when the variable is not present in the shell environment.
- Default to Docker Compose backend URL `http://127.0.0.1:8080`.
- Support IDEA local backend URL such as `http://127.0.0.1:18080`.
- Do not change production API paths, backend ports, or Docker Compose networking.

## Configuration

Set one of these variables in the shell environment or the repository root `.env` before starting `npm run dev`:

- `PATCHPILOT_FRONTEND_BACKEND_URL=http://127.0.0.1:18080`
- `VITE_PATCHPILOT_BACKEND_URL=http://127.0.0.1:18080`

The non-`VITE_` name is intended for local dev proxy configuration. The `VITE_` alias is accepted for operators who prefer Vite-style names.

## Testing

- Unit test the Vite proxy target helper for default, IDEA, and Vite-prefixed values.
- Run the full frontend test suite and production build.

## Acceptance Criteria

- Default frontend dev proxy still targets `8080`.
- IDEA users can target `18080` through `.env` or shell environment.
- The frontend no longer requires manual edits to `vite.config.ts` when backend ports differ.
