import { AlertCircle, CheckCircle2, CircleDot, ExternalLink, GitPullRequest, Terminal } from 'lucide-react';
import type { FixTask, TaskStatus, TaskStatusFilter } from '../../types';
import { compactTime, issueUrl, pullRequestNumber } from '../format';

const statusFilters: TaskStatusFilter[] = [
  'ALL',
  'PENDING',
  'RUNNING',
  'RUNNING_TESTS',
  'COMPLETED',
  'FAILED',
  'CANCELLED'
];

interface TaskListPanelProps {
  tasks: FixTask[];
  selectedTask: FixTask | null;
  statusFilter: TaskStatusFilter;
  searchQuery: string;
  loading: boolean;
  totalCount: number;
  canLoadMore: boolean;
  loadingMore: boolean;
  onStatusFilterChange: (status: TaskStatusFilter) => void;
  onSearchQueryChange: (query: string) => void;
  onSelectTask: (taskId: string) => void;
  onLoadMoreTasks: () => void;
}

export function TaskListPanel({
  tasks,
  selectedTask,
  statusFilter,
  searchQuery,
  loading,
  totalCount,
  canLoadMore,
  loadingMore,
  onStatusFilterChange,
  onSearchQueryChange,
  onSelectTask,
  onLoadMoreTasks
}: TaskListPanelProps) {
  return (
    <section className="panel task-list-panel">
      <div className="panel-header">
        <div>
          <h2>Tasks</h2>
          <p>{loading ? 'Loading latest tasks' : `${tasks.length} of ${totalCount} tasks visible`}</p>
        </div>
      </div>
      <div className="filter-bar" aria-label="Task status filters">
        {statusFilters.map((status) => (
          <button
            className={status === statusFilter ? 'filter-button filter-button-active' : 'filter-button'}
            key={status}
            type="button"
            onClick={() => onStatusFilterChange(status)}
            aria-pressed={status === statusFilter}
          >
            {status}
          </button>
        ))}
      </div>
      <div className="task-search">
        <label htmlFor="task-search-input">Search tasks</label>
        <input
          id="task-search-input"
          type="search"
          value={searchQuery}
          onChange={(event) => onSearchQueryChange(event.target.value)}
          placeholder="Task, repository, issue, status, comment, failure"
        />
      </div>
      <div className="task-list">
        {tasks.map((task) => (
          <button
            className={task.id === selectedTask?.id ? 'task-row task-row-active' : 'task-row'}
            key={task.id}
            type="button"
            onClick={() => onSelectTask(task.id)}
          >
            <span className={`status-pill status-${task.status.toLowerCase().replace('_', '-')}`}>
              {statusIcon(task.status)}
              {task.status}
            </span>
            <span className="task-repo">{task.repositoryOwner}/{task.repositoryName}</span>
            <span className="task-issue">#{task.issueNumber}</span>
            <span className="task-comment">{task.triggerComment}</span>
            <span className="task-times">
              <time dateTime={task.createdAt}>Created {compactTime(task.createdAt)}</time>
              <time dateTime={task.updatedAt}>Updated {compactTime(task.updatedAt)}</time>
            </span>
            {task.failureReason ? <span className="task-failure">{task.failureReason}</span> : null}
            <a className="task-link" href={issueUrl(task)} target="_blank" rel="noreferrer">
              <ExternalLink size={14} />
              Open Issue
            </a>
            {task.statusCommentUrl ? (
              <a className="task-link" href={task.statusCommentUrl} target="_blank" rel="noreferrer">
                <ExternalLink size={14} />
                Status Comment
              </a>
            ) : null}
            {task.pullRequestUrl ? (
              <a className="task-link" href={task.pullRequestUrl} target="_blank" rel="noreferrer">
                <GitPullRequest size={14} />
                PR #{pullRequestNumber(task.pullRequestUrl)}
              </a>
            ) : null}
          </button>
        ))}
        {!loading && tasks.length === 0 ? (
          <p className="empty-state">
            {searchQuery.trim() ? `No tasks match "${searchQuery.trim()}".` : `No ${statusFilter} tasks found.`}
          </p>
        ) : null}
        {!loading && canLoadMore ? (
          <button className="load-more-button" type="button" onClick={onLoadMoreTasks} disabled={loadingMore}>
            {loadingMore ? 'Loading more tasks' : 'Load more tasks'}
          </button>
        ) : null}
      </div>
    </section>
  );
}

function statusIcon(status: TaskStatus) {
  if (status === 'COMPLETED') {
    return <CheckCircle2 size={14} />;
  }
  if (status === 'FAILED' || status === 'CANCELLED') {
    return <AlertCircle size={14} />;
  }
  if (status === 'RUNNING_TESTS') {
    return <Terminal size={14} />;
  }
  return <CircleDot size={14} />;
}
