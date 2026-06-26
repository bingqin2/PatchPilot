# 209 - Demo Launch Outcome Session Integration

## Goal

Include browser-local demo launch outcome archive evidence in copied, downloaded, and archived demo session reports.

## Why This Matters

Plan 208 made post-launch outcome reports recoverable after refresh, but those reports still lived beside the session report instead of inside it. A live-demo handoff should show the prepared command and the observed outcome together: webhook/task/PR evidence must travel with the final session report so reviewers can reconstruct what happened without reopening multiple dashboard panels.

## Scope

- Add an `archivedLaunchOutcomes` field to demo session report requests.
- Render an `Archived Launch Outcomes` section in backend Markdown session reports.
- Keep archived outcome context bounded to five entries and summarize long embedded outcome reports.
- Load `patchpilot.demoLaunchOutcomeArchive` in the dashboard coordinator and pass it to the session snapshot panel.
- Show archived launch outcomes in the session snapshot panel.
- Include archived outcomes when copying, downloading, or archiving demo session reports.
- Refresh the session report context when the demo launch tracker archives or clears outcomes.
- Keep the feature read-only for GitHub/task execution: no new task creation, queue mutation, model call, Git command, or GitHub mutation path.

## Out of Scope

- Persisting launch outcome archives on the backend.
- Editing or deleting backend session archive records.
- Posting outcome reports to GitHub automatically.

## Validation

- RED frontend tests failed because `DemoSessionSnapshotPanel` sent only prepared commands to report actions.
- RED backend tests failed because `DemoArchivedLaunchOutcomeRequestDto` and the expanded report request did not exist.
- Focused frontend tests cover API request bodies, session snapshot report actions, tracker archive behavior, and App-level localStorage handoff.
- Focused backend tests cover DTO binding, Markdown rendering, and archive service behavior.
- Full frontend tests, frontend production build, backend tests, and diff formatting checks passed before handoff.
