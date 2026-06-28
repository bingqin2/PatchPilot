import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoEvidenceBundle } from '../../types';
import { DemoEvidenceBundlePanel } from './DemoEvidenceBundlePanel';

const bundle: DemoEvidenceBundle = {
  status: 'NEEDS_ATTENTION',
  summary: 'Demo evidence bundle needs attention.',
  summaryCounts: {
    adapterFixtureCount: 12,
    failedAdapterFixtureCount: 1,
    recentTaskCount: 3,
    activeQuarantineCount: 1,
    recentPullRequestAvailable: true
  },
  readiness: {
    status: 'READY',
    summary: 'PatchPilot is ready for a controlled demo.',
    checks: [],
    nextActions: []
  },
  smokeChecklist: {
    status: 'NEEDS_ATTENTION',
    summary: 'Live demo smoke checklist needs attention.',
    steps: [],
    nextActions: ['Fix webhook delivery before demo.']
  },
  configuration: null,
  adapterFixtures: {
    totalCount: 12,
    failedCount: 1
  },
  evaluationRunReadiness: {
    status: 'READY',
    latestRunId: 'evaluation-run-2',
    previousRunId: 'evaluation-run-1',
    passedDelta: 1,
    failedDelta: 0,
    skippedDelta: 0,
    coveredLanguages: ['java', 'python'],
    coveredBuildSystems: ['maven', 'pytest'],
    safetyRejectionCategories: ['DANGEROUS_REQUEST', 'SECRET_EXFILTRATION'],
    sideEffectContract: 'Evaluation run readiness summary reads archived full evaluation runs only; it does not create tasks, call the model, mutate Git, or write to GitHub.',
    nextAction: 'Full evaluation run archive is ready; use it as current demo evidence.'
  },
  queueSummary: {
    totalCount: 3,
    pendingCount: 1,
    availablePendingCount: 1,
    delayedPendingCount: 0,
    runningCount: 0,
    completedCount: 2,
    failedCount: 0,
    cancelledCount: 0
  },
  recentTask: {
    id: 'task-2',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    installationId: 0,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md demo',
    deliveryId: 'delivery-task-2',
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
  webhookSetupReadiness: {
    status: 'READY',
    secretConfigured: true,
    publicUrlReady: true,
    publicBaseUrl: 'https://demo.trycloudflare.com',
    payloadUrl: 'https://demo.trycloudflare.com/api/github/webhook',
    healthUrl: 'https://demo.trycloudflare.com/health',
    latestDeliveryStatus: 'TASK_CREATED',
    latestDeliveryId: 'delivery-1',
    redeliveryRecommended: false,
    summary: 'Webhook setup is ready for GitHub deliveries.',
    nextActions: ['Use the payload URL in GitHub Webhooks and continue the live demo.'],
    checkedAt: '2026-06-27T01:00:00Z',
    markdownReport: '# PatchPilot Webhook Setup Readiness'
  },
  latestWebhookDelivery: {
    id: 'delivery-1',
    deliveryId: 'delivery-1',
    event: 'issue_comment.created',
    status: 'TASK_CREATED',
    taskId: 'task-2',
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md demo',
    message: 'Webhook created a task.',
    redeliveryRecommended: false,
    operatorAction: 'Task was created.',
    outcomeType: 'TASK',
    outcomeId: 'task-2',
    outcomeUrl: '/tasks/task-2',
    createdAt: '2026-06-24T00:00:00Z'
  },
  recentWebhookDeliveries: [
    {
      id: 'delivery-1',
      deliveryId: 'delivery-1',
      event: 'issue_comment.created',
      status: 'TASK_CREATED',
      taskId: 'task-2',
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: '/agent fix replace docs/demo.md demo',
      message: 'Webhook created a task.',
      redeliveryRecommended: false,
      operatorAction: 'Task was created.',
      outcomeType: 'TASK',
      outcomeId: 'task-2',
      outcomeUrl: '/tasks/task-2',
      createdAt: '2026-06-24T00:00:00Z'
    },
    {
      id: 'delivery-2',
      deliveryId: 'delivery-2',
      event: 'issue_comment.created',
      status: 'REJECTED',
      taskId: null,
      repositoryOwner: 'bingqin2',
      repositoryName: 'PatchPilot',
      issueNumber: 1,
      triggerUser: 'bingqin2',
      triggerComment: 'please do stuff',
      message: 'Webhook trigger was rejected before task creation.',
      redeliveryRecommended: false,
      operatorAction: 'Review rejected trigger evidence.',
      outcomeType: 'REJECTED_TRIGGER',
      outcomeId: 'rejected-1',
      outcomeUrl: '/rejected-triggers/rejected-1',
      createdAt: '2026-06-24T00:01:00Z'
    }
  ],
  rejectedTriggerSummary: {
    totalCount: 4,
    categoryCounts: [{ value: 'MODEL_REJECTED', count: 4 }],
    sourceCounts: [],
    triggerUserCounts: [],
    repositoryCounts: []
  },
  activeQuarantineCount: 1,
  handoffShareChecklistStatus: 'READY',
  handoffShareChecklistSummary: 'Latest handoff archive is ready to share.',
  handoffShareChecklistNextAction: 'Share the latest handoff package summary and archived package with the reviewer.',
  handoffShareCenterStatus: 'READY',
  handoffShareCenterSummary: 'Post-demo handoff package is ready to share.',
  handoffShareCenterNextAction: 'Download the package, archive summary, and share checklist before sending handoff evidence.',
  handoffShareCenterDownloadActions: [
    'Download handoff package archive handoff-archive-1.',
    'Download handoff package archive summary.',
    'Download handoff share checklist.',
    'Download handoff share delivery receipt delivery-receipt-1.'
  ],
  launchEvidenceShareCenterStatus: 'READY',
  launchEvidenceShareCenterReady: true,
  launchEvidenceShareCenterSummary: 'Latest archived launch evidence package is READY and can be shared.',
  launchEvidenceShareCenterNextAction: 'Download the archived launch evidence package and share it with reviewers.',
  launchEvidenceShareCenterArchiveCount: 1,
  launchEvidenceShareCenterLatestArchiveId: 'launch-evidence-archive-1',
  launchEvidenceShareCenterLatestSessionId: 'demo-session-20260624T003000Z',
  launchEvidenceShareCenterLatestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  launchEvidenceShareCenterDownloadActions: [
    'Download launch evidence package archive launch-evidence-archive-1.',
    'Download launch evidence share center report.',
    'Open Pull Request https://github.com/bingqin2/PatchPilot/pull/42 for review.',
    'Download launch evidence delivery receipt launch-delivery-receipt-1.'
  ],
  launchEvidenceFinalizationStatus: 'READY',
  launchEvidenceFinalized: true,
  launchEvidenceFinalizationSummary: 'Demo launch evidence is finalized with a fresh delivery receipt for the current archive.',
  launchEvidenceFinalizationNextAction: 'Use the finalization report as the launch evidence delivery acceptance record.',
  launchEvidenceFinalizationDeliveryReceiptFreshness: 'FRESH',
  launchEvidenceFinalizationDeliveryReceiptFresh: true,
  launchEvidenceFinalizationLatestDeliveryReceiptId: 'launch-delivery-receipt-1',
  launchAcceptanceCloseoutEvidence: {
    status: 'READY',
    archived: true,
    accepted: true,
    summary: 'Latest launch acceptance closeout archive is accepted and ready.',
    nextAction: 'Use the archived launch acceptance closeout as the final launch evidence record.',
    archiveCount: 1,
    latestArchiveId: 'launch-closeout-archive-1',
    latestEvidenceArchiveId: 'launch-evidence-archive-1',
    latestDeliveryReceiptId: 'launch-delivery-receipt-1',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestArchivedAt: '2026-06-24T08:00:00Z',
    downloadActions: [
      'Download launch acceptance closeout archive launch-closeout-archive-1.',
      'Download linked launch evidence archive launch-evidence-archive-1.'
    ]
  },
  launchAcceptanceCertificateEvidence: {
    status: 'READY',
    archived: true,
    certified: true,
    summary: 'Latest launch acceptance certificate archive is certified and ready.',
    nextAction: 'Use the archived launch acceptance certificate as the external-review launch record.',
    archiveCount: 1,
    latestArchiveId: 'launch-certificate-archive-1',
    latestCloseoutArchiveId: 'launch-closeout-archive-1',
    latestEvidenceArchiveId: 'launch-evidence-archive-1',
    latestDeliveryReceiptId: 'launch-delivery-receipt-1',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestArchivedAt: '2026-06-24T08:30:00Z',
    downloadActions: [
      'Download launch acceptance certificate archive launch-certificate-archive-1.',
      'Download linked launch acceptance closeout archive launch-closeout-archive-1.'
    ]
  },
  handoffShareDeliveryReceiptRecorded: true,
  handoffShareLatestDeliveryReceiptId: 'delivery-receipt-1',
  handoffShareLatestDeliveryTarget: 'maintainer@example.com',
  handoffShareLatestDeliveryChannel: 'email',
  handoffShareLatestDeliveredAt: '2026-06-24T06:05:00Z',
  handoffShareDeliveryReceiptFreshness: 'FRESH',
  handoffShareDeliveryReceiptFresh: true,
  handoffShareDeliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current handoff archive and session.',
  handoffFinalizationStatus: 'READY',
  handoffFinalized: true,
  handoffFinalizationSummary: 'Demo handoff is finalized with a fresh delivery receipt for the current archive.',
  handoffFinalizationNextAction: 'Use the finalization report as the post-demo delivery acceptance record.',
  handoffFinalizationDeliveryReceiptFreshness: 'FRESH',
  handoffFinalizationDeliveryReceiptFresh: true,
  handoffFinalizationLatestDeliveryReceiptId: 'delivery-receipt-1',
  generatedAt: '2026-06-24T00:10:00Z',
  nextActions: ['Fix failing adapter fixtures before a live demo.', 'Inspect active trigger quarantines before a live demo.']
};

test('summarizes demo evidence bundle for operators', () => {
  render(<DemoEvidenceBundlePanel bundle={bundle} error={null} onCopyRunbook={vi.fn()} />);

  const panel = screen.getByRole('region', { name: 'Demo evidence bundle' });
  expect(within(panel).getByRole('heading', { name: 'Demo evidence bundle' })).toBeInTheDocument();
  expect(within(panel).getByText('Needs attention')).toBeInTheDocument();
  expect(within(panel).getByText('Demo evidence bundle needs attention.')).toBeInTheDocument();
  expect(within(panel).getByText('12')).toBeInTheDocument();
  expect(within(panel).getByText('1 failed')).toBeInTheDocument();
  expect(within(panel).getByText('Recent PR available')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Open recent Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(within(panel).getByText('Latest webhook delivery')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup readiness')).toBeInTheDocument();
  expect(within(panel).getByText('Webhook setup is ready for GitHub deliveries.')).toBeInTheDocument();
  expect(within(panel).getByText('Full evaluation run readiness')).toBeInTheDocument();
  expect(within(panel).getByText('Latest evaluation run evaluation-run-2')).toBeInTheDocument();
  expect(within(panel).getByText('Previous evaluation run evaluation-run-1')).toBeInTheDocument();
  expect(within(panel).getByText('Deltas passed +1, failed 0, skipped 0')).toBeInTheDocument();
  expect(within(panel).getByText('Coverage java, python / maven, pytest')).toBeInTheDocument();
  expect(within(panel).getByText('Safety DANGEROUS_REQUEST, SECRET_EXFILTRATION')).toBeInTheDocument();
  expect(
    within(panel).getByText('Full evaluation run archive is ready; use it as current demo evidence.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Handoff share checklist')).toBeInTheDocument();
  expect(within(panel).getByText('Latest handoff archive is ready to share.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Share the latest handoff package summary and archived package with the reviewer.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Handoff share center')).toBeInTheDocument();
  expect(within(panel).getByText('Post-demo handoff package is ready to share.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Download the package, archive summary, and share checklist before sending handoff evidence.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Download handoff package archive handoff-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Download handoff package archive summary.')).toBeInTheDocument();
  expect(within(panel).getByText('Launch evidence share center')).toBeInTheDocument();
  expect(within(panel).getByText('Latest archived launch evidence package is READY and can be shared.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Download the archived launch evidence package and share it with reviewers.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-evidence-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('demo-session-20260624T003000Z')).toBeInTheDocument();
  expect(within(panel).getByText('Download launch evidence package archive launch-evidence-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Download launch evidence share center report.')).toBeInTheDocument();
  expect(within(panel).getByText('Launch evidence finalization')).toBeInTheDocument();
  expect(within(panel).getByText('Demo launch evidence is finalized with a fresh delivery receipt for the current archive.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the finalization report as the launch evidence delivery acceptance record.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Launch acceptance closeout')).toBeInTheDocument();
  expect(within(panel).getByText('Accepted archive')).toBeInTheDocument();
  expect(within(panel).getByText('Latest launch acceptance closeout archive is accepted and ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived launch acceptance closeout as the final launch evidence record.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 closeout archives')).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-closeout-archive-1').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('launch-evidence-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(
    within(panel).getByText('Download launch acceptance closeout archive launch-closeout-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Download linked launch evidence archive launch-evidence-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Launch acceptance certificate')).toBeInTheDocument();
  expect(within(panel).getByText('Certified archive')).toBeInTheDocument();
  expect(within(panel).getByText('Latest launch acceptance certificate archive is certified and ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived launch acceptance certificate as the external-review launch record.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 certificate archives')).toBeInTheDocument();
  expect(within(panel).getByText('launch-certificate-archive-1')).toBeInTheDocument();
  expect(within(panel).getAllByText('launch-closeout-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('link', { name: 'Open certificate Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(
    within(panel).getByText('Download launch acceptance certificate archive launch-certificate-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Download linked launch acceptance closeout archive launch-closeout-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Handoff share delivery')).toBeInTheDocument();
  expect(within(panel).getAllByText('Fresh').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Handoff finalization')).toBeInTheDocument();
  expect(within(panel).getAllByText('Finalized')).toHaveLength(2);
  expect(
    within(panel).getByText('Use the finalization report as the post-demo delivery acceptance record.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Latest delivery receipt matches the current handoff archive and session.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('email - maintainer@example.com')).toBeInTheDocument();
  expect(within(panel).getByText('https://demo.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
  expect(within(panel).getAllByText('delivery-1')).toHaveLength(2);
  expect(within(panel).getByText('Recent webhook delivery trail')).toBeInTheDocument();
  expect(within(panel).getByText('delivery-2')).toBeInTheDocument();
  expect(within(panel).getByText('REJECTED')).toBeInTheDocument();
  expect(within(panel).getByText('REJECTED_TRIGGER')).toBeInTheDocument();
  expect(within(panel).getByText('please do stuff')).toBeInTheDocument();
  expect(within(panel).getByText('Fix failing adapter fixtures before a live demo.')).toBeInTheDocument();
  expect(within(panel).getByText('Inspect active trigger quarantines before a live demo.')).toBeInTheDocument();
});

test('shows loading and API errors without hiding existing bundle data', () => {
  const { rerender } = render(<DemoEvidenceBundlePanel bundle={null} error={null} onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Demo evidence bundle has not loaded yet.')).toBeInTheDocument();

  rerender(<DemoEvidenceBundlePanel bundle={bundle} error="Backend request failed" onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Demo evidence bundle unavailable')).toBeInTheDocument();
  expect(screen.getByText('Backend request failed')).toBeInTheDocument();
  expect(screen.getByText('Demo evidence bundle needs attention.')).toBeInTheDocument();
});

test('renders missing certificate evidence for legacy bundle responses', () => {
  const legacyBundle = { ...bundle, launchAcceptanceCertificateEvidence: undefined } as unknown as DemoEvidenceBundle;

  render(<DemoEvidenceBundlePanel bundle={legacyBundle} error={null} onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Launch acceptance certificate')).toBeInTheDocument();
  expect(screen.getAllByText('Needs attention').length).toBeGreaterThanOrEqual(2);
  expect(screen.getByText('No launch acceptance certificate archive is available.')).toBeInTheDocument();
  expect(
    screen.getByText('Archive the final launch acceptance certificate after the launch acceptance closeout is certified.')
  ).toBeInTheDocument();
  expect(screen.getByText('No certificate Pull Request')).toBeInTheDocument();
});

test('copies demo runbook markdown', async () => {
  const writeText = vi.fn().mockResolvedValue(undefined);
  const onCopyRunbook = vi.fn().mockResolvedValue('# PatchPilot Demo Runbook\n\n- Status: `READY`');
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(<DemoEvidenceBundlePanel bundle={bundle} error={null} onCopyRunbook={onCopyRunbook} />);

  await userEvent.click(screen.getByRole('button', { name: 'Copy runbook' }));

  expect(onCopyRunbook).toHaveBeenCalledTimes(1);
  expect(writeText).toHaveBeenCalledWith('# PatchPilot Demo Runbook\n\n- Status: `READY`');
  expect(screen.getByText('Demo runbook copied')).toBeInTheDocument();
});
