/* @vitest-environment node */

import { describe, expect, test } from 'vitest';
import { backendProxyTarget, frontendBackendUrlFromEnvFile } from './vite.config';

describe('backendProxyTarget', () => {
  test('defaults frontend proxy to the Docker backend port', () => {
    expect(backendProxyTarget({})).toBe('http://127.0.0.1:8080');
  });

  test('uses configured frontend backend URL for IDEA local runs', () => {
    expect(
      backendProxyTarget({
        PATCHPILOT_FRONTEND_BACKEND_URL: 'http://127.0.0.1:18080'
      })
    ).toBe('http://127.0.0.1:18080');
  });

  test('accepts Vite-prefixed backend URL as an alias', () => {
    expect(
      backendProxyTarget({
        VITE_PATCHPILOT_BACKEND_URL: 'http://localhost:18080'
      })
    ).toBe('http://localhost:18080');
  });

  test('loads frontend proxy target from repository env file content', () => {
    expect(frontendBackendUrlFromEnvFile('PATCHPILOT_FRONTEND_BACKEND_URL=http://127.0.0.1:18080')).toBe(
      'http://127.0.0.1:18080'
    );
  });
});
