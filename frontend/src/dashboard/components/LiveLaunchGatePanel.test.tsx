import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type {
  DemoLiveLaunchGate,
  DemoLiveTriggerLaunchPackage,
  DemoLiveTriggerLaunchPackageArchive,
  DemoLiveDemoEvidenceBundle,
  DemoLiveDemoEvidenceBundleArchive,
  DemoLiveDemoHandoffPackage,
  DemoLiveDemoHandoffDeliveryReceipt,
  DemoLiveDemoHandoffDeliveryFinalization,
  DemoLiveDemoHandoffDeliveryFinalizationArchive,
  DemoLiveDemoCompletionCertificate,
  DemoLiveDemoCompletionCertificateArchive,
  DemoLiveDemoArtifactChainReport,
  DemoLiveTriggerOutcomeCloseout,
  DemoLiveTriggerOutcomeCloseoutArchive
} from '../../types';
import { LiveLaunchGatePanel } from './LiveLaunchGatePanel';

const readyGate: DemoLiveLaunchGate = {
  status: 'READY',
  readyToPost: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-gate.md',
  summary: 'PatchPilot is ready for a live /agent fix launch.',
  nextActions: [
    'Post the exact /agent fix comment on the GitHub issue and watch webhook delivery, task execution, and Pull Request creation.'
  ],
  sideEffectContract: 'Read-only live launch gate: this endpoint does not create tasks.',
  launchReadiness: {
    status: 'READY',
    readyToLaunch: true,
    summary: 'Self-hosted launch readiness is READY',
    checks: [
      { name: 'runtime', status: 'READY', message: 'runtime ready', action: 'none' }
    ],
    nextActions: [],
    generatedAt: '2026-07-01T10:00:00Z',
    markdownReport: 'launch report'
  },
  webhookSetup: {
    status: 'READY',
    secretConfigured: true,
    publicUrlReady: true,
    publicBaseUrl: 'https://example.trycloudflare.com',
    payloadUrl: 'https://example.trycloudflare.com/api/github/webhook',
    healthUrl: 'https://example.trycloudflare.com/health',
    latestDeliveryStatus: 'TASK_CREATED',
    latestDeliveryId: 'delivery-1',
    redeliveryRecommended: false,
    summary: 'Webhook setup is READY',
    nextActions: [],
    checkedAt: '2026-07-01T10:00:00Z',
    markdownReport: 'webhook report'
  },
  livePublishPreflight: {
    status: 'READY',
    livePublishReady: true,
    tokenConfigured: true,
    repositoryConfigured: true,
    repository: 'bingqin2/PatchPilot',
    defaultBranch: 'main',
    patchpilotBranches: [],
    openPatchpilotPullRequests: [],
    summary: 'Live publish preflight is READY',
    nextAction: 'Live publish is ready.',
    sideEffectContract: 'publish preflight side effect contract',
    checks: [
      { name: 'publish', status: 'READY', summary: 'publish ready', nextAction: 'none' }
    ],
    evidenceNotes: ['publish evidence'],
    latencyMs: 10,
    checkedAt: '2026-07-01T10:00:00Z'
  },
  triggerDryRun: {
    status: 'WOULD_CREATE_TASK',
    wouldCreateTask: true,
    repository: 'bingqin2/PatchPilot',
    issueNumber: 1,
    issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md',
    summary: 'Live GitHub trigger dry run would create a PatchPilot task.',
    nextAction: 'Post this /agent fix comment on the GitHub issue when publish preflight is ready.',
    sideEffectContract: 'Read-only live trigger dry run: this endpoint does not create tasks.',
    evaluation: {
      status: 'WOULD_CREATE_TASK',
      source: 'ISSUE_COMMENT',
      wouldCreateTask: true,
      blockedReason: null,
      blockedCategory: null,
      safetyDecision: { allowed: true, reason: 'Accepted', category: 'UNKNOWN' },
      activeTaskDecision: { allowed: true, reason: 'No active task exists', category: 'UNKNOWN' },
      quarantineDecision: { allowed: true, reason: 'Not quarantined', category: 'UNKNOWN' },
      rateLimitDecision: { allowed: true, reason: 'Rate limit accepted', category: 'UNKNOWN' },
      triggerIntentDecision: { allowed: true, reason: 'Model accepted', category: 'UNKNOWN' },
      issueContextLoaded: true,
      nextAction: 'Create task is allowed.'
    }
  },
  checks: [
    {
      name: 'Self-hosted launch readiness',
      status: 'READY',
      message: 'Self-hosted launch readiness is READY',
      action: 'Ready.'
    },
    {
      name: 'Webhook setup',
      status: 'READY',
      message: 'Webhook setup is READY',
      action: 'Ready.'
    },
    {
      name: 'Live GitHub publish preflight',
      status: 'READY',
      message: 'Live publish preflight is READY',
      action: 'Live publish is ready.'
    },
    {
      name: 'Live trigger dry run',
      status: 'READY',
      message: 'Live GitHub trigger dry run would create a PatchPilot task.',
      action: 'Post this /agent fix comment on the GitHub issue when publish preflight is ready.'
    }
  ],
  generatedAt: '2026-07-01T10:00:00Z',
  markdownReport: '# PatchPilot Live Launch Gate\n\n- Status: READY'
};

const readyLaunchPackage: DemoLiveTriggerLaunchPackage = {
  status: 'READY',
  readyToPost: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  summary: 'PatchPilot is ready for the operator to post the live trigger.',
  operatorHandoffArchiveId: 'operator-archive-1',
  operatorHandoffArchiveReady: true,
  operatorHandoffArchivedAt: '2026-07-02T00:00:00Z',
  liveLaunchGateStatus: 'READY',
  liveLaunchGateReady: true,
  evidenceNotes: ['Latest external exposure operator handoff archive operator-archive-1 is ready.'],
  nextActions: [
    'Post `/agent fix touch docs/live-package.md` on https://github.com/bingqin2/PatchPilot/issues/1.',
    'After GitHub delivers the webhook, watch the task, Pull Request, and launch outcome tracker.'
  ],
  sideEffectContract: 'Read-only live trigger launch package: this endpoint does not create tasks.',
  liveLaunchGate: readyGate,
  generatedAt: '2026-07-02T00:00:00Z',
  markdownReport: '# PatchPilot Live Trigger Launch Package\n\n- Status: `READY`'
};

const readyLaunchPackageArchive: DemoLiveTriggerLaunchPackageArchive = {
  id: 'launch-package-archive-1',
  status: 'READY',
  readyToPost: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  summary: 'PatchPilot is ready for the operator to post the live trigger.',
  operatorHandoffArchiveId: 'operator-archive-1',
  operatorHandoffArchiveReady: true,
  operatorHandoffArchivedAt: '2026-07-02T00:00:00Z',
  liveLaunchGateStatus: 'READY',
  liveLaunchGateReady: true,
  evidenceNotes: ['Latest external exposure operator handoff archive operator-archive-1 is ready.'],
  nextActions: [
    'Post `/agent fix touch docs/live-package.md` on https://github.com/bingqin2/PatchPilot/issues/1.'
  ],
  sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
  packageGeneratedAt: '2026-07-02T00:00:00Z',
  archivedAt: '2026-07-02T00:00:05Z',
  report: '# PatchPilot Live Trigger Launch Package\n\n- Status: `READY`'
};

const readyOutcomeCloseout: DemoLiveTriggerOutcomeCloseout = {
  status: 'READY',
  successful: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  launchPackageArchiveId: 'launch-package-archive-1',
  launchPackageStatus: 'READY',
  launchPackageArchivedAt: '2026-07-02T00:00:05Z',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  failureReason: null,
  taskCreatedAt: '2026-07-02T00:10:00Z',
  taskUpdatedAt: '2026-07-02T00:11:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  webhookDeliveryStatus: 'TASK_CREATED',
  summary: 'Live trigger completed and created Pull Request https://github.com/bingqin2/PatchPilot/pull/42.',
  evidenceNotes: ['Launch package archive launch-package-archive-1 was used.', 'Task task-1 completed.'],
  nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
  sideEffectContract: 'Read-only live trigger outcome closeout: this endpoint does not mutate GitHub or task state.',
  generatedAt: '2026-07-02T01:00:00Z',
  markdownReport: '# PatchPilot Live Trigger Outcome Closeout'
};

const readyOutcomeCloseoutArchive: DemoLiveTriggerOutcomeCloseoutArchive = {
  id: 'outcome-closeout-archive-1',
  status: 'READY',
  successful: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  launchPackageArchiveId: 'launch-package-archive-1',
  launchPackageStatus: 'READY',
  launchPackageArchivedAt: '2026-07-02T00:00:05Z',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  failureReason: null,
  taskCreatedAt: '2026-07-02T00:10:00Z',
  taskUpdatedAt: '2026-07-02T00:11:00Z',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  webhookDeliveryStatus: 'TASK_CREATED',
  summary: 'Live trigger completed and created Pull Request https://github.com/bingqin2/PatchPilot/pull/42.',
  evidenceNotes: ['Launch package archive launch-package-archive-1 was used.', 'Task task-1 completed.'],
  nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
  sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
  closeoutGeneratedAt: '2026-07-02T01:00:00Z',
  archivedAt: '2026-07-02T01:05:00Z',
  report: '# PatchPilot Live Trigger Outcome Closeout'
};

const readyLiveDemoEvidenceBundle: DemoLiveDemoEvidenceBundle = {
  status: 'READY',
  readyForHandoff: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  launchPackageArchiveId: 'launch-package-archive-1',
  launchPackageArchivedAt: '2026-07-02T00:00:05Z',
  outcomeCloseoutArchiveId: 'outcome-closeout-archive-1',
  outcomeCloseoutArchivedAt: '2026-07-02T01:05:00Z',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  summary: 'Live demo evidence bundle is ready for handoff.',
  evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.', 'Outcome closeout archive outcome-closeout-archive-1 is successful.'],
  nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
  sideEffectContract: 'Read-only live demo evidence bundle: this endpoint does not mutate GitHub or task state.',
  generatedAt: '2026-07-02T02:00:00Z',
  markdownReport: '# PatchPilot Live Demo Evidence Bundle'
};

const readyLiveDemoEvidenceBundleArchive: DemoLiveDemoEvidenceBundleArchive = {
  id: 'live-demo-evidence-bundle-archive-1',
  status: 'READY',
  readyForHandoff: true,
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  launchPackageArchiveId: 'launch-package-archive-1',
  launchPackageArchivedAt: '2026-07-02T00:00:05Z',
  outcomeCloseoutArchiveId: 'outcome-closeout-archive-1',
  outcomeCloseoutArchivedAt: '2026-07-02T01:05:00Z',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  summary: 'Live demo evidence bundle is ready for handoff.',
  evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.'],
  nextActions: ['Review and merge https://github.com/bingqin2/PatchPilot/pull/42.'],
  sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
  bundleGeneratedAt: '2026-07-02T02:00:00Z',
  archivedAt: '2026-07-02T03:00:00Z',
  report: '# PatchPilot Live Demo Evidence Bundle Archive'
};

const readyLiveDemoHandoffPackage: DemoLiveDemoHandoffPackage = {
  status: 'READY',
  readyForReview: true,
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  summary: 'Live demo handoff package is ready for reviewer handoff.',
  reviewChecklist: [
    'Open the Pull Request and review the files changed.',
    'Confirm the evidence bundle archive live-demo-evidence-bundle-archive-1 matches the issue and task.',
    'Merge or close the Pull Request according to repository policy.'
  ],
  deliveryInstructions: ['Share this handoff package and archived evidence report with the reviewer.'],
  evidenceNotes: ['Launch package archive launch-package-archive-1 is ready.'],
  sideEffectContract: 'read-only live demo handoff package',
  generatedAt: '2026-07-02T04:00:00Z',
  markdownReport: '# PatchPilot Live Demo Handoff Package'
};

const readyLiveDemoHandoffDeliveryReceipt: DemoLiveDemoHandoffDeliveryReceipt = {
  id: 'live-demo-handoff-delivery-receipt-1',
  status: 'READY',
  handoffPackageStatus: 'READY',
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  triggerUser: 'bingqin2',
  triggerComment: '/agent fix touch docs/live-package.md',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  webhookDeliveryId: 'delivery-1',
  summary: 'Live demo handoff package delivery receipt is recorded.',
  deliveryChannel: 'github-comment',
  deliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
  operator: 'local-operator',
  notes: 'Sent the live demo handoff package to the reviewer.',
  deliveredAt: '2026-07-02T04:55:00Z',
  createdAt: '2026-07-02T05:00:00Z',
  markdownReport: '# PatchPilot Live Demo Handoff Delivery Receipt'
};

const readyLiveDemoHandoffDeliveryFinalization: DemoLiveDemoHandoffDeliveryFinalization = {
  status: 'READY',
  finalized: true,
  summary: 'Live demo handoff delivery is finalized with a fresh delivery receipt.',
  nextAction: 'Use this finalization report as the live demo reviewer handoff completion proof.',
  latestDeliveryReceiptId: 'live-demo-handoff-delivery-receipt-1',
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryChannel: 'github-comment',
  latestDeliveredAt: '2026-07-02T04:55:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest live demo handoff delivery receipt matches the current handoff package.',
  checks: [{
    name: 'Live demo handoff package',
    status: 'READY',
    summary: 'Live demo handoff package is ready.',
    nextAction: 'No action needed.'
  }, {
    name: 'Live demo handoff delivery receipt',
    status: 'READY',
    summary: 'Latest live demo handoff delivery receipt matches the current handoff package.',
    nextAction: 'No action needed.'
  }],
  evidenceNotes: [
    'Live demo handoff package is ready.',
    'Live demo handoff delivery receipt live-demo-handoff-delivery-receipt-1 is fresh.'
  ],
  downloadActions: ['Download live demo handoff delivery finalization report.'],
  sideEffectContract: 'GET /api/demo/live-demo-handoff-package/delivery-finalization is read-only.',
  markdownReport: '# PatchPilot Live Demo Handoff Delivery Finalization',
  generatedAt: '2026-07-02T06:00:00Z'
};

const readyLiveDemoHandoffDeliveryFinalizationArchive: DemoLiveDemoHandoffDeliveryFinalizationArchive = {
  id: 'live-demo-handoff-delivery-finalization-archive-1',
  status: 'READY',
  finalized: true,
  summary: 'Live demo handoff delivery is finalized with a fresh delivery receipt.',
  nextAction: 'Use this finalization report as the live demo reviewer handoff completion proof.',
  latestDeliveryReceiptId: 'live-demo-handoff-delivery-receipt-1',
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryChannel: 'github-comment',
  latestDeliveredAt: '2026-07-02T04:55:00Z',
  deliveryReceiptFreshness: 'FRESH',
  deliveryReceiptFresh: true,
  deliveryReceiptFreshnessSummary: 'Latest live demo handoff delivery receipt matches the current handoff package.',
  checks: readyLiveDemoHandoffDeliveryFinalization.checks,
  evidenceNotes: readyLiveDemoHandoffDeliveryFinalization.evidenceNotes,
  downloadActions: ['Download live demo handoff delivery finalization archive report.'],
  sideEffectContract: 'Archive creation writes only PatchPilot local archive records.',
  finalizationGeneratedAt: '2026-07-02T06:00:00Z',
  archivedAt: '2026-07-02T07:00:00Z',
  report: '# PatchPilot Live Demo Handoff Delivery Finalization Archive'
};

const readyLiveDemoCompletionCertificate: DemoLiveDemoCompletionCertificate = {
  status: 'READY',
  certified: true,
  summary: 'PatchPilot live demo is certified from the latest handoff finalization archive.',
  nextAction: 'Share the live demo completion certificate with reviewers.',
  latestFinalizationArchiveId: 'live-demo-handoff-delivery-finalization-archive-1',
  latestDeliveryReceiptId: 'live-demo-handoff-delivery-receipt-1',
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
  latestDeliveryChannel: 'github-comment',
  latestDeliveredAt: '2026-07-02T04:55:00Z',
  deliveryReceiptFreshness: 'FRESH',
  latestFinalizationGeneratedAt: '2026-07-02T06:00:00Z',
  latestFinalizationArchivedAt: '2026-07-02T07:00:00Z',
  generatedAt: '2026-07-02T08:00:00Z',
  downloadActions: ['Download live demo completion certificate.'],
  sideEffectContract: 'GET /api/demo/live-demo-handoff-package/completion-certificate is read-only.',
  markdownReport: '# PatchPilot Live Demo Completion Certificate'
};

const readyLiveDemoCompletionCertificateArchive: DemoLiveDemoCompletionCertificateArchive = {
  id: 'live-demo-completion-certificate-archive-1',
  ...readyLiveDemoCompletionCertificate,
  archivedAt: '2026-07-02T09:00:00Z',
  report: '# PatchPilot Live Demo Completion Certificate'
};

const readyLiveDemoArtifactChainReport: DemoLiveDemoArtifactChainReport = {
  status: 'READY',
  complete: true,
  summary: 'PatchPilot live demo artifact chain is complete and consistent.',
  nextAction: 'Share the live demo artifact chain report with reviewers.',
  launchPackageArchiveId: 'launch-package-archive-1',
  outcomeCloseoutArchiveId: 'outcome-closeout-archive-1',
  evidenceBundleArchiveId: 'live-demo-evidence-bundle-archive-1',
  handoffFinalizationArchiveId: 'live-demo-handoff-delivery-finalization-archive-1',
  completionCertificateArchiveId: 'live-demo-completion-certificate-archive-1',
  repository: 'bingqin2/PatchPilot',
  issueNumber: 1,
  issueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  taskId: 'task-1',
  taskStatus: 'COMPLETED',
  pullRequestUrl: 'https://github.com/bingqin2/PatchPilot/pull/42',
  steps: [
    {
      name: 'Live trigger launch package archive',
      status: 'READY',
      artifactId: 'launch-package-archive-1',
      summary: 'Launch package archive is ready.',
      nextAction: 'Continue.'
    },
    {
      name: 'Live demo completion certificate archive',
      status: 'READY',
      artifactId: 'live-demo-completion-certificate-archive-1',
      summary: 'Completion certificate archive is certified.',
      nextAction: 'Share report.'
    }
  ],
  checks: [
    {
      name: 'Evidence bundle references launch package',
      status: 'READY',
      summary: 'Evidence bundle references the launch package archive.',
      nextAction: 'Continue.'
    }
  ],
  evidenceNotes: ['Completion certificate archive closes the same evidence bundle.'],
  downloadActions: ['Download live demo artifact chain report.'],
  sideEffectContract: 'GET /api/demo/live-demo-handoff-package/artifact-chain-report is read-only.',
  generatedAt: '2026-07-02T10:00:00Z',
  markdownReport: '# PatchPilot Live Demo Artifact Chain Report'
};

const baseProps = {
  error: null,
  pending: false,
  onRunGate: vi.fn(),
  launchPackage: null,
  launchPackageError: null,
  launchPackagePending: false,
  onCreateLaunchPackage: vi.fn(),
  launchPackageArchives: [] as DemoLiveTriggerLaunchPackageArchive[],
  launchPackageArchiveError: null,
  onArchiveLaunchPackage: vi.fn(),
  onDownloadLaunchPackageArchiveReport: vi.fn(),
  outcomeCloseout: null,
  outcomeCloseoutError: null,
  outcomeCloseoutPending: false,
  onCreateOutcomeCloseout: vi.fn(),
  onDownloadOutcomeCloseoutReport: vi.fn(),
  outcomeCloseoutArchives: [] as DemoLiveTriggerOutcomeCloseoutArchive[],
  outcomeCloseoutArchiveError: null,
  onArchiveOutcomeCloseout: vi.fn(),
  onDownloadOutcomeCloseoutArchiveReport: vi.fn(),
  liveDemoEvidenceBundle: null as DemoLiveDemoEvidenceBundle | null,
  liveDemoEvidenceBundleError: null,
  onRefreshLiveDemoEvidenceBundle: vi.fn(),
  onDownloadLiveDemoEvidenceBundleReport: vi.fn(),
  liveDemoEvidenceBundleArchives: [] as DemoLiveDemoEvidenceBundleArchive[],
  liveDemoEvidenceBundleArchiveError: null,
  onArchiveLiveDemoEvidenceBundle: vi.fn(),
  onDownloadLiveDemoEvidenceBundleArchiveReport: vi.fn(),
  liveDemoHandoffPackage: null as DemoLiveDemoHandoffPackage | null,
  liveDemoHandoffPackageError: null,
  onRefreshLiveDemoHandoffPackage: vi.fn(),
  onDownloadLiveDemoHandoffPackageReport: vi.fn(),
  liveDemoHandoffDeliveryReceipts: [] as DemoLiveDemoHandoffDeliveryReceipt[],
  liveDemoHandoffDeliveryReceiptError: null,
  onRecordLiveDemoHandoffDeliveryReceipt: vi.fn(),
  onDownloadLiveDemoHandoffDeliveryReceiptReport: vi.fn(),
  liveDemoHandoffDeliveryFinalization: null as DemoLiveDemoHandoffDeliveryFinalization | null,
  liveDemoHandoffDeliveryFinalizationError: null,
  onRefreshLiveDemoHandoffDeliveryFinalization: vi.fn(),
  onDownloadLiveDemoHandoffDeliveryFinalizationReport: vi.fn(),
  liveDemoHandoffDeliveryFinalizationArchives: [] as DemoLiveDemoHandoffDeliveryFinalizationArchive[],
  liveDemoHandoffDeliveryFinalizationArchiveError: null,
  onArchiveLiveDemoHandoffDeliveryFinalization: vi.fn(),
  onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport: vi.fn(),
  liveDemoCompletionCertificate: null as DemoLiveDemoCompletionCertificate | null,
  liveDemoCompletionCertificateError: null,
  onRefreshLiveDemoCompletionCertificate: vi.fn(),
  onDownloadLiveDemoCompletionCertificateReport: vi.fn(),
  liveDemoCompletionCertificateArchives: [] as DemoLiveDemoCompletionCertificateArchive[],
  liveDemoCompletionCertificateArchiveError: null,
  onArchiveLiveDemoCompletionCertificate: vi.fn(),
  onDownloadLiveDemoCompletionCertificateArchiveReport: vi.fn(),
  liveDemoArtifactChainReport: null as DemoLiveDemoArtifactChainReport | null,
  liveDemoArtifactChainReportError: null,
  onRefreshLiveDemoArtifactChainReport: vi.fn(),
  onDownloadLiveDemoArtifactChainReport: vi.fn()
};

test('submits exact live launch gate input', async () => {
  const user = userEvent.setup();
  const onRunGate = vi.fn(async () => readyGate);

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={null}
      onRunGate={onRunGate}
    />
  );

  await user.clear(screen.getByLabelText('Repository owner'));
  await user.type(screen.getByLabelText('Repository owner'), 'bingqin2');
  await user.clear(screen.getByLabelText('Repository name'));
  await user.type(screen.getByLabelText('Repository name'), 'PatchPilot');
  await user.clear(screen.getByLabelText('Issue number'));
  await user.type(screen.getByLabelText('Issue number'), '1');
  await user.clear(screen.getByLabelText('Trigger user'));
  await user.type(screen.getByLabelText('Trigger user'), 'bingqin2');
  await user.clear(screen.getByLabelText('GitHub issue comment'));
  await user.type(screen.getByLabelText('GitHub issue comment'), '/agent fix touch docs/live-gate.md');
  await user.click(screen.getByRole('button', { name: 'Run live launch gate' }));

  expect(onRunGate).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md'
  });
});

test('renders ready and blocked launch gate results', () => {
  const { rerender } = render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
    />
  );

  const panel = screen.getByRole('region', { name: 'Live launch gate' });
  expect(within(panel).getByText('Ready to post')).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot is ready for a live /agent fix launch.')).toBeInTheDocument();
  expect(within(panel).getByText('Self-hosted launch readiness')).toBeInTheDocument();
  expect(within(panel).getByText('Live trigger dry run')).toBeInTheDocument();
  expect(within(panel).getByText('https://example.trycloudflare.com/api/github/webhook')).toBeInTheDocument();
  expect(within(panel).getByText('Read-only live launch gate: this endpoint does not create tasks.')).toBeInTheDocument();

  rerender(
    <LiveLaunchGatePanel
      {...baseProps}
      result={{
        ...readyGate,
        status: 'BLOCKED',
        readyToPost: false,
        summary: 'PatchPilot is blocked before live launch.',
        checks: readyGate.checks.map((check) =>
          check.name === 'Live trigger dry run' ? { ...check, status: 'BLOCKED' } : check
        )
      }}
    />
  );

  expect(within(panel).getByText('Blocked')).toBeInTheDocument();
  expect(within(panel).getByText('PatchPilot is blocked before live launch.')).toBeInTheDocument();
});

test('copies backend launch gate markdown report', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy launch gate report' }));

  expect(writeText).toHaveBeenCalledWith('# PatchPilot Live Launch Gate\n\n- Status: READY');
});

test('creates and downloads the final live trigger launch package', async () => {
  const user = userEvent.setup();
  const onCreateLaunchPackage = vi.fn(async () => readyLaunchPackage);
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:launch-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackage={readyLaunchPackage}
      onCreateLaunchPackage={onCreateLaunchPackage}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Create live trigger launch package' }));
  await user.click(screen.getByRole('button', { name: 'Download live trigger launch package' }));

  expect(onCreateLaunchPackage).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md'
  });
  expect(screen.getByText('Live trigger launch package')).toBeInTheDocument();
  expect(screen.getByText('operator-archive-1')).toBeInTheDocument();
  expect(screen.getByText('Ready to post live trigger')).toBeInTheDocument();
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-package');
});

test('archives and downloads frozen live trigger launch packages', async () => {
  const user = userEvent.setup();
  const onArchiveLaunchPackage = vi.fn(async () => readyLaunchPackageArchive);
  const onDownloadLaunchPackageArchiveReport = vi.fn(async () => new Blob(['archive report'], { type: 'text/markdown' }));
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:launch-package-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackage={readyLaunchPackage}
      launchPackageArchives={[readyLaunchPackageArchive]}
      onArchiveLaunchPackage={onArchiveLaunchPackage}
      onDownloadLaunchPackageArchiveReport={onDownloadLaunchPackageArchiveReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Archive live trigger launch package' }));
  await user.click(screen.getByRole('button', { name: 'Download launch package archive launch-package-archive-1' }));

  expect(onArchiveLaunchPackage).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md'
  });
  expect(screen.getByText('Recent launch package archives')).toBeInTheDocument();
  expect(screen.getByText('launch-package-archive-1')).toBeInTheDocument();
  expect(screen.getByText('Archived at 2026-07-02T00:00:05Z')).toBeInTheDocument();
  expect(onDownloadLaunchPackageArchiveReport).toHaveBeenCalledWith('launch-package-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:launch-package-archive');
});

test('shows launch package archive errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackageArchiveError="Archive write failed"
    />
  );

  expect(screen.getByText('Live trigger launch package archive failed')).toBeInTheDocument();
  expect(screen.getByText('Archive write failed')).toBeInTheDocument();
});

test('generates and downloads live trigger outcome closeout evidence', async () => {
  const user = userEvent.setup();
  const onCreateOutcomeCloseout = vi.fn(async () => readyOutcomeCloseout);
  const onDownloadOutcomeCloseoutReport = vi.fn(async () => new Blob(['outcome report'], { type: 'text/markdown' }));
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-trigger-outcome');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackage={readyLaunchPackage}
      launchPackageArchives={[readyLaunchPackageArchive]}
      outcomeCloseout={readyOutcomeCloseout}
      onCreateOutcomeCloseout={onCreateOutcomeCloseout}
      onDownloadOutcomeCloseoutReport={onDownloadOutcomeCloseoutReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Generate live trigger outcome closeout' }));
  await user.click(screen.getByRole('button', { name: 'Download live trigger outcome closeout' }));

  expect(onCreateOutcomeCloseout).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md',
    launchPackageArchiveId: 'launch-package-archive-1'
  });
  expect(onDownloadOutcomeCloseoutReport).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md',
    launchPackageArchiveId: 'launch-package-archive-1'
  });
  expect(screen.getByText('Live trigger outcome closeout')).toBeInTheDocument();
  expect(screen.getByText('Task task-1')).toBeInTheDocument();
  expect(screen.getByText('https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-trigger-outcome');
});

test('shows live trigger outcome closeout errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      outcomeCloseoutError="No matching task found"
    />
  );

  expect(screen.getByText('Live trigger outcome closeout failed')).toBeInTheDocument();
  expect(screen.getByText('No matching task found')).toBeInTheDocument();
});

test('archives and downloads frozen live trigger outcome closeouts', async () => {
  const user = userEvent.setup();
  const onArchiveOutcomeCloseout = vi.fn(async () => readyOutcomeCloseoutArchive);
  const onDownloadOutcomeCloseoutArchiveReport = vi.fn(async () => new Blob(['outcome archive'], { type: 'text/markdown' }));
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-trigger-outcome-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackage={readyLaunchPackage}
      launchPackageArchives={[readyLaunchPackageArchive]}
      outcomeCloseout={readyOutcomeCloseout}
      outcomeCloseoutArchives={[readyOutcomeCloseoutArchive]}
      onArchiveOutcomeCloseout={onArchiveOutcomeCloseout}
      onDownloadOutcomeCloseoutArchiveReport={onDownloadOutcomeCloseoutArchiveReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Archive live trigger outcome closeout' }));
  await user.click(screen.getByRole('button', { name: 'Download outcome closeout archive outcome-closeout-archive-1' }));

  expect(onArchiveOutcomeCloseout).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/live-gate.md',
    launchPackageArchiveId: 'launch-package-archive-1'
  });
  const archiveList = screen.getByText('Recent outcome closeout archives').closest('div') as HTMLElement;
  expect(archiveList).toBeInTheDocument();
  expect(within(archiveList).getByText('outcome-closeout-archive-1')).toBeInTheDocument();
  expect(within(archiveList).getByText('Task task-1')).toBeInTheDocument();
  expect(within(archiveList).getByText('Archived at 2026-07-02T01:05:00Z')).toBeInTheDocument();
  expect(onDownloadOutcomeCloseoutArchiveReport).toHaveBeenCalledWith('outcome-closeout-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-trigger-outcome-archive');
});

test('shows live trigger outcome closeout archive errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      outcomeCloseoutArchiveError="Closeout archive write failed"
    />
  );

  expect(screen.getByText('Live trigger outcome closeout archive failed')).toBeInTheDocument();
  expect(screen.getByText('Closeout archive write failed')).toBeInTheDocument();
});

test('renders and downloads final live demo evidence bundle', async () => {
  const user = userEvent.setup();
  const onRefreshLiveDemoEvidenceBundle = vi.fn(async () => readyLiveDemoEvidenceBundle);
  const onDownloadLiveDemoEvidenceBundleReport = vi.fn(async () => new Blob(['bundle report'], { type: 'text/markdown' }));
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-evidence-bundle');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackage={readyLaunchPackage}
      launchPackageArchives={[readyLaunchPackageArchive]}
      outcomeCloseout={readyOutcomeCloseout}
      outcomeCloseoutArchives={[readyOutcomeCloseoutArchive]}
      liveDemoEvidenceBundle={readyLiveDemoEvidenceBundle}
      onRefreshLiveDemoEvidenceBundle={onRefreshLiveDemoEvidenceBundle}
      onDownloadLiveDemoEvidenceBundleReport={onDownloadLiveDemoEvidenceBundleReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Refresh live demo evidence bundle' }));
  await user.click(screen.getByRole('button', { name: 'Download live demo evidence bundle' }));

  expect(onRefreshLiveDemoEvidenceBundle).toHaveBeenCalled();
  expect(screen.getByText('Live demo evidence bundle')).toBeInTheDocument();
  const bundleSection = screen.getByText('Live demo evidence bundle').closest('.demo-launch-preflight-result') as HTMLElement;
  expect(within(bundleSection).getByText('Live demo evidence bundle is ready for handoff.')).toBeInTheDocument();
  expect(within(bundleSection).getByText('Archive outcome-closeout-archive-1')).toBeInTheDocument();
  expect(within(bundleSection).getByText('PR https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(onDownloadLiveDemoEvidenceBundleReport).toHaveBeenCalled();
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-evidence-bundle');
});

test('shows live demo evidence bundle errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoEvidenceBundleError="Outcome closeout archive is missing"
    />
  );

  expect(screen.getByText('Live demo evidence bundle failed')).toBeInTheDocument();
  expect(screen.getByText('Outcome closeout archive is missing')).toBeInTheDocument();
});

test('archives and downloads final live demo evidence bundle snapshots', async () => {
  const user = userEvent.setup();
  const onArchiveLiveDemoEvidenceBundle = vi.fn(async () => readyLiveDemoEvidenceBundleArchive);
  const onDownloadLiveDemoEvidenceBundleArchiveReport = vi.fn(async () =>
    new Blob(['bundle archive report'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-evidence-bundle-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoEvidenceBundle={readyLiveDemoEvidenceBundle}
      liveDemoEvidenceBundleArchives={[readyLiveDemoEvidenceBundleArchive]}
      onArchiveLiveDemoEvidenceBundle={onArchiveLiveDemoEvidenceBundle}
      onDownloadLiveDemoEvidenceBundleArchiveReport={onDownloadLiveDemoEvidenceBundleArchiveReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Archive live demo evidence bundle' }));
  await user.click(screen.getByRole('button', {
    name: 'Download live demo evidence bundle archive live-demo-evidence-bundle-archive-1'
  }));

  expect(onArchiveLiveDemoEvidenceBundle).toHaveBeenCalled();
  const archiveList = screen.getByText('Recent live demo evidence bundle archives').closest('div') as HTMLElement;
  expect(archiveList).toBeInTheDocument();
  expect(within(archiveList).getByText('live-demo-evidence-bundle-archive-1')).toBeInTheDocument();
  expect(within(archiveList).getByText('Task task-1')).toBeInTheDocument();
  expect(within(archiveList).getByText('https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(within(archiveList).getByText('Archived at 2026-07-02T03:00:00Z')).toBeInTheDocument();
  expect(onDownloadLiveDemoEvidenceBundleArchiveReport).toHaveBeenCalledWith('live-demo-evidence-bundle-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-evidence-bundle-archive');
});

test('shows live demo evidence bundle archive errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoEvidenceBundleArchiveError="Bundle archive write failed"
    />
  );

  expect(screen.getByText('Live demo evidence bundle archive failed')).toBeInTheDocument();
  expect(screen.getByText('Bundle archive write failed')).toBeInTheDocument();
});

test('renders and downloads live demo reviewer handoff package', async () => {
  const user = userEvent.setup();
  const onRefreshLiveDemoHandoffPackage = vi.fn(async () => readyLiveDemoHandoffPackage);
  const onDownloadLiveDemoHandoffPackageReport = vi.fn(async () =>
    new Blob(['handoff package'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-handoff-package');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoEvidenceBundleArchives={[readyLiveDemoEvidenceBundleArchive]}
      liveDemoHandoffPackage={readyLiveDemoHandoffPackage}
      onRefreshLiveDemoHandoffPackage={onRefreshLiveDemoHandoffPackage}
      onDownloadLiveDemoHandoffPackageReport={onDownloadLiveDemoHandoffPackageReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Refresh live demo handoff package' }));
  await user.click(screen.getByRole('button', { name: 'Download live demo handoff package' }));

  expect(onRefreshLiveDemoHandoffPackage).toHaveBeenCalled();
  expect(screen.getByText('Live demo handoff package')).toBeInTheDocument();
  const packageSection = screen.getByText('Live demo handoff package').closest('.demo-launch-preflight-result') as HTMLElement;
  expect(within(packageSection).getByText('Live demo handoff package is ready for reviewer handoff.')).toBeInTheDocument();
  expect(within(packageSection).getByText('Archive live-demo-evidence-bundle-archive-1')).toBeInTheDocument();
  expect(within(packageSection).getByText('PR https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(within(packageSection).getByText('Open the Pull Request and review the files changed.')).toBeInTheDocument();
  expect(within(packageSection).getByText('Share this handoff package and archived evidence report with the reviewer.')).toBeInTheDocument();
  expect(onDownloadLiveDemoHandoffPackageReport).toHaveBeenCalled();
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-handoff-package');
});

test('shows live demo handoff package errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffPackageError="Evidence bundle archive is missing"
    />
  );

  expect(screen.getByText('Live demo handoff package failed')).toBeInTheDocument();
  expect(screen.getByText('Evidence bundle archive is missing')).toBeInTheDocument();
});

test('records and downloads live demo handoff delivery receipts', async () => {
  const user = userEvent.setup();
  const onRecordLiveDemoHandoffDeliveryReceipt = vi.fn(async () => readyLiveDemoHandoffDeliveryReceipt);
  const onDownloadLiveDemoHandoffDeliveryReceiptReport = vi.fn(async () =>
    new Blob(['receipt'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-handoff-delivery-receipt');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffPackage={readyLiveDemoHandoffPackage}
      liveDemoHandoffDeliveryReceipts={[readyLiveDemoHandoffDeliveryReceipt]}
      onRecordLiveDemoHandoffDeliveryReceipt={onRecordLiveDemoHandoffDeliveryReceipt}
      onDownloadLiveDemoHandoffDeliveryReceiptReport={onDownloadLiveDemoHandoffDeliveryReceiptReport}
    />
  );

  await user.clear(screen.getByLabelText('Live demo handoff delivery target'));
  await user.type(
    screen.getByLabelText('Live demo handoff delivery target'),
    'https://github.com/bingqin2/PatchPilot/pull/42'
  );
  await user.click(screen.getByRole('button', { name: 'Record live demo handoff delivery receipt' }));
  await user.click(screen.getByRole('button', {
    name: 'Download live demo handoff delivery receipt live-demo-handoff-delivery-receipt-1'
  }));

  expect(onRecordLiveDemoHandoffDeliveryReceipt).toHaveBeenCalledWith({
    deliveryChannel: 'github-comment',
    deliveryTarget: 'https://github.com/bingqin2/PatchPilot/pull/42',
    operator: 'local-operator',
    notes: 'Sent the live demo handoff package to the reviewer.',
    deliveredAt: expect.any(String)
  });
  expect(screen.getByText('Live demo handoff delivery receipts')).toBeInTheDocument();
  expect(screen.getByText('live-demo-handoff-delivery-receipt-1')).toBeInTheDocument();
  expect(screen.getByText('github-comment')).toBeInTheDocument();
  expect(screen.getByText('https://github.com/bingqin2/PatchPilot/pull/42')).toBeInTheDocument();
  expect(onDownloadLiveDemoHandoffDeliveryReceiptReport)
    .toHaveBeenCalledWith('live-demo-handoff-delivery-receipt-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-handoff-delivery-receipt');
});

test('shows live demo handoff delivery receipt errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffDeliveryReceiptError="Delivery receipt write failed"
    />
  );

  expect(screen.getByText('Live demo handoff delivery receipt failed')).toBeInTheDocument();
  expect(screen.getByText('Delivery receipt write failed')).toBeInTheDocument();
});

test('refreshes and downloads live demo handoff delivery finalization', async () => {
  const user = userEvent.setup();
  const onRefreshLiveDemoHandoffDeliveryFinalization = vi.fn(async () => readyLiveDemoHandoffDeliveryFinalization);
  const onDownloadLiveDemoHandoffDeliveryFinalizationReport = vi.fn(
    async () => new Blob(['finalization'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-handoff-delivery-finalization');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffPackage={readyLiveDemoHandoffPackage}
      liveDemoHandoffDeliveryFinalization={readyLiveDemoHandoffDeliveryFinalization}
      onRefreshLiveDemoHandoffDeliveryFinalization={onRefreshLiveDemoHandoffDeliveryFinalization}
      onDownloadLiveDemoHandoffDeliveryFinalizationReport={onDownloadLiveDemoHandoffDeliveryFinalizationReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Refresh live demo handoff delivery finalization' }));
  await user.click(screen.getByRole('button', { name: 'Download live demo handoff delivery finalization' }));

  const finalizationPanel = screen
    .getByText('Live demo handoff delivery finalization')
    .closest('.demo-launch-preflight-result');
  expect(finalizationPanel).not.toBeNull();
  const finalizationQueries = within(finalizationPanel as HTMLElement);
  expect(finalizationQueries.getAllByText('READY').length).toBeGreaterThanOrEqual(1);
  expect(finalizationQueries.getByText('FRESH')).toBeInTheDocument();
  expect(finalizationQueries.getByText('live-demo-handoff-delivery-receipt-1')).toBeInTheDocument();
  expect(
    finalizationQueries.getAllByText('Latest live demo handoff delivery receipt matches the current handoff package.')
      .length
  ).toBeGreaterThanOrEqual(1);
  expect(onRefreshLiveDemoHandoffDeliveryFinalization).toHaveBeenCalledTimes(1);
  expect(onDownloadLiveDemoHandoffDeliveryFinalizationReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-handoff-delivery-finalization');
});

test('archives and downloads live demo handoff delivery finalization archives', async () => {
  const user = userEvent.setup();
  const onArchiveLiveDemoHandoffDeliveryFinalization = vi.fn(
    async () => readyLiveDemoHandoffDeliveryFinalizationArchive
  );
  const onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport = vi.fn(
    async () => new Blob(['finalization archive'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-handoff-delivery-finalization-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffDeliveryFinalization={readyLiveDemoHandoffDeliveryFinalization}
      liveDemoHandoffDeliveryFinalizationArchives={[readyLiveDemoHandoffDeliveryFinalizationArchive]}
      onArchiveLiveDemoHandoffDeliveryFinalization={onArchiveLiveDemoHandoffDeliveryFinalization}
      onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport={
        onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport
      }
    />
  );

  await user.click(screen.getByRole('button', { name: 'Archive live demo handoff delivery finalization' }));
  await user.click(screen.getByRole('button', {
    name: 'Download live demo handoff delivery finalization archive live-demo-handoff-delivery-finalization-archive-1'
  }));

  expect(screen.getByText('Recent live demo handoff delivery finalization archives')).toBeInTheDocument();
  expect(screen.getByText('live-demo-handoff-delivery-finalization-archive-1')).toBeInTheDocument();
  expect(screen.getAllByText('live-demo-handoff-delivery-receipt-1').length).toBeGreaterThan(0);
  expect(onArchiveLiveDemoHandoffDeliveryFinalization).toHaveBeenCalledTimes(1);
  expect(onDownloadLiveDemoHandoffDeliveryFinalizationArchiveReport)
    .toHaveBeenCalledWith('live-demo-handoff-delivery-finalization-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-handoff-delivery-finalization-archive');
});

test('shows live demo handoff delivery finalization errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffDeliveryFinalizationError="Delivery finalization refresh failed"
    />
  );

  expect(screen.getByText('Live demo handoff delivery finalization failed')).toBeInTheDocument();
  expect(screen.getByText('Delivery finalization refresh failed')).toBeInTheDocument();
});

test('shows live demo handoff delivery finalization archive errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoHandoffDeliveryFinalizationArchiveError="Delivery finalization archive failed"
    />
  );

  expect(screen.getByText('Live demo handoff delivery finalization archive failed')).toBeInTheDocument();
  expect(screen.getByText('Delivery finalization archive failed')).toBeInTheDocument();
});

test('refreshes, downloads, archives, and downloads live demo completion certificates', async () => {
  const user = userEvent.setup();
  const onRefreshLiveDemoCompletionCertificate = vi.fn(async () => readyLiveDemoCompletionCertificate);
  const onDownloadLiveDemoCompletionCertificateReport = vi.fn(
    async () => new Blob(['completion certificate'], { type: 'text/markdown' })
  );
  const onArchiveLiveDemoCompletionCertificate = vi.fn(async () => readyLiveDemoCompletionCertificateArchive);
  const onDownloadLiveDemoCompletionCertificateArchiveReport = vi.fn(
    async () => new Blob(['completion certificate archive'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn()
    .mockReturnValueOnce('blob:live-demo-completion-certificate')
    .mockReturnValueOnce('blob:live-demo-completion-certificate-archive');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoCompletionCertificate={readyLiveDemoCompletionCertificate}
      liveDemoCompletionCertificateArchives={[readyLiveDemoCompletionCertificateArchive]}
      onRefreshLiveDemoCompletionCertificate={onRefreshLiveDemoCompletionCertificate}
      onDownloadLiveDemoCompletionCertificateReport={onDownloadLiveDemoCompletionCertificateReport}
      onArchiveLiveDemoCompletionCertificate={onArchiveLiveDemoCompletionCertificate}
      onDownloadLiveDemoCompletionCertificateArchiveReport={onDownloadLiveDemoCompletionCertificateArchiveReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Refresh live demo completion certificate' }));
  await user.click(screen.getByRole('button', { name: 'Download live demo completion certificate' }));
  await user.click(screen.getByRole('button', { name: 'Archive live demo completion certificate' }));
  await user.click(screen.getByRole('button', {
    name: 'Download live demo completion certificate archive live-demo-completion-certificate-archive-1'
  }));

  expect(screen.getByText('Live demo completion certificate')).toBeInTheDocument();
  expect(screen.getByText('Recent live demo completion certificate archives')).toBeInTheDocument();
  expect(screen.getByText('live-demo-completion-certificate-archive-1')).toBeInTheDocument();
  expect(screen.getAllByText('live-demo-handoff-delivery-finalization-archive-1').length)
    .toBeGreaterThan(0);
  expect(onRefreshLiveDemoCompletionCertificate).toHaveBeenCalledTimes(1);
  expect(onDownloadLiveDemoCompletionCertificateReport).toHaveBeenCalledTimes(1);
  expect(onArchiveLiveDemoCompletionCertificate).toHaveBeenCalledTimes(1);
  expect(onDownloadLiveDemoCompletionCertificateArchiveReport)
    .toHaveBeenCalledWith('live-demo-completion-certificate-archive-1');
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalledTimes(2);
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-completion-certificate');
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-completion-certificate-archive');
});

test('shows live demo completion certificate errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoCompletionCertificateError="Completion certificate refresh failed"
      liveDemoCompletionCertificateArchiveError="Completion certificate archive failed"
    />
  );

  expect(screen.getByText('Live demo completion certificate failed')).toBeInTheDocument();
  expect(screen.getByText('Completion certificate refresh failed')).toBeInTheDocument();
  expect(screen.getByText('Live demo completion certificate archive failed')).toBeInTheDocument();
  expect(screen.getByText('Completion certificate archive failed')).toBeInTheDocument();
});

test('refreshes and downloads live demo artifact chain reports', async () => {
  const user = userEvent.setup();
  const onRefreshLiveDemoArtifactChainReport = vi.fn(async () => readyLiveDemoArtifactChainReport);
  const onDownloadLiveDemoArtifactChainReport = vi.fn(
    async () => new Blob(['artifact chain report'], { type: 'text/markdown' })
  );
  const anchorClick = vi.fn();
  const createObjectURL = vi.fn(() => 'blob:live-demo-artifact-chain-report');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  vi.spyOn(document, 'createElement').mockImplementation((tagName: string) => {
    const element = document.createElementNS('http://www.w3.org/1999/xhtml', tagName) as HTMLAnchorElement;
    if (tagName === 'a') {
      element.click = anchorClick;
    }
    return element;
  });

  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoArtifactChainReport={readyLiveDemoArtifactChainReport}
      onRefreshLiveDemoArtifactChainReport={onRefreshLiveDemoArtifactChainReport}
      onDownloadLiveDemoArtifactChainReport={onDownloadLiveDemoArtifactChainReport}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Refresh live demo artifact chain report' }));
  await user.click(screen.getByRole('button', { name: 'Download live demo artifact chain report' }));

  const chainPanel = screen
    .getByText('Live demo artifact chain report')
    .closest('.demo-launch-preflight-result');
  expect(chainPanel).not.toBeNull();
  const chainQueries = within(chainPanel as HTMLElement);
  expect(chainQueries.getByText('PatchPilot live demo artifact chain is complete and consistent.'))
    .toBeInTheDocument();
  expect(chainQueries.getAllByText('launch-package-archive-1').length).toBeGreaterThan(0);
  expect(chainQueries.getAllByText('live-demo-completion-certificate-archive-1').length).toBeGreaterThan(0);
  expect(chainQueries.getByText('Evidence bundle references launch package')).toBeInTheDocument();
  expect(chainQueries.getByText('Completion certificate archive closes the same evidence bundle.'))
    .toBeInTheDocument();
  expect(onRefreshLiveDemoArtifactChainReport).toHaveBeenCalledTimes(1);
  expect(onDownloadLiveDemoArtifactChainReport).toHaveBeenCalledTimes(1);
  expect(createObjectURL).toHaveBeenCalledWith(expect.any(Blob));
  expect(anchorClick).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:live-demo-artifact-chain-report');
});

test('shows live demo artifact chain report errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      liveDemoArtifactChainReportError="Artifact chain report failed"
    />
  );

  expect(screen.getByText('Live demo artifact chain report failed')).toBeInTheDocument();
  expect(screen.getByText('Artifact chain report failed')).toBeInTheDocument();
});

test('shows launch package errors', () => {
  render(
    <LiveLaunchGatePanel
      {...baseProps}
      result={readyGate}
      launchPackageError="Operator handoff archive is missing"
    />
  );

  expect(screen.getByText('Live trigger launch package failed')).toBeInTheDocument();
  expect(screen.getByText('Operator handoff archive is missing')).toBeInTheDocument();
});
