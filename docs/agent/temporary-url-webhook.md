# Using a Temporary URL for GitHub Webhooks

This guide explains how to expose the local PatchPilot backend to GitHub with a temporary Cloudflare Tunnel URL.

## When to Use This

Use a temporary URL for local webhook testing. The URL is not stable and only works while the `cloudflared` process is running. Do not rely on it for production.

## Start the Backend

From the repository root:

```bash
cd /Users/wangbingqin/Documents/agent
cp .env.example .env
docker compose --env-file .env up --build
```

Set `PATCHPILOT_GITHUB_WEBHOOK_SECRET` in `.env` before starting. Keep this terminal open and wait until Spring Boot reports that `PatchPilotApplication` has started.

Verify the local backend:

```bash
curl http://127.0.0.1:8080/health
```

Expected: a JSON response with `"status":"UP"`.

## Start Cloudflare Tunnel

Open a second terminal:

```bash
cloudflared tunnel --url http://127.0.0.1:8080
```

Use `127.0.0.1` instead of `localhost` to avoid IPv6 `[::1]` connection issues. Copy the generated `https://*.trycloudflare.com` URL.

Verify the public URL:

```bash
curl https://your-temp-url.trycloudflare.com/health
```

Expected: the same health JSON as the local endpoint.

## Configure GitHub Webhook

In the GitHub repository, open `Settings` -> `Webhooks` -> `Add webhook`.

Use:

- Payload URL: `https://your-temp-url.trycloudflare.com/api/github/webhook`
- Content type: `application/json`
- Secret: same value as `PATCHPILOT_GITHUB_WEBHOOK_SECRET` in `.env`
- Events: select `Issue comments`

Save the webhook.

## Test the Webhook

Create or open a GitHub issue and add this comment:

```text
/agent fix
```

Check `Settings` -> `Webhooks` -> `Recent Deliveries`.

Expected results:

- `200` with `TASK_CREATED`: webhook reached PatchPilot and created a task.
- `200` with `IGNORED`: webhook reached PatchPilot, but event or comment content did not match.
- `401`: GitHub secret does not match `PATCHPILOT_GITHUB_WEBHOOK_SECRET`.
- `502` or `503`: the backend or `cloudflared` process is not running.

## Important Notes

- The temporary URL changes when `cloudflared` restarts.
- Update the GitHub webhook Payload URL after every tunnel restart.
- Keep both terminals running during testing.
- The Docker profile stores task records in MySQL; the default IDEA profile is intended for lightweight local startup without MySQL.
