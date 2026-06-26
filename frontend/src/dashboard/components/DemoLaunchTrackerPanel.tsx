import { Archive, Clipboard, Trash2 } from 'lucide-react';
import { useState } from 'react';
import type { DemoPreparedLaunchCommand, FixTask, WebhookDeliveryDiagnostic } from '../../types';
import {
  addDemoLaunchOutcomeArchiveItem,
  loadDemoLaunchOutcomeArchive,
  persistDemoLaunchOutcomeArchive,
  type DemoLaunchOutcomeArchiveItem
} from '../demoLaunchOutcomeArchive';

interface DemoLaunchTrackerPanelProps {
  preparedLaunchCommands: DemoPreparedLaunchCommand[];
  tasks: FixTask[];
  webhookDeliveries: WebhookDeliveryDiagnostic[];
}

interface TrackedLaunch {
  command: DemoPreparedLaunchCommand;
  task: FixTask | null;
  webhookDelivery: WebhookDeliveryDiagnostic | null;
  status: 'WAITING_FOR_WEBHOOK' | 'WEBHOOK_RECEIVED' | 'TASK_RUNNING' | 'TASK_FAILED' | 'TASK_COMPLETED';
  nextAction: string;
}

export function DemoLaunchTrackerPanel({
  preparedLaunchCommands,
  tasks,
  webhookDeliveries
}: DemoLaunchTrackerPanelProps) {
  const [archive, setArchive] = useState<DemoLaunchOutcomeArchiveItem[]>(loadDemoLaunchOutcomeArchive);
  const trackedLaunches = preparedLaunchCommands.slice(0, 5).map((command) => trackLaunch(command, tasks, webhookDeliveries));

  function archiveOutcome(launch: TrackedLaunch) {
    const item = createArchiveItem(launch);
    setArchive((currentArchive) => {
      const updatedArchive = addDemoLaunchOutcomeArchiveItem(currentArchive, item);
      persistDemoLaunchOutcomeArchive(updatedArchive);
      return updatedArchive;
    });
  }

  function clearArchive() {
    setArchive([]);
    persistDemoLaunchOutcomeArchive([]);
  }

  return (
    <section className="panel demo-launch-tracker-panel" aria-label="Demo launch tracker">
      <div className="panel-header">
        <div>
          <h2>Demo launch tracker</h2>
          <p>
            {trackedLaunches.length === 0
              ? 'No prepared launch commands'
              : `${trackedLaunches.length} prepared launch command${trackedLaunches.length === 1 ? '' : 's'} tracked`}
          </p>
        </div>
      </div>
      {trackedLaunches.length ? (
        <div className="demo-launch-tracker-list">
          {trackedLaunches.map((launch) => (
            <TrackedLaunchRow
              launch={launch}
              key={`${launch.command.triggerComment}-${launch.command.savedAt}`}
              onArchiveOutcome={archiveOutcome}
            />
          ))}
        </div>
      ) : (
        <p className="empty-state">No prepared launch commands recorded in this browser.</p>
      )}
      <DemoLaunchOutcomeArchive archive={archive} onClearArchive={clearArchive} />
    </section>
  );
}

function TrackedLaunchRow({
  launch,
  onArchiveOutcome
}: {
  launch: TrackedLaunch;
  onArchiveOutcome: (launch: TrackedLaunch) => void;
}) {
  return (
    <article className="demo-launch-tracker-row">
      <div className="demo-launch-tracker-command">
        <code>{launch.command.triggerComment}</code>
        <span>{repositoryLabel(launch.command)}</span>
      </div>
      <div className="demo-launch-tracker-steps">
        <TrackerStep label="Webhook" value={launch.webhookDelivery ? 'Webhook received' : 'Waiting for webhook'} tone={launch.webhookDelivery ? 'completed' : 'pending'} />
        <TrackerStep label="Task" value={taskStatusLabel(launch)} tone={taskStatusTone(launch)} />
        <TrackerStep label="PR" value={launch.task?.pullRequestUrl ? 'Pull Request ready' : 'Pull Request pending'} tone={launch.task?.pullRequestUrl ? 'completed' : 'pending'} />
      </div>
      <div className="demo-launch-tracker-links">
        {launch.task ? <a href={`/tasks/${launch.task.id}`}>Open task {launch.task.id}</a> : null}
        {launch.task?.pullRequestUrl ? <a href={launch.task.pullRequestUrl}>Open Pull Request</a> : null}
        <button
          className="secondary-button"
          type="button"
          onClick={() => void copyOutcomeReport(launch)}
          aria-label={`Copy outcome report for ${launch.command.triggerComment}`}
        >
          <Clipboard size={16} />
          Copy outcome report
        </button>
        <button
          className="secondary-button"
          type="button"
          onClick={() => onArchiveOutcome(launch)}
          aria-label={`Archive outcome for ${launch.command.triggerComment}`}
        >
          <Archive size={16} />
          Archive outcome
        </button>
      </div>
      <p>{launch.nextAction}</p>
    </article>
  );
}

function DemoLaunchOutcomeArchive({
  archive,
  onClearArchive
}: {
  archive: DemoLaunchOutcomeArchiveItem[];
  onClearArchive: () => void;
}) {
  return (
    <section className="demo-launch-outcome-archive" aria-label="Demo launch outcome archive">
      <div className="demo-launch-command-history-header">
        <div>
          <h3>Demo launch outcome archive</h3>
          <p>
            {archive.length > 0
              ? `${archive.length} outcome${archive.length === 1 ? '' : 's'} saved locally in this browser.`
              : 'Saved only in this browser.'}
          </p>
        </div>
        {archive.length > 0 ? (
          <button className="secondary-button" type="button" onClick={onClearArchive}>
            <Trash2 size={16} />
            Clear outcome archive
          </button>
        ) : null}
      </div>

      {archive.length > 0 ? (
        <ul aria-label="Archived demo launch outcomes">
          {archive.map((item) => (
            <li key={item.id}>
              <div className="demo-launch-command-history-copy">
                <code>{item.triggerComment}</code>
                <span>
                  {item.repositoryOwner}/{item.repositoryName} #{item.issueNumber} - {item.taskStatus} - Archived at{' '}
                  {formatArchiveTimestamp(item.archivedAt)}
                </span>
              </div>
              <div className="demo-launch-command-history-actions">
                {item.taskId ? <a href={`/tasks/${item.taskId}`}>Open archived task {item.taskId}</a> : null}
                {item.pullRequestUrl ? <a href={item.pullRequestUrl}>Open archived Pull Request</a> : null}
                <button
                  className="secondary-button"
                  type="button"
                  onClick={() => void copyArchivedOutcomeReport(item)}
                  aria-label={`Copy archived outcome report ${item.taskId ?? item.id}`}
                >
                  <Clipboard size={16} />
                  Copy archived report
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="empty-state compact-empty-state">No archived demo launch outcomes yet.</p>
      )}
    </section>
  );
}

function TrackerStep({ label, value, tone }: { label: string; value: string; tone: 'completed' | 'failed' | 'pending' }) {
  return (
    <div>
      <span>{label}</span>
      <strong className={`status-pill status-${tone}`}>{value}</strong>
    </div>
  );
}

function trackLaunch(
  command: DemoPreparedLaunchCommand,
  tasks: FixTask[],
  webhookDeliveries: WebhookDeliveryDiagnostic[]
): TrackedLaunch {
  const task = tasks.find((candidate) => matchesLaunch(command, candidate)) ?? null;
  const webhookDelivery = webhookDeliveries.find((candidate) => (
    matchesLaunch(command, candidate) || (task !== null && candidate.taskId === task.id)
  )) ?? null;
  const status = launchStatus(task, webhookDelivery);
  return {
    command,
    task,
    webhookDelivery,
    status,
    nextAction: nextAction(status)
  };
}

function matchesLaunch(
  command: DemoPreparedLaunchCommand,
  candidate: Pick<FixTask, 'repositoryOwner' | 'repositoryName' | 'issueNumber' | 'triggerUser' | 'triggerComment'>
    | Pick<WebhookDeliveryDiagnostic, 'repositoryOwner' | 'repositoryName' | 'issueNumber' | 'triggerUser' | 'triggerComment'>
) {
  return (
    candidate.repositoryOwner === command.repositoryOwner &&
    candidate.repositoryName === command.repositoryName &&
    candidate.issueNumber === command.issueNumber &&
    candidate.triggerUser === command.triggerUser &&
    candidate.triggerComment === command.triggerComment
  );
}

function launchStatus(task: FixTask | null, webhookDelivery: WebhookDeliveryDiagnostic | null): TrackedLaunch['status'] {
  if (task?.status === 'COMPLETED') {
    return 'TASK_COMPLETED';
  }
  if (task?.status === 'FAILED' || task?.status === 'CANCELLED') {
    return 'TASK_FAILED';
  }
  if (task) {
    return 'TASK_RUNNING';
  }
  if (webhookDelivery) {
    return 'WEBHOOK_RECEIVED';
  }
  return 'WAITING_FOR_WEBHOOK';
}

function nextAction(status: TrackedLaunch['status']) {
  switch (status) {
    case 'TASK_COMPLETED':
      return 'Launch succeeded. Open the Pull Request and review the generated patch.';
    case 'TASK_FAILED':
      return 'Launch reached task execution but failed. Open the task detail and inspect failure diagnosis.';
    case 'TASK_RUNNING':
      return 'Launch created a task. Refresh until verification, Pull Request, or failure evidence appears.';
    case 'WEBHOOK_RECEIVED':
      return 'Webhook reached PatchPilot. Check task creation or rejected-trigger evidence if no task appears.';
    case 'WAITING_FOR_WEBHOOK':
      return 'Post or redeliver the prepared GitHub issue comment, then refresh the dashboard.';
  }
}

function taskStatusLabel(launch: TrackedLaunch) {
  if (!launch.task) {
    return 'Task pending';
  }
  if (launch.task.status === 'COMPLETED') {
    return 'Task completed';
  }
  if (launch.task.status === 'FAILED') {
    return 'Task failed';
  }
  if (launch.task.status === 'CANCELLED') {
    return 'Task cancelled';
  }
  return `Task ${launch.task.status.toLowerCase().replaceAll('_', ' ')}`;
}

function taskStatusTone(launch: TrackedLaunch): 'completed' | 'failed' | 'pending' {
  if (!launch.task) {
    return 'pending';
  }
  if (launch.task.status === 'FAILED' || launch.task.status === 'CANCELLED') {
    return 'failed';
  }
  if (launch.task.status === 'COMPLETED') {
    return 'completed';
  }
  return 'pending';
}

function repositoryLabel(command: DemoPreparedLaunchCommand) {
  return `${command.repositoryOwner}/${command.repositoryName} #${command.issueNumber}`;
}

async function copyOutcomeReport(launch: TrackedLaunch) {
  await navigator.clipboard?.writeText(buildDemoLaunchOutcomeReport(launch));
}

async function copyArchivedOutcomeReport(item: DemoLaunchOutcomeArchiveItem) {
  await navigator.clipboard?.writeText(item.report);
}

function createArchiveItem(launch: TrackedLaunch): DemoLaunchOutcomeArchiveItem {
  const archivedAt = new Date().toISOString();
  return {
    id: `${archivedAt}-${launch.task?.id ?? launch.command.triggerComment}`,
    archivedAt,
    repositoryOwner: launch.command.repositoryOwner,
    repositoryName: launch.command.repositoryName,
    issueNumber: launch.command.issueNumber,
    triggerUser: launch.command.triggerUser,
    triggerComment: launch.command.triggerComment,
    taskId: launch.task?.id ?? null,
    taskStatus: launch.task?.status ?? 'PENDING',
    pullRequestUrl: launch.task?.pullRequestUrl ?? null,
    report: buildDemoLaunchOutcomeReport(launch)
  };
}

function buildDemoLaunchOutcomeReport(launch: TrackedLaunch) {
  return [
    '# PatchPilot Demo Launch Outcome Report',
    '',
    `- Repository: \`${launch.command.repositoryOwner}/${launch.command.repositoryName}\``,
    `- Issue: \`#${launch.command.issueNumber}\``,
    `- Trigger user: \`${launch.command.triggerUser}\``,
    `- Command: \`${launch.command.triggerComment}\``,
    `- Prepared at: \`${launch.command.savedAt}\``,
    `- Launch status: \`${launch.status}\``,
    '',
    '## Webhook Evidence',
    `- Webhook status: \`${launch.webhookDelivery?.status ?? 'NOT_RECEIVED'}\``,
    `- Webhook delivery id: \`${launch.webhookDelivery?.deliveryId ?? 'none'}\``,
    `- Webhook outcome: \`${launch.webhookDelivery?.outcomeType ?? 'none'}\``,
    `- Webhook message: ${launch.webhookDelivery?.message ?? 'No webhook delivery matched this prepared command.'}`,
    '',
    '## Task Evidence',
    `- Task: \`${launch.task?.id ?? 'none'}\``,
    `- Task status: \`${launch.task?.status ?? 'PENDING'}\``,
    `- Failure reason: ${launch.task?.failureReason ?? 'none'}`,
    `- Completed at: \`${launch.task?.completedAt ?? 'none'}\``,
    `- Pull Request: ${launch.task?.pullRequestUrl ?? 'none'}`,
    '',
    '## Operator Action',
    `- Next action: ${launch.nextAction}`
  ].join('\n');
}

function formatArchiveTimestamp(archivedAt: string) {
  const timestamp = new Date(archivedAt);
  if (Number.isNaN(timestamp.getTime())) {
    return 'saved locally';
  }
  return timestamp.toLocaleString();
}
