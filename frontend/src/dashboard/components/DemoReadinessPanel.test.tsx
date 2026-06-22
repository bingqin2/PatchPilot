import { render, screen, within } from '@testing-library/react';
import type { DemoReadiness } from '../../types';
import { DemoReadinessPanel } from './DemoReadinessPanel';

const readyReadiness: DemoReadiness = {
  status: 'READY',
  summary: 'PatchPilot is ready for a controlled demo.',
  checks: [
    {
      name: 'Backend',
      status: 'READY',
      message: 'Backend readiness endpoint is reachable.',
      action: 'No action needed.'
    },
    {
      name: 'Credentials',
      status: 'READY',
      message: 'Required credentials are configured.',
      action: 'No action needed.'
    },
    {
      name: 'Safety policy',
      status: 'READY',
      message: 'Trigger users, repositories, review approvers, command safety, and rate limits are configured.',
      action: 'No action needed.'
    }
  ],
  nextActions: ['Open a controlled GitHub issue and comment /agent fix with a concrete change request.']
};

test('shows ready demo readiness with checks and next action', () => {
  render(<DemoReadinessPanel readiness={readyReadiness} error={null} />);

  expect(screen.getByRole('heading', { name: 'Demo readiness' })).toBeInTheDocument();
  expect(screen.getByText('Ready')).toBeInTheDocument();
  expect(screen.getByText('PatchPilot is ready for a controlled demo.')).toBeInTheDocument();
  const credentialsRow = screen.getByRole('listitem', { name: /Credentials ready/i });
  expect(within(credentialsRow).getByText('Required credentials are configured.')).toBeInTheDocument();
  const safetyPolicyRow = screen.getByRole('listitem', { name: /Safety policy ready/i });
  expect(within(safetyPolicyRow).getByText(/Trigger users, repositories, review approvers/)).toBeInTheDocument();
  expect(screen.getByText('Open a controlled GitHub issue and comment /agent fix with a concrete change request.')).toBeInTheDocument();
});

test('shows blockers and warnings before a demo', () => {
  render(
    <DemoReadinessPanel
      readiness={{
        status: 'BLOCKED',
        summary: 'PatchPilot is blocked for demo use.',
        checks: [
          {
            name: 'Credentials',
            status: 'BLOCKED',
            message: 'Agent API key is missing; GitHub token is missing.',
            action: 'Configure missing credentials in .env and restart the backend.'
          },
          {
            name: 'Queue',
            status: 'NEEDS_ATTENTION',
            message: '2 failed queue items.',
            action: 'Inspect failed or running queue items before starting a demo.'
          },
          {
            name: 'Safety policy',
            status: 'NEEDS_ATTENTION',
            message: 'Trigger user allowlist is open; Review approval allowlist is missing.',
            action: 'Configure PATCHPILOT_ALLOWED_TRIGGER_USERS and PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS before a live demo.'
          }
        ],
        nextActions: [
          'Configure missing credentials in .env and restart the backend.',
          'Inspect failed or running queue items before starting a demo.',
          'Configure PATCHPILOT_ALLOWED_TRIGGER_USERS and PATCHPILOT_REVIEW_APPROVAL_ALLOWED_OPERATORS before a live demo.'
        ]
      }}
      error={null}
    />
  );

  expect(screen.getByText('Blocked')).toBeInTheDocument();
  expect(screen.getByText('PatchPilot is blocked for demo use.')).toBeInTheDocument();
  expect(screen.getByRole('listitem', { name: /Credentials blocked/i })).toBeInTheDocument();
  expect(screen.getByRole('listitem', { name: /Queue needs attention/i })).toBeInTheDocument();
  expect(screen.getByRole('listitem', { name: /Safety policy needs attention/i })).toBeInTheDocument();
  expect(screen.getByText('Trigger user allowlist is open; Review approval allowlist is missing.')).toBeInTheDocument();
  expect(screen.getByText('Configure missing credentials in .env and restart the backend.')).toBeInTheDocument();
});

test('shows readiness API errors without hiding previous readiness data', () => {
  render(<DemoReadinessPanel readiness={readyReadiness} error="Backend request failed" />);

  expect(screen.getByText('Demo readiness unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('PatchPilot is ready for a controlled demo.')).toBeInTheDocument();
});
