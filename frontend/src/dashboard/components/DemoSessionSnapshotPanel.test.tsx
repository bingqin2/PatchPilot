import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoArchivedLaunchOutcome,
  DemoHandoffReadiness,
  DemoHandoffPackageArchive,
  DemoHandoffPackageArchiveSummary,
  DemoHandoffShareCenter,
  DemoHandoffShareDeliveryReceipt,
  DemoHandoffShareDeliveryReceiptInput,
  DemoHandoffShareInstructions,
  DemoHandoffShareChecklist,
  DemoPreparedLaunchCommand,
  DemoSessionArchive,
  DemoSessionSnapshot
} from '../../types';
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
    handoffShareChecklistStatus: 'READY',
    handoffShareChecklistSummary: 'Latest handoff package archive is share-ready.',
    handoffShareChecklistNextAction: 'Share the current handoff package.',
    handoffShareCenterStatus: 'READY',
    handoffShareCenterSummary: 'Post-demo handoff package is ready to share.',
    handoffShareCenterNextAction:
      'Download the package, archive summary, and share checklist before sending handoff evidence.',
    handoffShareCenterDownloadActions: [
      'Download handoff package archive handoff-archive-1.',
      'Download handoff package archive summary.',
      'Download handoff share checklist.',
      'Download handoff share delivery receipt delivery-receipt-1.'
    ],
    handoffShareDeliveryReceiptRecorded: true,
    handoffShareLatestDeliveryReceiptId: 'delivery-receipt-1',
    handoffShareLatestDeliveryTarget: 'maintainer@example.com',
    handoffShareLatestDeliveryChannel: 'email',
    handoffShareLatestDeliveredAt: '2026-06-24T06:05:00Z',
    handoffShareDeliveryReceiptFreshness: 'FRESH',
    handoffShareDeliveryReceiptFresh: true,
    handoffShareDeliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current handoff archive and session.',
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
      retryReason: null,
      retriedAt: null
    },
    recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    webhookSetupReadiness: null,
    latestWebhookDelivery: null,
    recentWebhookDeliveries: [],
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
  readinessSnapshotTrend: {
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
  },
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

const archives: DemoSessionArchive[] = [
  {
    id: 'archive-1',
    sessionId: 'demo-session-20260624T003000Z',
    status: 'READY',
    summary: 'Demo session snapshot is ready.',
    shareSummary: 'Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
    recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    createdAt: '2026-06-24T04:00:00Z',
    report: '# PatchPilot Demo Session Report\n\n- Status: `READY`'
  }
];

const handoffPackageArchives: DemoHandoffPackageArchive[] = [
  {
    id: 'handoff-archive-1',
    sessionId: 'demo-session-20260624T003000Z',
    status: 'READY',
    summary: 'Demo session snapshot is ready.',
    shareSummary: 'Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.',
    handoffReadinessStatus: 'READY',
    handoffReadinessSummary: 'Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.',
    handoffReadinessNextAction: 'No missing handoff evidence.',
    handoffReadyCheckCount: 7,
    handoffNeedsAttentionCheckCount: 0,
    handoffBlockedCheckCount: 0,
    recentPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    createdAt: '2026-06-24T04:05:00Z',
    report: '# PatchPilot Demo Handoff Package\n\n- Status: `READY`'
  }
];

const handoffPackageArchiveSummary: DemoHandoffPackageArchiveSummary = {
  status: 'READY',
  shareReady: true,
  archiveCount: 1,
  latestArchiveId: 'handoff-archive-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestHandoffReadinessStatus: 'READY',
  latestCreatedAt: '2026-06-24T04:05:00Z',
  summary: 'Latest archived handoff package is READY and can be shared.',
  nextAction: 'No missing handoff evidence.',
  markdownReport: '# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`'
};

const handoffShareChecklist: DemoHandoffShareChecklist = {
  status: 'READY',
  summary: 'Latest handoff archive is ready to share.',
  nextAction: 'Share the latest handoff package summary and archived package with the reviewer.',
  checks: [
    {
      name: 'Handoff package archive',
      status: 'READY',
      summary: '1 archived handoff package is available.',
      nextAction: 'Use archive handoff-archive-1 as the latest package.'
    },
    {
      name: 'Portable evidence',
      status: 'READY',
      summary: 'Markdown evidence is available for the latest handoff package.',
      nextAction: 'Copy or download the handoff share checklist before handoff.'
    }
  ],
  markdownReport: '# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`',
  generatedAt: '2026-06-24T05:00:00Z'
};

const handoffShareCenter: DemoHandoffShareCenter = {
  status: 'READY',
  shareReady: true,
  summary: 'Post-demo handoff package is ready to share.',
  nextAction: 'Download the package, archive summary, and share checklist before sending handoff evidence.',
  latestArchiveId: 'handoff-archive-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  latestCreatedAt: '2026-06-24T04:05:00Z',
  latestDeliveryReceiptId: 'delivery-receipt-1',
  latestDeliveryTarget: 'maintainer@example.com',
  latestDeliveryChannel: 'email',
  latestDeliveredAt: '2026-06-24T06:05:00Z',
  deliveryReceiptRecorded: true,
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current handoff archive and session.',
  downloadActions: [
    'Download handoff package archive handoff-archive-1.',
    'Download handoff package archive summary.',
    'Download handoff share checklist.',
    'Download handoff share delivery receipt delivery-receipt-1.'
  ],
  evidenceNotes: [
    'Latest package archive is READY.',
    'Share checklist has 2 checks.',
    'Latest delivery receipt delivery-receipt-1 was recorded for maintainer@example.com via email.'
  ],
  markdownReport: '# PatchPilot Demo Handoff Share Center\n\n- Status: `READY`',
  generatedAt: '2026-06-24T05:30:00Z'
};

const handoffShareInstructions: DemoHandoffShareInstructions = {
  status: 'READY',
  sendReady: true,
  summary: 'Share the current handoff package with repository maintainers and demo reviewers.',
  nextAction: 'Send the prepared handoff message with all required attachments.',
  latestArchiveId: 'handoff-archive-1',
  latestSessionId: 'demo-session-20260624T003000Z',
  recommendedRecipients: ['Repository owner or maintainer', 'Demo reviewer'],
  requiredAttachments: [
    'Handoff package archive handoff-archive-1',
    'Handoff package archive summary',
    'Handoff share checklist',
    'Handoff share center report'
  ],
  preSendChecks: [
    'Confirm the Pull Request link in the handoff package opens correctly.',
    'Confirm no handoff share checklist warnings remain.'
  ],
  messageSubject: 'PatchPilot demo handoff: demo-session-20260624T003000Z',
  messageBody: 'The PatchPilot demo handoff package is ready to share.',
  markdownReport: '# PatchPilot Demo Handoff Share Instructions\n\n- Status: `READY`',
  generatedAt: '2026-06-24T05:45:00Z'
};

const handoffShareDeliveryReceipts: DemoHandoffShareDeliveryReceipt[] = [
  {
    id: 'delivery-receipt-1',
    status: 'READY',
    handoffArchiveId: 'handoff-archive-1',
    sessionId: 'demo-session-20260624T003000Z',
    deliveryChannel: 'email',
    deliveryTarget: 'maintainer@example.com',
    operator: 'local-operator',
    notes: 'Sent after the demo review.',
    messageSubject: 'PatchPilot demo handoff: demo-session-20260624T003000Z',
    deliveredAt: '2026-06-24T06:05:00Z',
    createdAt: '2026-06-24T06:10:00Z',
    markdownReport: '# PatchPilot Demo Handoff Share Delivery Receipt\n\n- Status: `READY`'
  }
];

const preparedLaunchCommands: DemoPreparedLaunchCommand[] = [
  {
    triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    operation: 'replace',
    targetPath: 'docs/demo.md',
    replacementText: 'PatchPilot smoke test',
    savedAt: '2026-06-26T01:00:00Z'
  },
  {
    triggerComment: '/agent fix touch docs/history.md',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 2,
    triggerUser: 'bingqin2',
    operation: 'touch',
    targetPath: 'docs/history.md',
    replacementText: null,
    savedAt: '2026-06-26T01:05:00Z'
  }
];

const archivedLaunchOutcomes: DemoArchivedLaunchOutcome[] = [
  {
    triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    taskId: 'task-1',
    taskStatus: 'COMPLETED',
    pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    archivedAt: '2026-06-26T01:10:00Z',
    report: '# PatchPilot Demo Launch Outcome Report\n\n- Task: `task-1`'
  }
];

const handoffReadiness: DemoHandoffReadiness = {
  status: 'READY',
  summary: 'Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.',
  nextAction: 'No missing handoff evidence.',
  checks: [
    {
      name: 'Demo snapshot status',
      status: 'READY',
      summary: 'Demo session snapshot is ready.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Recent task evidence',
      status: 'READY',
      summary: 'task-1 is completed.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Webhook delivery evidence',
      status: 'READY',
      summary: 'delivery-1 created task task-1.',
      nextAction: 'No action needed.'
    },
    {
      name: 'Prepared command context',
      status: 'READY',
      summary: '1 prepared command recorded.',
      nextAction: 'No action needed.'
    }
  ]
};

const missingHandoffReadiness: DemoHandoffReadiness = {
  status: 'NEEDS_ATTENTION',
  summary: 'Handoff package is missing evidence required for a credible live-demo handoff.',
  nextAction: 'Complete the handoff readiness checks that need attention before sharing the package.',
  checks: [
    {
      name: 'Recent task evidence',
      status: 'NEEDS_ATTENTION',
      summary: 'No recent completed task is available in the session snapshot.',
      nextAction: 'Run one controlled /agent fix task and wait for COMPLETED status.'
    },
    {
      name: 'Webhook delivery evidence',
      status: 'NEEDS_ATTENTION',
      summary: 'No recent webhook delivery evidence is available in the session snapshot.',
      nextAction: 'Deliver one controlled GitHub issue comment and confirm TASK_CREATED webhook evidence.'
    },
    {
      name: 'Prepared command context',
      status: 'NEEDS_ATTENTION',
      summary: 'No prepared launch command was captured in this browser session.',
      nextAction: 'Use the dashboard launch command composer before handoff.'
    }
  ]
};

const sessionReportInput = {
  preparedLaunchCommands,
  archivedLaunchOutcomes
};

afterEach(() => {
  vi.restoreAllMocks();
  vi.unstubAllGlobals();
});

test('renders demo session snapshot summary, evidence, checklist, contract, and archives', () => {
  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={archives}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      handoffShareDeliveryReceipts={handoffShareDeliveryReceipts}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onCreateHandoffShareDeliveryReceipt={vi.fn()}
      onDownloadHandoffShareDeliveryReceiptReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo session snapshot' });
  expect(within(panel).getByRole('heading', { name: 'Demo session snapshot' })).toBeInTheDocument();
  expect(within(panel).getAllByText('demo-session-20260624T003000Z').length).toBeGreaterThanOrEqual(4);
  expect(within(panel).getAllByText('Demo session snapshot is ready.')).toHaveLength(2);
  expect(within(panel).getAllByText('Ready').length).toBeGreaterThanOrEqual(3);
  expect(
    within(panel).getAllByText('Status READY; recent task task-1; recent PR https://github.com/bingqin2/PatchPilot/pull/42.')
  ).toHaveLength(3);
  expect(within(panel).getAllByText('https://github.com/bingqin2/PatchPilot/pull/42')).toHaveLength(2);
  expect(within(panel).getByText('task-1')).toBeInTheDocument();
  expect(within(panel).getByText('1 step')).toBeInTheDocument();
  expect(within(panel).getByText('Readiness trend')).toBeInTheDocument();
  expect(within(panel).getByText('Improving')).toBeInTheDocument();
  expect(within(panel).getByText('+4 ready / -2 warning / -2 blocked')).toBeInTheDocument();
  expect(within(panel).getByText(/Use the latest readiness snapshot as demo evidence/)).toBeInTheDocument();
  expect(within(panel).getByText('Handoff readiness')).toBeInTheDocument();
  expect(
    within(panel).getByText('Handoff package has current webhook delivery, PR, command, outcome, and readiness trend evidence.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Handoff evidence')).toBeInTheDocument();
  expect(within(panel).getAllByText('No missing handoff evidence.')).toHaveLength(3);
  expect(within(panel).getByText('2 commands / 1 outcome')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff readiness checks' })).toBeInTheDocument();
  expect(within(panel).getByText('Webhook delivery evidence')).toBeInTheDocument();
  expect(within(panel).getByText('delivery-1 created task task-1.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Next action: No action needed.').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Prepared command context')).toBeInTheDocument();
  expect(within(panel).getByText('1 prepared command recorded.')).toBeInTheDocument();
  expect(within(panel).getByText('Open the dashboard and confirm the demo session snapshot status.')).toBeInTheDocument();
  expect(within(panel).getByText(/does not create tasks, call the model, run tests, mutate Git, or write to GitHub/)).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Recent session archives' })).toBeInTheDocument();
  expect(within(panel).getByText('archive-1')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Recent handoff package archives' })).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff package archive summary' })).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff share center' })).toBeInTheDocument();
  expect(within(panel).getByText('Post-demo handoff package is ready to share.')).toBeInTheDocument();
  expect(within(panel).getByText('Download handoff package archive handoff-archive-1.')).toBeInTheDocument();
  expect(within(panel).getAllByText('Download handoff share delivery receipt delivery-receipt-1.').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Latest package archive is READY.')).toBeInTheDocument();
  expect(within(panel).getByText('Latest delivery')).toBeInTheDocument();
  expect(within(panel).getByText('Fresh')).toBeInTheDocument();
  expect(
    within(panel).getByText('Latest delivery receipt matches the current handoff archive and session.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('email - maintainer@example.com').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByRole('button', { name: 'Download handoff share center' })).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff share instructions' })).toBeInTheDocument();
  expect(within(panel).getByText('Share the current handoff package with repository maintainers and demo reviewers.')).toBeInTheDocument();
  expect(within(panel).getByText('Repository owner or maintainer')).toBeInTheDocument();
  expect(within(panel).getByText('Handoff share center report')).toBeInTheDocument();
  expect(within(panel).getAllByText('PatchPilot demo handoff: demo-session-20260624T003000Z').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('button', { name: 'Copy handoff share instructions' })).toBeInTheDocument();
  expect(within(panel).getByRole('button', { name: 'Download handoff share instructions' })).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff share delivery receipts' })).toBeInTheDocument();
  expect(within(panel).getAllByText('delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText(/email - maintainer@example\.com/).length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('button', { name: 'Record handoff share delivery receipt' })).toBeInTheDocument();
  expect(within(panel).getByRole('button', { name: 'Download handoff share delivery receipt delivery-receipt-1' })).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Handoff share checklist' })).toBeInTheDocument();
  expect(within(panel).getByRole('button', { name: 'Download handoff share checklist' })).toBeInTheDocument();
  expect(within(panel).getByText('Latest handoff archive is ready to share.')).toBeInTheDocument();
  expect(within(panel).getByText('Portable evidence')).toBeInTheDocument();
  expect(within(panel).getAllByText('Share-ready').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Latest archived handoff package is READY and can be shared.')).toBeInTheDocument();
  expect(within(panel).getByText('1 archived package')).toBeInTheDocument();
  expect(within(panel).getByText('Latest archive: handoff-archive-1')).toBeInTheDocument();
  expect(within(panel).getByRole('button', { name: 'Copy handoff archive summary' })).toBeInTheDocument();
  expect(within(panel).getByRole('button', { name: 'Download handoff archive summary' })).toBeInTheDocument();
  expect(within(panel).getAllByText('handoff-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Handoff readiness: Ready')).toBeInTheDocument();
  expect(within(panel).getByText('7 ready / 0 warning / 0 blocked')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Prepared launch commands' })).toBeInTheDocument();
  expect(within(panel).getAllByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toHaveLength(2);
  expect(within(panel).getByText('/agent fix touch docs/history.md')).toBeInTheDocument();
  expect(within(panel).getByRole('heading', { name: 'Archived launch outcomes' })).toBeInTheDocument();
  expect(within(panel).getByText('COMPLETED')).toBeInTheDocument();
});

test('shows loading and API errors without hiding snapshot data', () => {
  const { rerender } = render(
    <DemoSessionSnapshotPanel
      snapshot={null}
      preparedLaunchCommands={[]}
      archivedLaunchOutcomes={[]}
      handoffReadiness={null}
      archives={[]}
      handoffPackageArchives={[]}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  expect(screen.getByText('Demo session snapshot has not loaded yet.')).toBeInTheDocument();

  rerender(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={archives}
      handoffPackageArchives={handoffPackageArchives}
      error="Backend request failed"
      archiveError="Archive request failed"
      handoffPackageArchiveSummaryError="Handoff package archive summary request failed"
      handoffPackageArchiveError="Handoff package archive request failed"
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  expect(screen.getByText('Demo session snapshot unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('Archive request failed')).toBeInTheDocument();
  expect(screen.getByText('Handoff package archive request failed')).toBeInTheDocument();
  expect(screen.getByText('Handoff package archive summary request failed')).toBeInTheDocument();
  expect(screen.getAllByText('demo-session-20260624T003000Z')).toHaveLength(3);
});

test('shows handoff readiness gaps when launch context is missing', () => {
  render(
    <DemoSessionSnapshotPanel
      snapshot={{
        ...snapshot,
        evidenceBundle: {
          ...snapshot.evidenceBundle,
          recentTask: null,
          recentPullRequestUrl: null
        },
        shareSummary: 'Status READY; recent task none; recent PR none.'
      }}
      preparedLaunchCommands={[]}
      archivedLaunchOutcomes={[]}
      handoffReadiness={missingHandoffReadiness}
      archives={[]}
      handoffPackageArchives={[]}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo session snapshot' });
  expect(within(panel).getByText('Handoff readiness')).toBeInTheDocument();
  expect(within(panel).getAllByText('Needs attention').length).toBeGreaterThanOrEqual(1);
  expect(
    within(panel).getByText('No recent completed task is available in the session snapshot.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('No prepared launch command was captured in this browser session.')).toBeInTheDocument();
  expect(within(panel).getByText('Complete the handoff readiness checks that need attention before sharing the package.')).toBeInTheDocument();
  expect(within(panel).getByText('Next action: Use the dashboard launch command composer before handoff.')).toBeInTheDocument();
  expect(within(panel).getByText('0 commands / 0 outcomes')).toBeInTheDocument();
});

test('copies demo session report markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyReport = vi.fn().mockResolvedValue('# PatchPilot Demo Session Report\n\n- Status: `READY`');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      error={null}
      handoffPackageArchives={[]}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={onCopyReport}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy session report' }));

  expect(onCopyReport).toHaveBeenCalledWith(sessionReportInput);
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Session Report\n\n- Status: `READY`');
  expect(screen.getByText('Demo session report copied')).toBeInTheDocument();
});

test('copies demo handoff package markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyHandoffPackage = vi.fn().mockResolvedValue('# PatchPilot Demo Handoff Package');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      error={null}
      handoffPackageArchives={[]}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={onCopyHandoffPackage}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy handoff package' }));

  expect(onCopyHandoffPackage).toHaveBeenCalledWith(sessionReportInput);
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Handoff Package');
  expect(screen.getByText('Demo handoff package copied')).toBeInTheDocument();
});

test('downloads demo session report markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Session Report'], { type: 'text/markdown;charset=UTF-8' });
  const onDownloadReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-session-report');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      error={null}
      handoffPackageArchives={[]}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={onDownloadReport}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download session report' }));

  expect(onDownloadReport).toHaveBeenCalledWith(sessionReportInput);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-session-report');
  expect(screen.getByText('Demo session report downloaded')).toBeInTheDocument();
});

test('downloads demo handoff package markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Package'], { type: 'text/markdown;charset=UTF-8' });
  const onDownloadHandoffPackage = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      error={null}
      handoffPackageArchives={[]}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={onDownloadHandoffPackage}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download handoff package' }));

  expect(onDownloadHandoffPackage).toHaveBeenCalledWith(sessionReportInput);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-package');
  expect(screen.getByText('Demo handoff package downloaded')).toBeInTheDocument();
});

test('copies demo handoff package archive summary markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy handoff archive summary' }));

  expect(writeText).toHaveBeenCalledWith('# PatchPilot Handoff Package Archive Summary\n\n- Status: `READY`');
  expect(screen.getByText('Handoff archive summary copied')).toBeInTheDocument();
});

test('copies demo handoff share checklist markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy handoff share checklist' }));

  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Handoff Share Checklist\n\n- Status: `READY`');
  expect(screen.getByText('Handoff share checklist copied')).toBeInTheDocument();
});

test('downloads demo handoff share checklist markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Checklist'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadHandoffShareChecklistReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-share-checklist');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={onDownloadHandoffShareChecklistReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download handoff share checklist' }));

  expect(onDownloadHandoffShareChecklistReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-share-checklist');
  expect(screen.getByText('Handoff share checklist downloaded')).toBeInTheDocument();
});

test('downloads demo handoff share center markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Center'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadHandoffShareCenterReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-share-center');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={onDownloadHandoffShareCenterReport}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download handoff share center' }));

  expect(onDownloadHandoffShareCenterReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-share-center');
  expect(screen.getByText('Handoff share center downloaded')).toBeInTheDocument();
});

test('copies demo handoff share instructions markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Copy handoff share instructions' }));

  expect(writeText).toHaveBeenCalledWith(handoffShareInstructions.markdownReport);
  expect(screen.getByText('Handoff share instructions copied')).toBeInTheDocument();
});

test('downloads demo handoff share instructions markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Instructions'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadHandoffShareInstructionsReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-share-instructions');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={onDownloadHandoffShareInstructionsReport}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download handoff share instructions' }));

  expect(onDownloadHandoffShareInstructionsReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-share-instructions');
  expect(screen.getByText('Handoff share instructions downloaded')).toBeInTheDocument();
});

test('records demo handoff share delivery receipt', async () => {
  const onCreateHandoffShareDeliveryReceipt = vi.fn().mockResolvedValue({
    ...handoffShareDeliveryReceipts[0],
    id: 'delivery-receipt-2',
    deliveryTarget: 'reviewer@example.com'
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      handoffShareDeliveryReceipts={handoffShareDeliveryReceipts}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onCreateHandoffShareDeliveryReceipt={onCreateHandoffShareDeliveryReceipt}
      onDownloadHandoffShareDeliveryReceiptReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.clear(screen.getByLabelText('Delivery target'));
  await userEvent.type(screen.getByLabelText('Delivery target'), 'reviewer@example.com');
  await userEvent.clear(screen.getByLabelText('Operator'));
  await userEvent.type(screen.getByLabelText('Operator'), 'local-operator');
  await userEvent.clear(screen.getByLabelText('Notes'));
  await userEvent.type(screen.getByLabelText('Notes'), 'Sent after the demo review.');
  await userEvent.click(screen.getByRole('button', { name: 'Record handoff share delivery receipt' }));

  const receiptInput = onCreateHandoffShareDeliveryReceipt.mock.calls[0][0] as DemoHandoffShareDeliveryReceiptInput;
  expect(receiptInput.deliveryChannel).toBe('email');
  expect(receiptInput.deliveryTarget).toBe('reviewer@example.com');
  expect(receiptInput.operator).toBe('local-operator');
  expect(receiptInput.notes).toBe('Sent after the demo review.');
  expect(receiptInput.deliveredAt).toMatch(/^\d{4}-\d{2}-\d{2}T/);
  expect(screen.getByText('Handoff share delivery receipt recorded')).toBeInTheDocument();
});

test('downloads demo handoff share delivery receipt markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Share Delivery Receipt'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadHandoffShareDeliveryReceiptReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-share-delivery-receipt');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      handoffShareChecklist={handoffShareChecklist}
      handoffShareCenter={handoffShareCenter}
      handoffShareInstructions={handoffShareInstructions}
      handoffShareDeliveryReceipts={handoffShareDeliveryReceipts}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onCreateHandoffShareDeliveryReceipt={vi.fn()}
      onDownloadHandoffShareDeliveryReceiptReport={onDownloadHandoffShareDeliveryReceiptReport}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', {
    name: 'Download handoff share delivery receipt delivery-receipt-1'
  }));

  expect(onDownloadHandoffShareDeliveryReceiptReport).toHaveBeenCalledWith('delivery-receipt-1');
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-share-delivery-receipt');
  expect(screen.getByText('Handoff share delivery receipt downloaded')).toBeInTheDocument();
});

test('downloads demo handoff package archive summary markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Handoff Package Archive Summary'], {
    type: 'text/markdown;charset=UTF-8'
  });
  const onDownloadHandoffPackageArchiveSummaryReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-archive-summary');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveSummary={handoffPackageArchiveSummary}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={onDownloadHandoffPackageArchiveSummaryReport}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download handoff archive summary' }));

  expect(onDownloadHandoffPackageArchiveSummaryReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-archive-summary');
  expect(screen.getByText('Handoff archive summary downloaded')).toBeInTheDocument();
});

test('archives current demo session and copies archived report markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onArchiveSession = vi.fn().mockResolvedValue(archives[0]);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={archives}
      error={null}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={onArchiveSession}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Archive session' }));
  await userEvent.click(screen.getByRole('button', { name: 'Copy archived session report archive-1' }));

  expect(onArchiveSession).toHaveBeenCalledWith(sessionReportInput);
  expect(screen.getByText('Demo session archived')).toBeInTheDocument();
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Session Report\n\n- Status: `READY`');
  expect(screen.getByText('Archived session report copied')).toBeInTheDocument();
});

test('archives current demo handoff package and copies archived package markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onArchiveHandoffPackage = vi.fn().mockResolvedValue(handoffPackageArchives[0]);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={onArchiveHandoffPackage}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Archive handoff package' }));
  await userEvent.click(screen.getByRole('button', { name: 'Copy archived handoff package handoff-archive-1' }));

  expect(onArchiveHandoffPackage).toHaveBeenCalledWith(sessionReportInput);
  expect(screen.getByText('Demo handoff package archived')).toBeInTheDocument();
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Handoff Package\n\n- Status: `READY`');
  expect(screen.getByText('Archived handoff package copied')).toBeInTheDocument();
});

test('downloads archived demo session report markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Session Report'], { type: 'text/markdown;charset=UTF-8' });
  const onDownloadArchiveReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-session-archive-report');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={[]}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={archives}
      error={null}
      handoffPackageArchives={handoffPackageArchives}
      handoffPackageArchiveError={null}
      archiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={onDownloadArchiveReport}
      onDownloadHandoffPackageArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download archived session report archive-1' }));

  expect(onDownloadArchiveReport).toHaveBeenCalledWith('archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-session-archive-report');
  expect(screen.getByText('Archived session report downloaded')).toBeInTheDocument();
});

test('downloads archived demo handoff package markdown', async () => {
  const reportBlob = new Blob(['# PatchPilot Demo Handoff Package'], { type: 'text/markdown;charset=UTF-8' });
  const onDownloadHandoffPackageArchiveReport = vi.fn().mockResolvedValue(reportBlob);
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:demo-handoff-package-archive-report');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', {
    ...globalThis.URL,
    createObjectURL,
    revokeObjectURL
  });

  render(
    <DemoSessionSnapshotPanel
      snapshot={snapshot}
      preparedLaunchCommands={preparedLaunchCommands}
      archivedLaunchOutcomes={archivedLaunchOutcomes}
      handoffReadiness={handoffReadiness}
      archives={[]}
      handoffPackageArchives={handoffPackageArchives}
      error={null}
      archiveError={null}
      handoffPackageArchiveError={null}
      onCopyReport={vi.fn()}
      onDownloadReport={vi.fn()}
      onArchiveSession={vi.fn()}
      onCopyHandoffPackage={vi.fn()}
      onDownloadHandoffPackage={vi.fn()}
      onArchiveHandoffPackage={vi.fn()}
      onDownloadArchiveReport={vi.fn()}
      onDownloadHandoffPackageArchiveReport={onDownloadHandoffPackageArchiveReport}
      onDownloadHandoffPackageArchiveSummaryReport={vi.fn()}
      onDownloadHandoffShareCenterReport={vi.fn()}
      onDownloadHandoffShareInstructionsReport={vi.fn()}
      onDownloadHandoffShareChecklistReport={vi.fn()}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download archived handoff package handoff-archive-1' }));

  expect(onDownloadHandoffPackageArchiveReport).toHaveBeenCalledWith('handoff-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(reportBlob);
  expect(click).toHaveBeenCalledTimes(1);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:demo-handoff-package-archive-report');
  expect(screen.getByText('Archived handoff package downloaded')).toBeInTheDocument();
});
