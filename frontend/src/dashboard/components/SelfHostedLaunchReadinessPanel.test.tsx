import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { SelfHostedLaunchReadinessPanel } from './SelfHostedLaunchReadinessPanel';
import type { DemoSelfHostedLaunchReadiness } from '../../types';

const launchReadiness: DemoSelfHostedLaunchReadiness = {
  status: 'NEEDS_ATTENTION',
  readyToLaunch: false,
  summary: 'Self-hosted PatchPilot needs attention before launch.',
  checks: [
    {
      name: 'Demo readiness',
      status: 'READY',
      message: 'PatchPilot is ready for a controlled demo.',
      action: 'No action needed.'
    },
    {
      name: 'Handoff finalization',
      status: 'NEEDS_ATTENTION',
      message: 'Demo handoff package is send-ready but final delivery evidence is not current.',
      action: 'Send the current handoff package, record a delivery receipt, then download the finalization report.'
    }
  ],
  nextActions: [
    'Send the current handoff package, record a delivery receipt, then download the finalization report.',
    'Resolve launch package warnings, then rerun this readiness package.'
  ],
  generatedAt: '2026-06-27T01:00:00Z',
  markdownReport: '# PatchPilot Self-Hosted Launch Readiness'
};

test('renders self-hosted launch readiness checks and next actions', () => {
  render(
    <SelfHostedLaunchReadinessPanel
      readiness={launchReadiness}
      error={null}
      onDownloadReport={async () => new Blob(['report'], { type: 'text/markdown' })}
    />
  );

  const panel = screen.getByRole('region', { name: 'Self-hosted launch readiness' });
  expect(within(panel).getByText('Self-hosted launch readiness')).toBeInTheDocument();
  expect(within(panel).getAllByText('Needs attention').length).toBeGreaterThanOrEqual(1);
  expect(within(panel).getByText('Not ready')).toBeInTheDocument();
  expect(within(panel).getByText('Handoff finalization')).toBeInTheDocument();
  expect(within(panel).getByText('Demo handoff package is send-ready but final delivery evidence is not current.')).toBeInTheDocument();
  expect(within(panel).getByText('Resolve launch package warnings, then rerun this readiness package.')).toBeInTheDocument();
});

test('downloads self-hosted launch readiness markdown report', async () => {
  const click = vi.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(() => {});
  const createObjectURL = vi.fn(() => 'blob:self-hosted-launch-readiness');
  const revokeObjectURL = vi.fn();
  vi.stubGlobal('URL', { createObjectURL, revokeObjectURL });
  const onDownloadReport = vi.fn(async () => new Blob(['report'], { type: 'text/markdown' }));

  render(
    <SelfHostedLaunchReadinessPanel
      readiness={launchReadiness}
      error={null}
      onDownloadReport={onDownloadReport}
    />
  );

  await userEvent.click(screen.getByRole('button', { name: 'Download launch readiness report' }));

  expect(onDownloadReport).toHaveBeenCalled();
  expect(createObjectURL).toHaveBeenCalled();
  expect(click).toHaveBeenCalled();
  expect(revokeObjectURL).toHaveBeenCalledWith('blob:self-hosted-launch-readiness');
  expect(screen.getByText('Launch readiness report downloaded')).toBeInTheDocument();

  vi.unstubAllGlobals();
  click.mockRestore();
});
