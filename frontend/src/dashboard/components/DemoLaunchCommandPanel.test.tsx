import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { DemoLaunchCommand } from '../../types';
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
