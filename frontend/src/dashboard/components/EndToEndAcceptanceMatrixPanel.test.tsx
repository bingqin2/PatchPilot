import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoEndToEndAcceptanceMatrix } from '../../types';
import { EndToEndAcceptanceMatrixPanel } from './EndToEndAcceptanceMatrixPanel';

const matrix: DemoEndToEndAcceptanceMatrix = {
  status: 'NEEDS_ATTENTION',
  readyForFinalDemo: false,
  readinessPercent: 78,
  readyCount: 7,
  needsAttentionCount: 2,
  blockedCount: 0,
  totalCount: 9,
  summary: 'PatchPilot is close to final demo readiness, with two remaining gaps.',
  nextActions: [
    'Close the pending review task before a public demo.',
    'Run one fresh live launch gate before posting the trigger.'
  ],
  sideEffectContract:
    'GET /api/demo/end-to-end-acceptance-matrix is read-only: it does not create tasks, call the model, run tests, mutate Git, create branches, create pull requests, post comments, archive records, or write to GitHub.',
  items: [
    {
      category: 'Launch',
      name: 'Live launch gate',
      status: 'READY',
      evidence: 'Live launch gate is READY for bingqin2/PatchPilot#1.',
      gap: 'No launch gate gap.',
      nextAction: 'No action needed.'
    },
    {
      category: 'Pending review safety',
      name: 'Generated diff risk review',
      status: 'NEEDS_ATTENTION',
      evidence: 'One generated diff is waiting for operator review.',
      gap: 'Resolve the pending review before final demo.',
      nextAction: 'Approve or reject the pending review task.'
    }
  ],
  generatedAt: '2026-07-01T12:00:00Z',
  markdownReport: '# PatchPilot End-to-End Acceptance Matrix'
};

afterEach(() => {
  vi.unstubAllGlobals();
});

test('renders acceptance matrix counts, gaps, and next actions', () => {
  render(<EndToEndAcceptanceMatrixPanel matrix={matrix} error={null} onRefresh={vi.fn()} />);

  const panel = screen.getByLabelText('End-to-end acceptance matrix');
  expect(within(panel).getByRole('heading', { name: 'End-to-end acceptance' })).toBeInTheDocument();
  expect(within(panel).getByText('78%')).toBeInTheDocument();
  expect(within(panel).getByText('7 ready')).toBeInTheDocument();
  expect(within(panel).getByText('2 warning')).toBeInTheDocument();
  expect(within(panel).getByText('0 blocked')).toBeInTheDocument();
  expect(within(panel).getByText('Live launch gate')).toBeInTheDocument();
  expect(within(panel).getByText('Generated diff risk review')).toBeInTheDocument();
  expect(within(panel).getByText('Resolve the pending review before final demo.')).toBeInTheDocument();
  expect(within(panel).getByText('Close the pending review task before a public demo.')).toBeInTheDocument();
});

test('copies the markdown matrix report and refreshes the panel', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn();
  const onRefresh = vi.fn();
  vi.stubGlobal('navigator', {
    clipboard: { writeText }
  });

  render(<EndToEndAcceptanceMatrixPanel matrix={matrix} error={null} onRefresh={onRefresh} />);

  await user.click(screen.getByRole('button', { name: 'Copy matrix report' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot End-to-End Acceptance Matrix');
  expect(screen.getByText('Matrix report copied')).toBeInTheDocument();

  await user.click(screen.getByRole('button', { name: 'Refresh matrix' }));
  expect(onRefresh).toHaveBeenCalledTimes(1);
});

test('shows empty and error states without hiding the refresh action', () => {
  render(
    <EndToEndAcceptanceMatrixPanel
      matrix={null}
      error="backend unavailable"
      onRefresh={vi.fn()}
    />
  );

  expect(screen.getByText('End-to-end acceptance unavailable')).toBeInTheDocument();
  expect(screen.getByText('backend unavailable')).toBeInTheDocument();
  expect(screen.getByText('No end-to-end acceptance matrix loaded.')).toBeInTheDocument();
  expect(screen.getByRole('button', { name: 'Refresh matrix' })).toBeEnabled();
});
