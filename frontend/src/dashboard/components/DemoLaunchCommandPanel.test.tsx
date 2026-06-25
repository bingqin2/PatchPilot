import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoLaunchCommand, DemoLaunchCommandInput } from '../../types';
import { DemoLaunchCommandPanel } from './DemoLaunchCommandPanel';

const composedCommand: DemoLaunchCommand = {
  triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test',
  preflightInput: {
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix replace docs/demo.md PatchPilot smoke test'
  },
  githubIssueUrl: 'https://github.com/bingqin2/PatchPilot/issues/1',
  summary: 'Prepared a demo /agent fix replace command for bingqin2/PatchPilot#1.',
  nextActions: ['Run launch preflight with the generated command before posting it on GitHub.']
};

const touchCommand: DemoLaunchCommand = {
  triggerComment: '/agent fix touch docs/history.md',
  preflightInput: {
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 2,
    triggerUser: 'bingqin2',
    triggerComment: '/agent fix touch docs/history.md'
  },
  githubIssueUrl: 'https://github.com/bingqin2/PatchPilot/issues/2',
  summary: 'Prepared a demo /agent fix touch command for bingqin2/PatchPilot#2.',
  nextActions: ['Run launch preflight with the generated command before posting it on GitHub.']
};

beforeEach(() => {
  localStorage.clear();
});

test('composes a demo launch command from structured inputs', async () => {
  const user = userEvent.setup();
  const onComposeCommand = vi.fn(async () => composedCommand);

  render(
    <DemoLaunchCommandPanel
      result={null}
      error={null}
      pending={false}
      onComposeCommand={onComposeCommand}
      onApplyToPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  await user.clear(within(panel).getByLabelText('Repository owner'));
  await user.type(within(panel).getByLabelText('Repository owner'), 'bingqin2');
  await user.clear(within(panel).getByLabelText('Repository name'));
  await user.type(within(panel).getByLabelText('Repository name'), 'PatchPilot');
  await user.clear(within(panel).getByLabelText('Issue number'));
  await user.type(within(panel).getByLabelText('Issue number'), '1');
  await user.clear(within(panel).getByLabelText('Trigger user'));
  await user.type(within(panel).getByLabelText('Trigger user'), 'bingqin2');
  await user.selectOptions(within(panel).getByLabelText('Operation'), 'replace');
  await user.clear(within(panel).getByLabelText('Target path'));
  await user.type(within(panel).getByLabelText('Target path'), 'docs/demo.md');
  await user.clear(within(panel).getByLabelText('Replacement text'));
  await user.type(within(panel).getByLabelText('Replacement text'), 'PatchPilot smoke test');
  await user.click(within(panel).getByRole('button', { name: 'Generate command' }));

  expect(onComposeCommand).toHaveBeenCalledWith({
    repositoryOwner: 'bingqin2',
    repositoryName: 'PatchPilot',
    issueNumber: 1,
    triggerUser: 'bingqin2',
    operation: 'replace',
    targetPath: 'docs/demo.md',
    replacementText: 'PatchPilot smoke test'
  });
});

test('stores generated demo launch commands in local history for reuse', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  const onComposeCommand = vi.fn(async (input: DemoLaunchCommandInput) => (
    input.operation === 'touch' ? touchCommand : composedCommand
  ));
  const onApplyToPreflight = vi.fn();

  render(
    <DemoLaunchCommandPanel
      result={null}
      error={null}
      pending={false}
      onComposeCommand={onComposeCommand}
      onApplyToPreflight={onApplyToPreflight}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  expect(within(panel).getByText('No saved demo launch commands yet.')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', { name: 'Generate command' }));
  expect(within(panel).getByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();

  await user.selectOptions(within(panel).getByLabelText('Operation'), 'touch');
  await user.clear(within(panel).getByLabelText('Target path'));
  await user.type(within(panel).getByLabelText('Target path'), 'docs/history.md');
  await user.click(within(panel).getByRole('button', { name: 'Generate command' }));

  const history = within(panel).getByRole('list', { name: 'Recent demo launch commands' });
  expect(within(history).getByText('/agent fix touch docs/history.md')).toBeInTheDocument();
  expect(within(history).getByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();

  await user.click(within(history).getAllByRole('button', { name: 'Copy saved command' })[0]);
  expect(writeText).toHaveBeenCalledWith('/agent fix touch docs/history.md');

  await user.click(within(history).getAllByRole('button', { name: 'Apply saved command to composer' })[1]);
  expect(within(panel).getByLabelText('Operation')).toHaveValue('replace');
  expect(within(panel).getByLabelText('Target path')).toHaveValue('docs/demo.md');
  expect(within(panel).getByLabelText('Replacement text')).toHaveValue('PatchPilot smoke test');

  await user.click(within(history).getAllByRole('button', { name: 'Apply saved command to launch preflight' })[0]);
  expect(onApplyToPreflight).toHaveBeenCalledWith(touchCommand.preflightInput);
});

test('restores and clears demo launch command history from local storage', async () => {
  const user = userEvent.setup();
  localStorage.setItem('patchpilot.demoLaunchCommandHistory', JSON.stringify([
    {
      id: 'saved-touch',
      savedAt: '2026-06-26T01:00:00.000Z',
      input: {
        repositoryOwner: 'bingqin2',
        repositoryName: 'PatchPilot',
        issueNumber: 2,
        triggerUser: 'bingqin2',
        operation: 'touch',
        targetPath: 'docs/history.md',
        replacementText: null
      },
      result: touchCommand
    }
  ]));

  render(
    <DemoLaunchCommandPanel
      result={null}
      error={null}
      pending={false}
      onComposeCommand={vi.fn()}
      onApplyToPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  expect(within(panel).getByText('/agent fix touch docs/history.md')).toBeInTheDocument();

  await user.click(within(panel).getByRole('button', { name: 'Clear command history' }));
  expect(within(panel).getByText('No saved demo launch commands yet.')).toBeInTheDocument();
  expect(localStorage.getItem('patchpilot.demoLaunchCommandHistory')).toBeNull();
});

test('keeps only the five most recent saved demo launch commands', async () => {
  const user = userEvent.setup();
  const onComposeCommand = vi.fn(async (input: DemoLaunchCommandInput): Promise<DemoLaunchCommand> => ({
    triggerComment: `/agent fix touch ${input.targetPath}`,
    preflightInput: {
      repositoryOwner: input.repositoryOwner,
      repositoryName: input.repositoryName,
      issueNumber: input.issueNumber,
      triggerUser: input.triggerUser,
      triggerComment: `/agent fix touch ${input.targetPath}`
    },
    githubIssueUrl: `https://github.com/${input.repositoryOwner}/${input.repositoryName}/issues/${input.issueNumber}`,
    summary: `Prepared a demo /agent fix touch command for ${input.repositoryOwner}/${input.repositoryName}#${input.issueNumber}.`,
    nextActions: ['Run launch preflight with the generated command before posting it on GitHub.']
  }));

  render(
    <DemoLaunchCommandPanel
      result={null}
      error={null}
      pending={false}
      onComposeCommand={onComposeCommand}
      onApplyToPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  await user.selectOptions(within(panel).getByLabelText('Operation'), 'touch');
  for (let index = 1; index <= 6; index += 1) {
    await user.clear(within(panel).getByLabelText('Target path'));
    await user.type(within(panel).getByLabelText('Target path'), `docs/history-${index}.md`);
    await user.click(within(panel).getByRole('button', { name: 'Generate command' }));
  }

  const history = JSON.parse(localStorage.getItem('patchpilot.demoLaunchCommandHistory') ?? '[]');
  expect(history).toHaveLength(5);
  expect(history[0].result.triggerComment).toBe('/agent fix touch docs/history-6.md');
  expect(history[4].result.triggerComment).toBe('/agent fix touch docs/history-2.md');
  expect(within(panel).queryByText('/agent fix touch docs/history-1.md')).not.toBeInTheDocument();
});

test('copies and applies the generated command', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  const onApplyToPreflight = vi.fn();

  render(
    <DemoLaunchCommandPanel
      result={composedCommand}
      error={null}
      pending={false}
      onComposeCommand={vi.fn()}
      onApplyToPreflight={onApplyToPreflight}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  expect(within(panel).getByText('/agent fix replace docs/demo.md PatchPilot smoke test')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Open GitHub issue' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1'
  );

  await user.click(within(panel).getByRole('button', { name: 'Copy command' }));
  expect(writeText).toHaveBeenCalledWith('/agent fix replace docs/demo.md PatchPilot smoke test');

  await user.click(within(panel).getByRole('button', { name: 'Apply to launch preflight' }));
  expect(onApplyToPreflight).toHaveBeenCalledWith(composedCommand.preflightInput);
});

test('hides replacement text for touch commands', async () => {
  const user = userEvent.setup();

  render(
    <DemoLaunchCommandPanel
      result={null}
      error={null}
      pending={false}
      onComposeCommand={vi.fn()}
      onApplyToPreflight={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Demo launch command composer' });
  await user.selectOptions(within(panel).getByLabelText('Operation'), 'touch');

  expect(within(panel).queryByLabelText('Replacement text')).not.toBeInTheDocument();
});
