import { render, screen, within } from '@testing-library/react';
import type { DemoScript } from '../../types';
import { DemoScriptPanel } from './DemoScriptPanel';

const script: DemoScript = {
  status: 'READY',
  summary: 'Demo script is ready.',
  steps: [
    {
      order: 1,
      name: 'Confirm backend and dashboard access',
      status: 'READY',
      operatorAction: 'Open the dashboard and confirm protected APIs load.',
      verificationCommand: 'curl http://127.0.0.1:8080/health',
      successCriteria: 'Backend reports UP and dashboard data loads.',
      troubleshootingPanel: 'Connectivity panel',
      evidence: 'Backend readiness endpoint is reachable.'
    },
    {
      order: 4,
      name: 'Create controlled /agent fix trigger',
      status: 'NEEDS_ATTENTION',
      operatorAction: 'Post `/agent fix replace docs/demo.md PatchPilot smoke test` on the demo issue.',
      verificationCommand: 'curl ${ADMIN_HEADER[@]} http://127.0.0.1:8080/api/github/webhook-deliveries?limit=10',
      successCriteria: 'Webhook delivery creates exactly one task.',
      troubleshootingPanel: 'Webhook delivery panel',
      evidence: 'delivery-1'
    }
  ],
  healthContract: [
    'GET /api/demo/script is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.',
    'Live execution still starts from a controlled GitHub issue comment or manual task creation.'
  ],
  nextActions: ['Follow the script from step 1 through Pull Request review.'],
  generatedAt: '2026-06-24T00:00:00Z'
};

test('renders ordered demo script steps and health contract', () => {
  render(<DemoScriptPanel script={script} error={null} />);

  const panel = screen.getByRole('region', { name: 'Demo script' });
  expect(within(panel).getByRole('heading', { name: 'Demo script' })).toBeInTheDocument();
  expect(within(panel).getAllByText('Ready')).toHaveLength(2);
  expect(within(panel).getByText('Demo script is ready.')).toBeInTheDocument();
  expect(within(panel).getByText('Confirm backend and dashboard access')).toBeInTheDocument();
  expect(within(panel).getByText('Create controlled /agent fix trigger')).toBeInTheDocument();
  expect(within(panel).getByText('curl http://127.0.0.1:8080/health')).toBeInTheDocument();
  expect(within(panel).getByText('Connectivity panel')).toBeInTheDocument();
  expect(within(panel).getByText(/does not create tasks, call the model, run tests, mutate Git, or write to GitHub/)).toBeInTheDocument();
  expect(within(panel).getByText('Follow the script from step 1 through Pull Request review.')).toBeInTheDocument();
});

test('shows loading and API errors without hiding existing script data', () => {
  const { rerender } = render(<DemoScriptPanel script={null} error={null} />);

  expect(screen.getByText('Demo script has not loaded yet.')).toBeInTheDocument();

  rerender(<DemoScriptPanel script={script} error="Backend request failed" />);

  expect(screen.getByText('Demo script unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('Demo script is ready.')).toBeInTheDocument();
});
