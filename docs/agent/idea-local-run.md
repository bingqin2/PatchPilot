# IDEA Local Run

Use this guide when starting PatchPilot directly from IntelliJ IDEA.

## Click-To-Run Behavior

Open `/Users/wangbingqin/Documents/agent` in IDEA and run:

```text
io.patchpilot.backend.PatchPilotApplication
```

The shared run configuration `.run/PatchPilotApplication.run.xml` uses the `idea` profile:

- HTTP port: `18080`
- Datasource: Docker MySQL exposed on `127.0.0.1:3307`
- Env file: `/Users/wangbingqin/Documents/agent/.env`

This mode is for running the backend from IDEA while using Docker only for MySQL. The full Docker backend still uses the `docker` profile and port `8080`.

## Docker Startup Behavior

The `idea` profile uses Spring Boot Docker Compose support with `/Users/wangbingqin/Documents/agent/docker-compose.idea.yml`. When you click Run:

- Spring Boot calls `docker compose up` for the IDEA-only Compose file.
- Only the MySQL service is started or reused.
- The backend Docker container is not started; the backend runs from IDEA.
- MySQL is left running after the JVM exits, so the next startup is faster.

Docker Desktop still needs to be running. You do not need to run `docker compose up` manually for normal IDEA startup.

## Verify IDEA Startup

After clicking Run in IDEA, check:

```bash
curl http://127.0.0.1:18080/health
curl http://127.0.0.1:18080/api/tasks
```

Expected responses are JSON API wrappers with `"success":true`.

## Port Notes

- Docker backend: `http://127.0.0.1:8080`
- IDEA backend: `http://127.0.0.1:18080`
- Docker MySQL from host: `127.0.0.1:3307`

You can run the Docker backend and the IDEA backend at the same time because they use different HTTP ports. If port `3307` is already occupied, change both `docker-compose.yml` and `application-idea.properties` to the same free port.
