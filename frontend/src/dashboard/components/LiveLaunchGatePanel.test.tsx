import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoLiveLaunchGate, DemoLiveTriggerLaunchPackage } from '../../types';
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

const baseProps = {
  error: null,
  pending: false,
  onRunGate: vi.fn(),
  launchPackage: null,
  launchPackageError: null,
  launchPackagePending: false,
  onCreateLaunchPackage: vi.fn()
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
