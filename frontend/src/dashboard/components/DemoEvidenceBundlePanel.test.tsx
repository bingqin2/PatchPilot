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
  taskEvidenceAcceptanceCertificateEvidence: {
    status: 'READY',
    archived: true,
    certified: true,
    summary: 'Latest task evidence acceptance certificate archive is certified and ready.',
    nextAction: 'Use the archived task evidence acceptance certificate as task-level review proof.',
    archiveCount: 1,
    latestArchiveId: 'task-evidence-certificate-archive-1',
    latestCloseoutArchiveId: 'task-evidence-closeout-archive-1',
    latestEvidenceArchiveId: 'task-evidence-archive-1',
    latestDeliveryReceiptId: 'task-evidence-receipt-1',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestArchivedAt: '2026-06-24T09:30:00Z',
    downloadActions: [
      'Download task evidence acceptance certificate archive task-evidence-certificate-archive-1.',
      'Download linked task evidence acceptance closeout archive task-evidence-closeout-archive-1.'
    ]
  },
  finalHandoffReportPackageArchiveEvidence: {
    status: 'READY',
    archived: true,
    downloadReady: true,
    summary: 'Latest final handoff report package archive is download-ready and ready.',
    nextAction: 'Use the archived final handoff report package as the post-demo closeout proof.',
    archiveCount: 1,
    latestArchiveId: 'final-handoff-package-archive-1',
    latestHandoffArchiveId: 'handoff-archive-1',
    latestSessionId: 'demo-session-20260624T003000Z',
    latestDeliveryReceiptId: 'delivery-receipt-1',
    taskCertificateArchiveId: 'task-evidence-certificate-archive-1',
    taskCertificateReady: true,
    latestArchivedAt: '2026-06-24T11:30:00Z',
    downloadActions: [
      'Download final handoff report package archive final-handoff-package-archive-1.',
      'Download linked handoff package archive handoff-archive-1.'
    ]
  },
  finalAcceptanceShareFinalization: {
    status: 'READY',
    finalized: true,
    summary: 'Final demo acceptance share package is finalized with a fresh delivery receipt.',
    nextAction: 'Use the finalization report as the external-review acceptance delivery record.',
    latestArchiveId: 'final-acceptance-share-package-archive-1',
    latestTaskId: 'task-2',
    latestDeliveryReceiptId: 'final-acceptance-delivery-receipt-1',
    latestDeliveryTarget: 'reviewer@example.com',
    latestDeliveryChannel: 'email',
    latestDeliveredAt: '2026-06-29T03:05:00Z',
    deliveryReceiptFreshness: 'FRESH',
    deliveryReceiptFresh: true,
    deliveryReceiptFreshnessSummary: 'Latest delivery receipt matches the current final acceptance share package archive.',
    checks: [
      {
        name: 'Final acceptance delivery evidence',
        status: 'READY',
        summary: 'Finalization report is ready as the external-review acceptance record.',
        nextAction: 'Download the finalization report.'
      }
    ],
    evidenceNotes: [
      'Latest delivery receipt final-acceptance-delivery-receipt-1 is fresh for final-acceptance-share-package-archive-1.'
    ],
    markdownReport: '# PatchPilot Final Demo Acceptance Share Finalization Gate',
    generatedAt: '2026-06-29T03:30:00Z'
  },
  finalAcceptanceCompletionCloseoutEvidence: {
    status: 'READY',
    closed: true,
    summary:
      'PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.',
    nextAction: 'Use this closeout report as the final external-review completion record.',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
    latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
    latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    latestDeliveryTarget: 'reviewer@example.com',
    latestDeliveryChannel: 'email',
    latestDeliveredAt: '2026-06-29T03:45:00Z',
    deliveryReceiptFreshness: 'FRESH',
    checks: [
      {
        name: 'Completion evidence delivery',
        status: 'READY',
        summary: 'Completion closeout can be used as the final external-review record.',
        nextAction: 'No action needed.'
      }
    ],
    evidenceNotes: [
      'Final acceptance completion archive final-acceptance-completion-archive-1 has a fresh evidence delivery receipt.'
    ],
    downloadActions: [
      'Download final acceptance completion closeout report.',
      'Download final acceptance completion evidence bundle.'
    ],
    sideEffectContract: 'Final acceptance completion closeout is read-only and does not mutate tasks, Git, or GitHub.',
    markdownReport: '# PatchPilot Final Acceptance Completion Closeout',
    generatedAt: '2026-06-29T04:00:00Z'
  },
  finalAcceptanceCompletionCloseoutArchiveEvidence: {
    status: 'READY',
    archived: true,
    closed: true,
    summary: 'Latest final acceptance completion closeout archive is closed and ready.',
    nextAction: 'Use the archived final acceptance completion closeout as the frozen external-review completion record.',
    archiveCount: 1,
    latestArchiveId: 'final-acceptance-completion-closeout-archive-1',
    latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
    latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestArchivedAt: '2026-06-29T04:15:00Z',
    downloadActions: [
      'Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.',
      'Download linked final acceptance completion archive final-acceptance-completion-archive-1.'
    ]
  },
  finalExternalReviewEvidencePackage: {
    status: 'READY',
    readyForExternalReview: true,
    summary: 'PatchPilot final external-review evidence package is ready.',
    nextAction: 'Share this package with reviewers as the frozen external-review record.',
    finalAcceptanceSummaryStatus: 'READY',
    finalAcceptanceShareFinalizationStatus: 'READY',
    completionEvidenceBundleStatus: 'READY',
    completionDeliveryFinalizationStatus: 'READY',
    completionCloseoutStatus: 'READY',
    closeoutArchiveStatus: 'READY',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    finalAcceptanceSharePackageArchiveId: 'final-acceptance-share-package-archive-1',
    completionArchiveId: 'final-acceptance-completion-archive-1',
    completionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    closeoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
    deliveryTarget: 'reviewer@example.com',
    deliveryChannel: 'email',
    deliveredAt: '2026-06-29T03:45:00Z',
    deliveryReceiptFreshness: 'FRESH',
    closeoutArchivedAt: '2026-06-29T04:15:00Z',
    generatedAt: '2026-06-29T04:30:00Z',
    checks: [
      {
        name: 'Frozen closeout archive',
        status: 'READY',
        summary: 'Frozen closeout archive final-acceptance-completion-closeout-archive-1 is closed.',
        nextAction: 'No action needed.'
      }
    ],
    evidenceNotes: ['Frozen closeout archive final-acceptance-completion-closeout-archive-1 is READY and closed.'],
    downloadActions: [
      'Download final external-review evidence package.',
      'Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.'
    ],
    sideEffectContract: 'GET /api/demo/final-external-review-evidence-package is read-only.',
    markdownReport: '# PatchPilot Final External Review Evidence Package'
  },
  finalExternalReviewEvidencePackageArchiveEvidence: {
    status: 'READY',
    archived: true,
    readyForExternalReview: true,
    summary: 'Latest final external-review evidence package archive is ready for external review.',
    nextAction: 'Use the archived final external-review evidence package as the frozen reviewer-facing record.',
    archiveCount: 1,
    latestArchiveId: 'final-external-review-package-archive-1',
    latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
    latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
    latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestArchivedAt: '2026-06-29T04:45:00Z',
    downloadActions: [
      'Download final external-review evidence package archive final-external-review-package-archive-1.'
    ]
  },
  finalExternalReviewEvidencePackageDeliveryReceiptEvidence: {
    status: 'READY',
    recorded: true,
    fresh: true,
    freshness: 'FRESH',
    summary: 'Latest final external-review package delivery receipt is fresh.',
    nextAction: 'Use the delivery receipt as proof that the frozen final external-review package was shared.',
    receiptCount: 1,
    latestReceiptId: 'final-external-review-package-delivery-receipt-1',
    latestPackageArchiveId: 'final-external-review-package-archive-1',
    latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
    latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
    latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestDeliveryTarget: 'reviewer@example.com',
    latestDeliveryChannel: 'email',
    latestDeliveredAt: '2026-06-29T05:00:00Z',
    downloadActions: [
      'Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1.'
    ]
  },
  finalExternalReviewEvidencePackageDeliveryFinalization: {
    status: 'READY',
    finalized: true,
    summary: 'Final external-review package delivery is finalized with a fresh package delivery receipt.',
    nextAction: 'Use the finalization report as proof that the frozen external-review package was delivered.',
    latestArchiveId: 'final-external-review-package-archive-1',
    latestDeliveryReceiptId: 'final-external-review-package-delivery-receipt-1',
    latestCloseoutArchiveId: 'final-acceptance-completion-closeout-archive-1',
    latestCompletionArchiveId: 'final-acceptance-completion-archive-1',
    latestCompletionEvidenceDeliveryReceiptId: 'final-acceptance-completion-evidence-delivery-receipt-1',
    latestTaskId: 'task-2',
    latestPullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
    latestDeliveryTarget: 'reviewer@example.com',
    latestDeliveryChannel: 'email',
    latestDeliveredAt: '2026-06-29T05:00:00Z',
    deliveryReceiptFreshness: 'FRESH',
    deliveryReceiptFresh: true,
    deliveryReceiptFreshnessSummary:
      'Latest package delivery receipt matches the current frozen final external-review package.',
    checks: [
      {
        name: 'Final package delivery receipt',
        status: 'READY',
        summary: 'Package delivery receipt final-external-review-package-delivery-receipt-1 matches the archive.',
        nextAction: 'No action needed.'
      }
    ],
    evidenceNotes: [
      'Final external-review package archive final-external-review-package-archive-1 has a fresh package delivery receipt.'
    ],
    downloadActions: ['Download final external-review package delivery finalization report.'],
    sideEffectContract:
      'GET /api/demo/final-external-review-evidence-package/delivery-finalization is read-only.',
    markdownReport: '# PatchPilot Final External Review Package Delivery Finalization',
    generatedAt: '2026-06-29T05:10:00Z'
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
  expect(within(panel).getAllByText('demo-session-20260624T003000Z').length).toBeGreaterThanOrEqual(1);
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
  expect(within(panel).getByText('Latest launch acceptance certificate archive is certified and ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived launch acceptance certificate as the external-review launch record.')
  ).toBeInTheDocument();
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
  expect(within(panel).getByText('Task evidence acceptance certificate')).toBeInTheDocument();
  expect(within(panel).getAllByText('Certified archive')).toHaveLength(2);
  expect(within(panel).getByText('Latest task evidence acceptance certificate archive is certified and ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived task evidence acceptance certificate as task-level review proof.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('1 certificate archives').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('task-evidence-certificate-archive-1').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('task-evidence-closeout-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('task-evidence-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('task-evidence-receipt-1')).toBeInTheDocument();
  expect(within(panel).getAllByText('Task task-2').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('link', { name: 'Open task certificate Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(
    within(panel).getByText('Download task evidence acceptance certificate archive task-evidence-certificate-archive-1.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Download linked task evidence acceptance closeout archive task-evidence-closeout-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Final handoff report package archive')).toBeInTheDocument();
  expect(within(panel).getByText('Download-ready archive')).toBeInTheDocument();
  expect(
    within(panel).getByText('Latest final handoff report package archive is download-ready and ready.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived final handoff report package as the post-demo closeout proof.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 final package archives')).toBeInTheDocument();
  expect(within(panel).getByText('final-handoff-package-archive-1')).toBeInTheDocument();
  expect(within(panel).getAllByText('handoff-archive-1').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('delivery-receipt-1').length).toBeGreaterThanOrEqual(3);
  expect(within(panel).getAllByText('task-evidence-certificate-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(
    within(panel).getByText('Download final handoff report package archive final-handoff-package-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Download linked handoff package archive handoff-archive-1.')).toBeInTheDocument();
  expect(within(panel).getByText('Final acceptance delivery')).toBeInTheDocument();
  expect(within(panel).getByText('Final demo acceptance share package is finalized with a fresh delivery receipt.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the finalization report as the external-review acceptance delivery record.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('final-acceptance-share-package-archive-1')).toBeInTheDocument();
  expect(within(panel).getByText('final-acceptance-delivery-receipt-1')).toBeInTheDocument();
  expect(within(panel).getAllByText('Task task-2').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('email - reviewer@example.com').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Final acceptance completion closeout')).toBeInTheDocument();
  expect(within(panel).getByText('Closed')).toBeInTheDocument();
  expect(
    within(panel).getByText(
      'PatchPilot final acceptance completion is closed with accepted certificates, finalized sharing, and fresh completion delivery proof.'
    )
  ).toBeInTheDocument();
  expect(within(panel).getByText('Use this closeout report as the final external-review completion record.')).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-completion-archive-1').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('final-acceptance-completion-evidence-delivery-receipt-1').length)
    .toBeGreaterThanOrEqual(1);
  expect(within(panel).getByRole('link', { name: 'Open final acceptance completion Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(within(panel).getByText('Download final acceptance completion closeout report.')).toBeInTheDocument();
  expect(within(panel).getByText('Download final acceptance completion evidence bundle.')).toBeInTheDocument();
  expect(within(panel).getByText('Final acceptance completion closeout archive')).toBeInTheDocument();
  expect(within(panel).getByText('Closed archive')).toBeInTheDocument();
  expect(within(panel).getByText('Latest final acceptance completion closeout archive is closed and ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived final acceptance completion closeout as the frozen external-review completion record.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 closeout archive snapshots')).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-completion-closeout-archive-1').length)
    .toBeGreaterThanOrEqual(1);
  expect(within(panel).getAllByText('final-acceptance-completion-archive-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('final-acceptance-completion-evidence-delivery-receipt-1').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('link', { name: 'Open archived final acceptance completion Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(
    within(panel).getAllByText('Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.').length
  ).toBeGreaterThanOrEqual(1);
  expect(
    within(panel).getByText('Download linked final acceptance completion archive final-acceptance-completion-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Final external-review evidence package')).toBeInTheDocument();
  expect(within(panel).getByText('Ready for external review')).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot final external-review evidence package is ready.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Share this package with reviewers as the frozen external-review record.')
  ).toBeInTheDocument();
  expect(within(panel).getAllByText('final-acceptance-completion-closeout-archive-1').length)
    .toBeGreaterThanOrEqual(2);
  expect(within(panel).getAllByText('final-acceptance-completion-archive-1').length).toBeGreaterThanOrEqual(3);
  expect(within(panel).getAllByText('final-acceptance-completion-evidence-delivery-receipt-1').length)
    .toBeGreaterThanOrEqual(3);
  expect(within(panel).getByRole('link', { name: 'Open final external-review Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(within(panel).getByText('Download final external-review evidence package.')).toBeInTheDocument();
  expect(
    within(panel).getAllByText('Download final acceptance completion closeout archive final-acceptance-completion-closeout-archive-1.').length
  ).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Final external-review package archive')).toBeInTheDocument();
  expect(within(panel).getByText('Frozen archive')).toBeInTheDocument();
  expect(
    within(panel).getByText('Latest final external-review evidence package archive is ready for external review.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the archived final external-review evidence package as the frozen reviewer-facing record.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 final external-review package archives')).toBeInTheDocument();
  expect(within(panel).getAllByText('final-external-review-package-archive-1').length)
    .toBeGreaterThanOrEqual(2);
  expect(within(panel).getByRole('link', { name: 'Open archived final external-review Pull Request' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  expect(
    within(panel).getByText('Download final external-review evidence package archive final-external-review-package-archive-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Final external-review package delivery')).toBeInTheDocument();
  expect(within(panel).getByText('Fresh receipt')).toBeInTheDocument();
  expect(within(panel).getByText('Latest final external-review package delivery receipt is fresh.')).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the delivery receipt as proof that the frozen final external-review package was shared.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('1 final external-review package delivery receipts')).toBeInTheDocument();
  expect(
    within(panel).getAllByText('final-external-review-package-delivery-receipt-1').length
  ).toBeGreaterThanOrEqual(1);
  expect(
    within(panel).getByText('Download final external-review package delivery receipt final-external-review-package-delivery-receipt-1.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Final external-review package delivery finalization')).toBeInTheDocument();
  expect(within(panel).getAllByText('Finalized').length).toBeGreaterThanOrEqual(4);
  expect(
    within(panel).getByText('Final external-review package delivery is finalized with a fresh package delivery receipt.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Use the finalization report as proof that the frozen external-review package was delivered.')
  ).toBeInTheDocument();
  expect(
    within(panel).getByText('Latest package delivery receipt matches the current frozen final external-review package.')
  ).toBeInTheDocument();
  expect(
    within(panel).getAllByText('final-external-review-package-delivery-receipt-1').length
  ).toBeGreaterThanOrEqual(2);
  expect(
    within(panel).getByText('Download final external-review package delivery finalization report.')
  ).toBeInTheDocument();
  expect(within(panel).getByText('Handoff share delivery')).toBeInTheDocument();
  expect(within(panel).getAllByText('Fresh').length).toBeGreaterThanOrEqual(2);
  expect(within(panel).getByText('Handoff finalization')).toBeInTheDocument();
  expect(within(panel).getAllByText('Finalized').length).toBeGreaterThanOrEqual(3);
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
  const legacyBundle = {
    ...bundle,
    launchAcceptanceCertificateEvidence: undefined,
    taskEvidenceAcceptanceCertificateEvidence: undefined,
    finalHandoffReportPackageArchiveEvidence: undefined,
    finalAcceptanceShareFinalization: undefined,
    finalAcceptanceCompletionCloseoutEvidence: undefined,
    finalAcceptanceCompletionCloseoutArchiveEvidence: undefined,
    finalExternalReviewEvidencePackage: undefined,
    finalExternalReviewEvidencePackageArchiveEvidence: undefined,
    finalExternalReviewEvidencePackageDeliveryReceiptEvidence: undefined,
    finalExternalReviewEvidencePackageDeliveryFinalization: undefined
  } as unknown as DemoEvidenceBundle;

  render(<DemoEvidenceBundlePanel bundle={legacyBundle} error={null} onCopyRunbook={vi.fn()} />);

  expect(screen.getByText('Launch acceptance certificate')).toBeInTheDocument();
  expect(screen.getAllByText('Needs attention').length).toBeGreaterThanOrEqual(2);
  expect(screen.getByText('No launch acceptance certificate archive is available.')).toBeInTheDocument();
  expect(
    screen.getByText('Archive the final launch acceptance certificate after the launch acceptance closeout is certified.')
  ).toBeInTheDocument();
  expect(screen.getByText('No certificate Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Task evidence acceptance certificate')).toBeInTheDocument();
  expect(screen.getByText('No task evidence acceptance certificate archive is available.')).toBeInTheDocument();
  expect(
    screen.getByText('Archive a certified task evidence acceptance certificate after final task evidence closeout.')
  ).toBeInTheDocument();
  expect(screen.getByText('No task certificate Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Final handoff report package archive')).toBeInTheDocument();
  expect(screen.getByText('No final handoff report package archive is available.')).toBeInTheDocument();
  expect(
    screen.getByText('Archive the final handoff report package after the post-demo handoff package is finalized.')
  ).toBeInTheDocument();
  expect(screen.getByText('Final acceptance delivery')).toBeInTheDocument();
  expect(screen.getByText('Final acceptance share package is not finalized.')).toBeInTheDocument();
  expect(
    screen.getByText('Archive and deliver the final acceptance share package before using the evidence bundle as external-review proof.')
  ).toBeInTheDocument();
  expect(screen.getByText('Final acceptance completion closeout')).toBeInTheDocument();
  expect(screen.getByText('Final acceptance completion closeout is not available.')).toBeInTheDocument();
  expect(
    screen.getByText('Close the final acceptance completion delivery loop before using the evidence bundle as final external-review proof.')
  ).toBeInTheDocument();
  expect(screen.getByText('No final acceptance completion Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Final acceptance completion closeout archive')).toBeInTheDocument();
  expect(screen.getByText('No final acceptance completion closeout archive is available.')).toBeInTheDocument();
  expect(screen.getAllByText('Archive the final acceptance completion closeout after it is READY and closed.').length)
    .toBeGreaterThanOrEqual(1);
  expect(screen.getByText('No archived final acceptance completion Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Final external-review evidence package')).toBeInTheDocument();
  expect(screen.getByText('Final external-review evidence package is not available in the top-level evidence bundle.'))
    .toBeInTheDocument();
  expect(screen.getByText('Load the final external-review evidence package before sharing demo evidence externally.'))
    .toBeInTheDocument();
  expect(screen.getByText('No final external-review Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Final external-review package archive')).toBeInTheDocument();
  expect(screen.getByText('No final external-review evidence package archive is available.')).toBeInTheDocument();
  expect(
    screen.getAllByText('Archive the final external-review evidence package after it is READY.').length
  ).toBeGreaterThanOrEqual(1);
  expect(screen.getByText('No archived final external-review Pull Request')).toBeInTheDocument();
  expect(screen.getByText('Final external-review package delivery')).toBeInTheDocument();
  expect(screen.getByText('No final external-review package delivery receipt is available.')).toBeInTheDocument();
  expect(
    screen.getByText('Share the latest final external-review package archive and record a delivery receipt.')
  ).toBeInTheDocument();
  expect(screen.getByText('No final external-review package delivery receipt')).toBeInTheDocument();
  expect(screen.getByText('Final external-review package delivery finalization')).toBeInTheDocument();
  expect(screen.getByText('Final external-review package delivery is not finalized.')).toBeInTheDocument();
  expect(screen.getByText('Record a fresh package delivery receipt for the latest frozen final external-review package.'))
    .toBeInTheDocument();
  expect(screen.getByText('No finalized final external-review package delivery receipt')).toBeInTheDocument();
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
