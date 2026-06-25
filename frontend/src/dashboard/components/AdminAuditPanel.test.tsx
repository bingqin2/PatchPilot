import { render, screen, within } from '@testing-library/react';
import { AdminAuditPanel } from './AdminAuditPanel';

test('renders recent admin audit events in a dedicated panel', () => {
  render(
    <AdminAuditPanel
      audits={[
        {
          id: 'admin-audit-1',
          action: 'TASK_RETRIED',
          resourceType: 'TASK',
          resourceId: 'task-123',
          scope: 'REPOSITORY',
          scopeKey: 'bingqin2/patchpilot',
          operator: 'admin-api',
          reason: 'Verified failure cause and requested a clean rerun',
          createdAt: '2026-06-24T02:00:00Z'
        },
        {
          id: 'admin-audit-2',
          action: 'DEMO_SESSION_ARCHIVED',
          resourceType: 'DEMO_SESSION_ARCHIVE',
          resourceId: 'archive-1',
          scope: 'REPOSITORY',
          scopeKey: 'patchpilot/local-demo',
          operator: 'admin-api',
          reason: 'Archived demo session demo-session-20260624T003000Z',
          createdAt: '2026-06-24T02:05:00Z'
        }
      ]}
      error={null}
    />
  );

  const panel = screen.getByRole('region', { name: 'Admin audit trail' });
  expect(within(panel).getByText('2 recent admin events')).toBeInTheDocument();
  expect(within(panel).getByText('Task retried')).toBeInTheDocument();
  expect(within(panel).getByText('task-123')).toBeInTheDocument();
  expect(within(panel).getByText('bingqin2/patchpilot')).toBeInTheDocument();
  expect(within(panel).getAllByText('admin-api')).toHaveLength(2);
  expect(within(panel).getByText('Verified failure cause and requested a clean rerun')).toBeInTheDocument();
  expect(within(panel).getByText('Demo session archived')).toBeInTheDocument();
  expect(within(panel).getByText('archive-1')).toBeInTheDocument();
});

test('renders admin audit empty and error states', () => {
  render(<AdminAuditPanel audits={[]} error="Admin token is required" />);

  const panel = screen.getByRole('region', { name: 'Admin audit trail' });
  expect(within(panel).getByText('0 recent admin events')).toBeInTheDocument();
  expect(within(panel).getByText('Admin token is required')).toBeInTheDocument();
  expect(within(panel).getByText('No admin actions recorded.')).toBeInTheDocument();
});
