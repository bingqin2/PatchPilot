import { CheckCircle2, Copy, ExternalLink, RotateCcw, XCircle } from 'lucide-react';
import { useEffect, useState } from 'react';
import type { ApproveReviewInput, FixTask } from '../../types';
import { compactTime, duration, issueUrl } from '../format';
import type { TaskDetailState } from '../types';
import { RecordLine } from './RecordLine';
import { SummaryItem } from './SummaryItem';

interface TaskDetailPanelProps {
  task: FixTask | null;
  detail: TaskDetailState;
  loading: boolean;
  actionInFlight: boolean;
  reviewApprovalAllowedOperators: string[];
  onCancelTask: (taskId: string) => Promise<void>;
  onRetryTask: (taskId: string) => Promise<void>;
  onApproveReview: (taskId: string, input: ApproveReviewInput) => Promise<void>;
  onCopyReport: (taskId: string) => Promise<string>;
}

export function TaskDetailPanel({
  task,
  detail,
  loading,
  actionInFlight,
  reviewApprovalAllowedOperators,
  onCancelTask,
  onRetryTask,
  onApproveReview,
  onCopyReport
}: TaskDetailPanelProps) {
  const [copyStatus, setCopyStatus] = useState<string | null>(null);
  const [approvalOperator, setApprovalOperator] = useState('');
  const [approvalReason, setApprovalReason] = useState('');

  useEffect(() => {
    if (approvalOperator && !reviewApprovalAllowedOperators.includes(approvalOperator)) {
      setApprovalOperator('');
    }
  }, [approvalOperator, reviewApprovalAllowedOperators]);

  if (!task) {
    return (
      <section className="panel detail-panel">
        <h2>Task Detail</h2>
        <p className="empty-state">Select a task to inspect execution records.</p>
      </section>
    );
  }

  const canCancel = task.status === 'PENDING'
    || task.status === 'RUNNING'
    || task.status === 'RUNNING_TESTS'
    || task.status === 'PENDING_REVIEW';
  const canRetry = task.status === 'FAILED' || task.status === 'CANCELLED';
  const canApproveReview = task.status === 'PENDING_REVIEW';
  const reviewApprovalConfigured = reviewApprovalAllowedOperators.length > 0;
  const latestTestStatus = testStatus(detail.summary?.latestTestRunExitCode);
  const generatedDiffRiskBlocked = detail.toolCalls.some(
    (call) => call.toolName === 'GeneratedDiffRiskGate' && !call.success
  );
  const approvalInputReady = reviewApprovalConfigured
    && approvalOperator.trim().length > 0
    && approvalReason.trim().length > 0;

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

  async function approveReview() {
    if (!task || !approvalInputReady) {
      return;
    }
    await onApproveReview(task.id, {
      operator: approvalOperator.trim(),
      reason: approvalReason.trim()
    });
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
        {generatedDiffRiskBlocked ? <strong className="evidence-risk-blocked">Risk gate BLOCKED</strong> : null}
        {task.language && task.buildSystem ? (
          <strong>Adapter {task.language} / {task.buildSystem}</strong>
        ) : null}
        {task.adapterDetectionReason ? <strong>Detection {task.adapterDetectionReason}</strong> : null}
        {task.verificationCommand ? <strong>Verify {task.verificationCommand}</strong> : null}
      </div>

      {task.riskReviewApprovedAt ? (
        <section className="detail-section review-approval-detail" aria-label="Review approval">
          <h3>Review approval</h3>
          <div className="review-approval-grid">
            <div>
              <span>Approver</span>
              <strong>{task.riskReviewApprovedBy ?? 'unknown'}</strong>
            </div>
            <div>
              <span>Approved</span>
              <time dateTime={task.riskReviewApprovedAt}>{compactTime(task.riskReviewApprovedAt)}</time>
            </div>
          </div>
          {task.riskReviewApprovalReason ? <p>{task.riskReviewApprovalReason}</p> : null}
        </section>
      ) : null}

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

      {detail.generatedDiff ? (
        <section className="detail-section generated-diff-section">
          <div className="generated-diff-header">
            <div>
              <h3>Generated diff</h3>
              <p>
                {canApproveReview
                  ? 'Review these changes before approving the task.'
                  : 'Latest generated patch captured before commit or review.'}
              </p>
            </div>
            <time dateTime={detail.generatedDiff.generatedAt}>
              Generated {compactTime(detail.generatedDiff.generatedAt)}
            </time>
          </div>
          <pre aria-label="Generated diff preview">{detail.generatedDiff.diff}</pre>
        </section>
      ) : null}

      {canApproveReview ? (
        <section className="detail-section review-approval-form" aria-label="Review approval form">
          <h3>Review approval</h3>
          {!reviewApprovalConfigured ? (
            <p className="review-approval-warning">
              Configure review approval operators before approving this task.
            </p>
          ) : null}
          <label>
            <span>Approver</span>
            <select
              value={approvalOperator}
              onChange={(event) => setApprovalOperator(event.target.value)}
              disabled={!reviewApprovalConfigured}
            >
              <option value="">Select an approver</option>
              {reviewApprovalAllowedOperators.map((operator) => (
                <option value={operator} key={operator}>{operator}</option>
              ))}
            </select>
          </label>
          <label>
            <span>Approval reason</span>
            <textarea
              value={approvalReason}
              onChange={(event) => setApprovalReason(event.target.value)}
              placeholder="Summarize what was reviewed and why the task can continue."
              rows={3}
            />
          </label>
          <button
            className="secondary-button"
            type="button"
            disabled={actionInFlight || !approvalInputReady}
            onClick={() => void approveReview()}
          >
            <CheckCircle2 size={14} />
            Approve review
          </button>
        </section>
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
        <h3>Verification Output</h3>
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
        {!loading && detail.testRuns.length === 0 ? <p className="empty-state">No verification runs recorded.</p> : null}
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
  url.pathname = `/tasks/${encodeURIComponent(taskId)}`;
  url.searchParams.delete('taskId');
  return url.toString();
}
