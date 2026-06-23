import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { RejectedTriggerPanel } from './RejectedTriggerPanel';

test('renders rejected trigger audit rows and retries a rejected trigger', async () => {
  const user = userEvent.setup();
  const onRetryRejectedTrigger = vi.fn();

  render(
    <RejectedTriggerPanel
      error={null}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={onRetryRejectedTrigger}
      rejectedTriggers={[
        {
          id: 'rejected-1',
          source: 'webhook',
          deliveryId: 'delivery-rejected',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'drive-by-user',
          triggerComment: '/agent fix make it better',
          reason: 'Unsafe request rejected: instruction is not actionable',
          commentId: 456,
          commentUrl: 'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456',
          createdAt: '2026-06-20T01:03:05Z'
        }
      ]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Rejected triggers' });
  expect(within(panel).getByText('1 recent rejection')).toBeInTheDocument();
  expect(within(panel).getByText('/agent fix make it better')).toBeInTheDocument();
  expect(within(panel).getByText('bingqin2/PatchPilot #1')).toBeInTheDocument();
  expect(within(panel).getByText('drive-by-user')).toBeInTheDocument();
  expect(within(panel).getByText('delivery-rejected')).toBeInTheDocument();
  expect(within(panel).getByText('Unsafe request rejected: instruction is not actionable')).toBeInTheDocument();
  expect(within(panel).getByRole('link', { name: 'Refusal comment' })).toHaveAttribute(
    'href',
    'https://github.com/bingqin2/PatchPilot/issues/1#issuecomment-456'
  );

  await user.click(within(panel).getByRole('button', { name: 'Retry trigger' }));

  expect(onRetryRejectedTrigger).toHaveBeenCalledWith('rejected-1');
});

test('renders rejected trigger empty and error states', () => {
  const { rerender } = render(
    <RejectedTriggerPanel
      error={null}
      rejectedTriggers={[]}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={vi.fn()}
    />
  );

  expect(screen.getByText('No recent rejections')).toBeInTheDocument();
  expect(screen.getByText('No rejected /agent fix triggers recorded.')).toBeInTheDocument();

  rerender(
    <RejectedTriggerPanel
      error="Rejected trigger API unavailable"
      rejectedTriggers={[]}
      retryingRejectedTriggerId={null}
      onRetryRejectedTrigger={vi.fn()}
    />
  );

  expect(screen.getByText('Rejected trigger API unavailable')).toBeInTheDocument();
});

test('disables the retry action while retrying a rejected trigger', () => {
  render(
    <RejectedTriggerPanel
      error={null}
      retryingRejectedTriggerId="rejected-1"
      onRetryRejectedTrigger={vi.fn()}
      rejectedTriggers={[
        {
          id: 'rejected-1',
          source: 'webhook',
          deliveryId: 'delivery-rejected',
          repositoryOwner: 'bingqin2',
          repositoryName: 'PatchPilot',
          issueNumber: 1,
          triggerUser: 'drive-by-user',
          triggerComment: '/agent fix make it better',
          reason: 'Unsafe request rejected: instruction is not actionable',
          commentId: null,
          commentUrl: null,
          createdAt: '2026-06-20T01:03:05Z'
        }
      ]}
    />
  );

  expect(screen.getByRole('button', { name: 'Retrying trigger' })).toBeDisabled();
});
