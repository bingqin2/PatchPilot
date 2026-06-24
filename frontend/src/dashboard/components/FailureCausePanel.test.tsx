import { render, screen, within } from '@testing-library/react';
import { FailureCausePanel } from './FailureCausePanel';

test('renders stable failure categories with operator next actions', () => {
  render(
    <FailureCausePanel
      causes={[
        {
          cause: 'VERIFICATION_FAILED',
          count: 2,
          nextAction: 'Inspect the verification output, fix the failing test or build error, then retry the task.'
        },
        {
          cause: 'GITHUB_OPERATION_FAILED',
          count: 1,
          nextAction: 'Check GitHub token or App permissions, then retry the task after access is fixed.'
        }
      ]}
    />
  );

  const panel = screen.getByRole('region', { name: 'Failure causes' });
  expect(within(panel).getByText('Verification failed')).toBeInTheDocument();
  expect(within(panel).getByText('Inspect the verification output, fix the failing test or build error, then retry the task.')).toBeInTheDocument();
  expect(within(panel).getByText('GitHub operation failed')).toBeInTheDocument();
  expect(within(panel).getByText('Check GitHub token or App permissions, then retry the task after access is fixed.')).toBeInTheDocument();
});
