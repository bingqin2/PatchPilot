import { render, screen, within } from '@testing-library/react';
import type { DemoReadiness } from '../../types';
import userEvent from '@testing-library/user-event';
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
  expect(screen.getAllByText('Configure missing credentials in .env and restart the backend.')).toHaveLength(2);
});

test('shows evaluation baseline gate action inside the blocked check row', () => {
  render(
    <DemoReadinessPanel
      readiness={{
        status: 'BLOCKED',
        summary: 'PatchPilot is blocked for demo use.',
        checks: [
          {
            name: 'Evaluation baseline',
            status: 'BLOCKED',
            message: 'Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix.',
            action: 'Investigate newly failed fixture cases before using the baseline as demo evidence.'
          }
        ],
        nextActions: ['Investigate newly failed fixture cases before using the baseline as demo evidence.']
      }}
      error={null}
    />
  );

  const baselineRow = screen.getByRole('listitem', { name: /Evaluation baseline blocked/i });
  expect(within(baselineRow).getByText('Latest fixture baseline regressed. Newly failed cases: node-npm-basic-fix.')).toBeInTheDocument();
  expect(
    within(baselineRow).getByText('Investigate newly failed fixture cases before using the baseline as demo evidence.')
  ).toBeInTheDocument();
});

test('shows readiness API errors without hiding previous readiness data', () => {
  render(<DemoReadinessPanel readiness={readyReadiness} error="Backend request failed" />);

  expect(screen.getByText('Demo readiness unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('PatchPilot is ready for a controlled demo.')).toBeInTheDocument();
});

test('archives current readiness and copies recent snapshot reports', async () => {
  const writeText = vi.fn(async () => undefined);
  Object.assign(navigator, { clipboard: { writeText } });
  const archivedSnapshot = {
    id: 'readiness-snapshot-1',
    status: 'BLOCKED' as const,
    summary: 'PatchPilot is blocked before a live demo.',
    readyCheckCount: 1,
    needsAttentionCheckCount: 1,
    blockedCheckCount: 1,
    createdAt: '2026-06-27T04:00:00Z',
    report: '# PatchPilot Demo Readiness Snapshot\n\n- Status: `BLOCKED`'
  };
  const onArchiveReadiness = vi.fn(async () => archivedSnapshot);

  render(
    <DemoReadinessPanel
      readiness={readyReadiness}
      error={null}
      snapshots={[archivedSnapshot]}
      snapshotError={null}
      onArchiveReadiness={onArchiveReadiness}
      onDownloadSnapshotReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Archive readiness' }));
  expect(onArchiveReadiness).toHaveBeenCalledTimes(1);
  expect(screen.getByText('Demo readiness snapshot archived')).toBeInTheDocument();
  expect(screen.getByRole('heading', { name: 'Recent readiness snapshots' })).toBeInTheDocument();
  expect(screen.getByText('readiness-snapshot-1')).toBeInTheDocument();
  expect(screen.getByText('1 ready / 1 warning / 1 blocked')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Copy readiness snapshot report readiness-snapshot-1' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Readiness Snapshot\n\n- Status: `BLOCKED`');
  expect(screen.getByText('Readiness snapshot report copied')).toBeInTheDocument();
});

test('shows readiness snapshot trend summary and copies trend report', async () => {
  const writeText = vi.fn(async () => undefined);
  Object.assign(navigator, { clipboard: { writeText } });

  render(
    <DemoReadinessPanel
      readiness={readyReadiness}
      error={null}
      snapshots={[]}
      snapshotError={null}
      snapshotTrend={{
        status: 'IMPROVING',
        summary: 'Demo readiness improved from BLOCKED to READY.',
        latestSnapshotId: 'readiness-snapshot-new',
        previousSnapshotId: 'readiness-snapshot-old',
        latestReadinessStatus: 'READY',
        previousReadinessStatus: 'BLOCKED',
        readyCheckDelta: 4,
        needsAttentionCheckDelta: -2,
        blockedCheckDelta: -2,
        nextAction: 'Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.',
        markdownReport: '# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`'
      }}
      snapshotTrendError={null}
    />
  );

  expect(screen.getByRole('heading', { name: 'Snapshot trend' })).toBeInTheDocument();
  expect(screen.getByText('Improving')).toBeInTheDocument();
  expect(screen.getByText('Demo readiness improved from BLOCKED to READY.')).toBeInTheDocument();
  expect(screen.getByText('Latest readiness-snapshot-new')).toBeInTheDocument();
  expect(screen.getByText('Previous readiness-snapshot-old')).toBeInTheDocument();
  expect(screen.getByText('+4 ready / -2 warning / -2 blocked')).toBeInTheDocument();
  expect(screen.getByText('Use the latest readiness snapshot as demo evidence or archive one more snapshot immediately before the live run.')).toBeInTheDocument();

  await userEvent.click(screen.getByRole('button', { name: 'Copy readiness snapshot trend report' }));
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Readiness Snapshot Trend\n\n- Status: `IMPROVING`');
  expect(screen.getByText('Readiness snapshot trend report copied')).toBeInTheDocument();
});
