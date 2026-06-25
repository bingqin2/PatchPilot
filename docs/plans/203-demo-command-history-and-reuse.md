# 203 - Demo Command History and Reuse

## Goal

Make the demo launch command composer usable during repeated live-demo practice by keeping recent generated `/agent fix` commands available for copy, composer reuse, and launch-preflight reuse.

## Scope

- Store successful demo launch command compositions in browser `localStorage`.
- Keep only the five most recent unique commands.
- Restore saved commands when the dashboard reloads in the same browser.
- Let operators copy a saved command, refill the composer from it, or apply its returned preflight input to launch preflight.
- Keep command history local to the browser and read-only with respect to backend tasks, GitHub, queues, and demo session archives.

## Non-Goals

- Do not create tasks, GitHub comments, webhook diagnostics, queue items, or session archive records from command history.
- Do not expose command history through a backend API in this slice.
- Do not write command history into demo session snapshots or session reports in this slice.

## Validation

- Component tests should cover save, restore, clear, copy, composer refill, preflight apply, and max-history behavior.
- App integration tests should cover history persistence across dashboard remounts.
- Frontend build should continue to pass.

## Follow-Up

Plan 204 should move selected command-history evidence into the demo session snapshot/report so a copied or archived demo report can show which controlled launch commands were prepared during a session.
