# IDEA Local Run

Use this guide when starting PatchPilot directly from IntelliJ IDEA.

## Default Click-To-Run Behavior

Open `/Users/wangbingqin/Documents/agent` in IDEA and run:

```text
io.patchpilot.backend.PatchPilotApplication
```

When no Spring profile is selected, the app uses `application-default.properties`:

- HTTP port: `8080`
- Datasource auto-configuration: disabled
- Flyway: disabled

Use either the IDEA backend or the Docker backend at one time. Both use port `8080`, so they cannot run together.

## Verify IDEA Startup

After clicking Run in IDEA, check:

```bash
curl http://127.0.0.1:8080/health
curl http://127.0.0.1:8080/api/tasks
```

Expected responses are JSON API wrappers with `"success":true`.

## Running With Docker MySQL Later

For local development that needs MySQL, use the `local` profile and make sure a MySQL service is reachable at `localhost:3306`.

Current Docker Compose does not publish MySQL to the host, so the default IDEA run intentionally avoids requiring MySQL.

## Port Notes

- Docker backend: `http://127.0.0.1:8080`
- IDEA local backend: `http://127.0.0.1:8080`

Before clicking Run in IDEA, stop the Docker backend if it is running:

```bash
docker compose stop patchpilot-backend
```
