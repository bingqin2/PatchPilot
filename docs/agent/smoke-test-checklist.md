# Smoke Test Checklist

Use this checklist before demoing PatchPilot or after changing backend workflow code.

## 1. Backend Test Suite

```bash
mvn -pl PatchPilot test
```

Expected: build success and zero test failures.

## 2. Docker Runtime

```bash
docker compose --env-file .env up --build
```

Expected:

- MySQL health check passes.
- `patchpilot-backend` starts on port `8080`.
- Flyway migrations complete without errors.

## 3. Local Health Checks

```bash
curl http://127.0.0.1:8080/health
curl http://127.0.0.1:8080/api/tasks
curl http://127.0.0.1:8080/api/task-queue/summary
```

Expected: JSON responses with `"success":true`.

## 4. Temporary Webhook URL

```bash
cloudflared tunnel --url http://127.0.0.1:8080
curl https://your-temp-url.trycloudflare.com/health
```

Expected: public health response matches local health response.

## 5. GitHub Webhook Settings

Configure the test repository webhook:

- Payload URL: `https://your-temp-url.trycloudflare.com/api/github/webhook`
- Content type: `application/json`
- Secret: value of `PATCHPILOT_GITHUB_WEBHOOK_SECRET`
- Events: `Issue comments`

Expected: GitHub webhook ping returns `200`.

## 6. Trigger A Deterministic Task

Comment on a test issue. Use an existing file in the target repository:

```text
/agent fix replace docs/demo.md PatchPilot smoke test
```

Expected webhook response body includes `TASK_CREATED`.

## 7. Inspect Task State

```bash
curl http://127.0.0.1:8080/api/tasks
curl http://127.0.0.1:8080/api/tasks/{taskId}
curl http://127.0.0.1:8080/api/tasks/{taskId}/timeline
curl http://127.0.0.1:8080/api/tasks/{taskId}/test-runs
curl http://127.0.0.1:8080/api/tasks/{taskId}/tool-calls
curl http://127.0.0.1:8080/api/tasks/{taskId}/model-calls
```

Expected:

- Task eventually reaches `COMPLETED`, or records a clear `FAILED` reason.
- Timeline records lifecycle events.
- Test runs, tool calls, and model calls are queryable.

## 8. Verify GitHub Result

Expected on success:

- A `patchpilot/{taskId}` branch exists.
- A Pull Request is open.
- The issue status comment links to the Pull Request.

Expected on failure:

- No success comment is posted.
- Task detail includes a failure reason.
- Timeline includes the failure event.
