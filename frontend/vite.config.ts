import fs from 'node:fs';
import path from 'node:path';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const defaultBackendUrl = 'http://127.0.0.1:8080';
const repositoryEnvPath = path.resolve(__dirname, '..', '.env');

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': backendProxyTarget(process.env, repositoryEnvValue(repositoryEnvPath)),
      '/health': backendProxyTarget(process.env, repositoryEnvValue(repositoryEnvPath))
    }
  }
});

export function backendProxyTarget(env: Record<string, string | undefined>, envFileValue?: string | null) {
  return (
    env.PATCHPILOT_FRONTEND_BACKEND_URL?.trim() ||
    env.VITE_PATCHPILOT_BACKEND_URL?.trim() ||
    envFileValue?.trim() ||
    defaultBackendUrl
  );
}

export function frontendBackendUrlFromEnvFile(content: string) {
  for (const line of content.split(/\r?\n/)) {
    const trimmed = line.trim();
    if (!trimmed || trimmed.startsWith('#')) {
      continue;
    }
    const [key, ...valueParts] = trimmed.split('=');
    if (key === 'PATCHPILOT_FRONTEND_BACKEND_URL' || key === 'VITE_PATCHPILOT_BACKEND_URL') {
      return valueParts.join('=').trim() || null;
    }
  }
  return null;
}

function repositoryEnvValue(envPath: string) {
  if (!fs.existsSync(envPath)) {
    return null;
  }
  return frontendBackendUrlFromEnvFile(fs.readFileSync(envPath, 'utf8'));
}
