# 208 - Demo Launch History Outcome Archive

## Goal

Turn single demo launch outcome reports into a browser-local outcome archive that operators can save, reopen, copy, and clear during repeated live-demo practice.

## Why This Matters

Plans 206 and 207 made post-launch evidence visible and copyable. Operators still lose context when they refresh the dashboard or run several demo attempts in sequence. A local outcome archive gives the demo workflow a small evidence memory: recent launch outcomes stay visible in the browser, keep their task and Pull Request links, and can be copied again for handoff without adding server state or another GitHub mutation path.

## Scope

- Add browser-local storage for recent demo launch outcome reports under `patchpilot.demoLaunchOutcomeArchive`.
- Keep the five most recent unique outcomes, deduplicating by task id when available and by repository, issue, and command otherwise.
- Add `Archive outcome` to tracked launch rows.
- Render a `Demo launch outcome archive` section inside the tracker panel.
- Let operators copy archived reports, open archived task links, open archived Pull Request links, and clear the local archive.
- Restore archived outcomes after dashboard reload from the same browser.
- Keep the feature read-only: no backend endpoint, task creation, queue mutation, GitHub mutation, or command-history mutation.

## Out of Scope

- Database-backed outcome archives.
- Posting archived reports to GitHub.
- Downloading archived reports as files.
- Cross-browser or multi-operator synchronization.

## Validation

- Component test proving an outcome can be archived, copied from the archive, and cleared from local storage.
- App-level test proving archived outcomes restore after reload and preserve task, Pull Request, and report-copy actions.
- Focused tracker/App regression tests for existing tracking and outcome-report behavior.
- Full frontend test/build verification.
- Backend regression check to keep the repository baseline clean even though this slice is frontend-only.
