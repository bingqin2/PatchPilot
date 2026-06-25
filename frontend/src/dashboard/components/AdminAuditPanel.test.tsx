import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AdminAuditPanel } from './AdminAuditPanel';

const adminAudits = [
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
] as const;

test('renders recent admin audit events in a dedicated panel', () => {
  render(
    <AdminAuditPanel
      audits={[...adminAudits]}
      error={null}
      filters={{ limit: 20 }}
      onFiltersChange={vi.fn()}
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
  render(
    <AdminAuditPanel
      audits={[]}
      error="Admin token is required"
      filters={{ limit: 20 }}
      onFiltersChange={vi.fn()}
    />
  );

  const panel = screen.getByRole('region', { name: 'Admin audit trail' });
  expect(within(panel).getByText('0 recent admin events')).toBeInTheDocument();
  expect(within(panel).getByText('Admin token is required')).toBeInTheDocument();
  expect(within(panel).getByText('No admin actions recorded.')).toBeInTheDocument();
});

test('submits and clears admin audit filters', async () => {
  const user = userEvent.setup();
  const onFiltersChange = vi.fn();
  render(
    <AdminAuditPanel
      audits={[...adminAudits]}
      error={null}
      filters={{ limit: 20 }}
      onFiltersChange={onFiltersChange}
    />
  );

  await user.type(screen.getByLabelText('Admin audit action'), 'TASK_RETRIED');
  await user.type(screen.getByLabelText('Admin audit operator'), 'admin-api');
  await user.type(screen.getByLabelText('Admin audit resource type'), 'TASK');
  await user.type(screen.getByLabelText('Admin audit resource id'), 'task-123');
  await user.type(screen.getByLabelText('Admin audit scope key'), 'bingqin2/patchpilot');
  await user.click(screen.getByRole('button', { name: 'Apply admin audit filters' }));

  expect(onFiltersChange).toHaveBeenLastCalledWith({
    limit: 20,
    action: 'TASK_RETRIED',
    resourceType: 'TASK',
    resourceId: 'task-123',
    scopeKey: 'bingqin2/patchpilot',
    operator: 'admin-api'
  });

  await user.click(screen.getByRole('button', { name: 'Clear admin audit filters' }));

  expect(onFiltersChange).toHaveBeenLastCalledWith({ limit: 20 });
});

test('copies the visible admin audit report', async () => {
  const user = userEvent.setup();
  const writeText = vi.fn().mockResolvedValue(undefined);
  Object.defineProperty(navigator, 'clipboard', {
    configurable: true,
    value: { writeText }
  });
  render(
    <AdminAuditPanel
      audits={[...adminAudits]}
      error={null}
      filters={{ limit: 20, action: 'TASK_RETRIED' }}
      onFiltersChange={vi.fn()}
    />
  );

  await user.click(screen.getByRole('button', { name: 'Copy admin audit report' }));

  expect(writeText).toHaveBeenCalledWith(
    expect.stringContaining('# PatchPilot Admin Audit Report\n\n- Filters: `limit=20, action=TASK_RETRIED`')
  );
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- `TASK_RETRIED` TASK task-123'));
  expect(writeText).toHaveBeenCalledWith(expect.stringContaining('- `DEMO_SESSION_ARCHIVED` DEMO_SESSION_ARCHIVE archive-1'));
});
