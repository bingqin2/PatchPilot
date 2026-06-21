import { Copy, ExternalLink, RotateCcw, XCircle } from 'lucide-react';
import { useState } from 'react';
import type { FixTask } from '../../types';
import { compactTime, duration, issueUrl } from '../format';
import type { TaskDetailState } from '../types';
import { RecordLine } from './RecordLine';
import { SummaryItem } from './SummaryItem';

interface TaskDetailPanelProps {
  task: FixTask | null;
  detail: TaskDetailState;
  loading: boolean;
  actionInFlight: boolean;
  onCancelTask: (taskId: string) => Promise<void>;
  onRetryTask: (taskId: string) => Promise<void>;
  onCopyReport: (taskId: string) => Promise<string>;
}

export function TaskDetailPanel({
  task,
  detail,
  loading,
  actionInFlight,
  onCancelTask,
  onRetryTask,
  onCopyReport
}: TaskDetailPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);

  if (!task) {
    return (
      <section className="panel detail-panel">
        <h2>Task Detail</h2>
        <p className="empty-state">Select a task to inspect execution records.</p>
      </section>
    );
  }

  const canCancel = task.status === 'PENDING' || task.status === 'RUNNING' || task.status === 'RUNNING_TESTS';
  const canRetry = task.status === 'FAILED' || task.status === 'CANCELLED';
  const latestTestStatus = testStatus(detail.summary?.latestTestRunExitCode);

  async function copyTaskLink() {
    if (!task) {
      return;
    }

    try {
      await navigator.clipboard.writeText(taskLinkFor(task.id));
      setCopyStatus('Task link copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  async function copyTaskReport() {
    if (!task) {
      return;
    }

    try {
      const report = await onCopyReport(task.id);
      await navigator.clipboard.writeText(report);
      setCopyStatus('Task report copied');
    } catch {
      setCopyStatus('Copy failed');
    }
  }

  return (
    <section className="panel detail-panel">
      <div className="panel-header">
        <div>
          <h2>{task.repositoryOwner}/{task.repositoryName} #{task.issueNumber}</h2>
          <p>{task.id}</p>
        </div>
        <div className="detail-actions">
          <button className="secondary-button" type="button" onClick={() => void copyTaskLink()}>
            <Copy size={14} />
            Copy link
          </button>
          <button className="secondary-button" type="button" onClick={() => void copyTaskReport()}>
            <Copy size={14} />
            Copy report
          </button>
          {copyStatus ? <span className="copy-status">{copyStatus}</span> : null}
          <a className="external-link" href={issueUrl(task)} target="_blank" rel="noreferrer">
            Open Issue
            <ExternalLink size={14} />
          </a>
          {task.statusCommentUrl ? (
            <a className="external-link" href={task.statusCommentUrl} target="_blank" rel="noreferrer">
              Status Comment
              <ExternalLink size={14} />
            </a>
          ) : null}
          {canCancel ? (
            <button
              className="secondary-button danger-button"
              type="button"
              disabled={actionInFlight}
              onClick={() => void onCancelTask(task.id)}
            >
              <XCircle size={14} />
              Cancel task
            </button>
          ) : null}
          {canRetry ? (
            <button
              className="secondary-button"
              type="button"
              disabled={actionInFlight}
              onClick={() => void onRetryTask(task.id)}
            >
              <RotateCcw size={14} />
              Retry task
            </button>
          ) : null}
          {task.pullRequestUrl ? (
            <a className="external-link" href={task.pullRequestUrl} target="_blank" rel="noreferrer">
              Open PR
              <ExternalLink size={14} />
            </a>
          ) : null}
        </div>
      </div>

      <div className="detail-summary">
        <SummaryItem label="Timeline" value={detail.summary?.timelineEventCount ?? 0} />
        <SummaryItem label="Tests" value={detail.summary?.testRunCount ?? 0} />
        <SummaryItem label="Tools" value={detail.summary?.toolCallCount ?? 0} />
        <SummaryItem label="Models" value={detail.summary?.modelCallCount ?? 0} />
        <SummaryItem label="Tokens" value={detail.summary?.totalModelTokens ?? 0} />
      </div>

      <div className="evidence-summary" aria-label="Execution evidence">
        <span>Execution evidence</span>
        <strong>Timeline {detail.summary?.timelineEventCount ?? 0}</strong>
        <strong>Tests {detail.summary?.testRunCount ?? 0}</strong>
        <strong>Tools {detail.summary?.toolCallCount ?? 0}</strong>
        <strong>Model calls {detail.summary?.modelCallCount ?? 0}</strong>
        <strong className={`evidence-test evidence-test-${latestTestStatus.toLowerCase()}`}>
          Latest test {latestTestStatus}
        </strong>
      </div>

      {detail.queueItem ? (
        <div className="queue-detail">
          <div>
            <span>Queue {detail.queueItem.status}</span>
            <strong>attempt {detail.queueItem.attemptCount}</strong>
          </div>
          <div>
            <time dateTime={detail.queueItem.availableAt}>Available {compactTime(detail.queueItem.availableAt)}</time>
            {detail.queueItem.lockedAt ? (
              <time dateTime={detail.queueItem.lockedAt}>Locked {compactTime(detail.queueItem.lockedAt)}</time>
            ) : null}
          </div>
          {detail.queueItem.lastError ? <p>{detail.queueItem.lastError}</p> : null}
        </div>
      ) : null}

      {detail.summary?.latestTimelineEvent ? (
        <div className="latest-event">
          <span>Latest event</span>
          <strong>{detail.summary.latestTimelineEvent.eventType}</strong>
          <p>{detail.summary.latestTimelineEvent.message}</p>
        </div>
      ) : null}

      {detail.queueItems.length > 0 ? (
        <section className="detail-section">
          <h3>Queue History</h3>
          <div className="queue-history">
            {detail.queueItems.map((item) => (
              <RecordLine
                key={item.id}
                title={item.id}
                meta={`${item.status} · attempt ${item.attemptCount}`}
                body={queueItemDescription(item)}
              />
            ))}
          </div>
        </section>
      ) : null}

      {loading ? <p className="empty-state">Loading task records...</p> : null}

      <section className="detail-section">
        <h3>Timeline</h3>
        <div className="timeline">
          {detail.timeline.map((event) => (
            <div className="timeline-item" key={event.id}>
              <span className="timeline-dot" />
              <div>
                <strong>{event.eventType}</strong>
                <p>{event.message}</p>
              </div>
              <time>{compactTime(event.createdAt)}</time>
            </div>
          ))}
          {!loading && detail.timeline.length === 0 ? <p className="empty-state">No timeline events recorded.</p> : null}
        </div>
      </section>

      <section className="detail-section">
        <h3>Maven Test Output</h3>
        {detail.testRuns.map((run) => (
          <div className="test-run" key={run.id}>
            <div className="test-run-header">
              <span className={run.exitCode === 0 ? 'result-pass' : 'result-fail'}>exit {run.exitCode}</span>
              <span>{run.command}</span>
              <span>{duration(run.durationMs)}</span>
            </div>
            <pre>{run.output}</pre>
          </div>
        ))}
        {!loading && detail.testRuns.length === 0 ? <p className="empty-state">No Maven test runs recorded.</p> : null}
      </section>

      <section className="detail-section split-section">
        <div>
          <h3>Tool Calls</h3>
          {detail.toolCalls.map((call) => (
            <RecordLine
              key={call.id}
              title={call.toolName}
              meta={`${call.success ? 'success' : 'failed'} · ${duration(call.durationMs)}`}
              body={call.outputSummary}
            />
          ))}
          {!loading && detail.toolCalls.length === 0 ? <p className="empty-state">No tool calls recorded.</p> : null}
        </div>
        <div>
          <h3>Model Calls</h3>
          {detail.modelCalls.map((call) => (
            <RecordLine
              key={call.id}
              title={call.model}
              meta={`${call.totalTokens} tokens · ${duration(call.durationMs)}`}
              body={call.responseSummary}
            />
          ))}
          {!loading && detail.modelCalls.length === 0 ? <p className="empty-state">No model calls recorded.</p> : null}
        </div>
      </section>
    </section>
  );
}

function testStatus(exitCode: number | null | undefined) {
  if (exitCode === null || exitCode === undefined) {
    return 'None';
  }
  return exitCode === 0 ? 'PASS' : 'FAIL';
}

function queueItemDescription(item: TaskDetailState['queueItems'][number]) {
  const parts = [`Available ${compactTime(item.availableAt)}`];
  if (item.lockedAt) {
    parts.push(`Locked ${compactTime(item.lockedAt)}`);
  }
  if (item.lastError) {
    parts.push(item.lastError);
  }
  return parts.join(' · ');
}

export function taskLinkFor(taskId: string, href = window.location.href) {
  const url = new URL(href);
  url.searchParams.set('taskId', taskId);
  return url.toString();
}
