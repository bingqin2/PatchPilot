import { render, screen, within } from '@testing-library/react';
import type { DemoSessionSnapshot } from '../../types';
import { DemoSessionSnapshotPanel } from './DemoSessionSnapshotPanel';

const snapshot: DemoSessionSnapshot = {
  sessionId: 'demo-session-20260624T003000Z',
  status: 'READY',
  summary: 'Demo session snapshot is ready.',
  generatedAt: '2026-06-24T00:30:00Z',
  evidenceBundle: {
    status: 'READY',
    summary: 'Demo evidence bundle is ready.',
    summaryCounts: {
      adapterFixtureCount: 12,
      failedAdapterFixtureCount: 0,
      recentTaskCount: 2,
      activeQuarantineCount: 0,
      recentPullRequestAvailable: true
    },
    readiness: {
      status: 'READY',
      summary: 'PatchPilot is ready for a controlled demo.',
      checks: [],
      nextActions: []
    },
    smokeChecklist: {
      status: 'READY',
      summary: 'Live demo smoke checklist is ready.',
      steps: [],
      nextActions: []
    },
    configuration: null,
    adapterFixtures: {
      totalCount: 12,
      failedCount: 0
    },
    queueSummary: {
      totalCount: 2,
      pendingCount: 0,
      availablePendingCount: 0,
      delayedPendingCount: 0,
      runningCount: 0,
      completedCount: 2,
      failedCount: 0,
      cancelledCount: 0
    },
    recentTask: {
      id: 'task-1',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      installationId: 0,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix replace docs/demo.md demo',
      deliveryId: 'delivery-task-1',
      commentId: 123,
      status: 'COMPLETED',
      failureReason: null,
      createdAt: '2026-06-24T00:00:00Z',
      pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
      completedAt: '2026-06-24T00:05:00Z',
      updatedAt: '2026-06-24T00:05:00Z',
      language: 'java',
      buildSystem: 'maven',
      verificationCommand: 'mvn test',
      adapterDetectionReason: 'Detected Maven project',
      statusCommentId: 456,
      statusCommentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
      riskReviewApprovedAt: null,
      riskReviewApprovedBy: null,
      riskReviewApprovalReason: null,
      retrySourceTaskId: null,
      retrySourceStatus: null,
      retrySourceFailureReason: null,
      retriedAt: null
    },
    recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestWebhookDelivery: null,
    rejectedTriggerSummary: null,
    activeQuarantineCount: 0,
    generatedAt: '2026-06-24T00:00:00Z',
    nextActions: ['Follow the script from step 1 through Pull Request review.']
  },
  script: {
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
      }
    ],
    healthContract: ['The script endpoint is read-only.'],
    nextActions: ['Follow the script from step 1 through Pull Request review.'],
    generatedAt: '2026-06-24T00:30:00Z'
  },
  runbook: '# PatchPilot Demo Runbook\n\n- Status: `READY`',
  operatorChecklist: [
    'Open the dashboard and confirm the demo session snapshot status.',
    'Verify the latest webhook delivery and recent task before posting a live trigger.'
  ],
  healthContract: [
    'GET /api/demo/session-snapshot is read-only: it does not create tasks, call the model, run tests, mutate Git, or write to GitHub.'
  ],
  shareSummary: 'Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
  nextActions: ['Follow the script from step 1 through Pull Request review.']
};

test('renders demo session snapshot summary, evidence, checklist, and contract', () => {
  render(<DemoSessionSnapshotPanel snapshot={snapshot} error={null} />);

  const panel = screen.getByRole('region', { name: 'Demo session snapshot' });
  expect(within(panel).getByRole('heading', { name: 'Demo session snapshot' })).toBeInTheDocument();
  expect(within(panel).getByText('demo-session-20260624T003000Z')).toBeInTheDocument();
  expect(within(panel).getByText('Demo session snapshot is ready.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Ready')).toHaveLength(2);
  expect(within(panel).getByText('Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.')).toBeInTheDocument();
  expect(within(panel).getByText('https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(within(panel).getByText('task-1')).toBeInTheDocument();
  expect(within(panel).getByText('1 step')).toBeInTheDocument();
  expect(within(panel).getByText('Open the dashboard and confirm the demo session snapshot status.')).toBeInTheDocument();
  expect(within(panel).getByText(/does not create tasks, call the model, run tests, mutate Git, or write to GitHub/)).toBeInTheDocument();
});

test('shows loading and API errors without hiding snapshot data', () => {
  const { rerender } = render(<DemoSessionSnapshotPanel snapshot={null} error={null} />);

  expect(screen.getByText('Demo session snapshot has not loaded yet.')).toBeInTheDocument();

  rerender(<DemoSessionSnapshotPanel snapshot={snapshot} error="Backend request failed" />);

  expect(screen.getByText('Demo session snapshot unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('demo-session-20260624T003000Z')).toBeInTheDocument();
});
