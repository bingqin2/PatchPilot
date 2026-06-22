import { AlertCircle, CheckCircle2, CircleDot, ExternalLink, GitPullRequest, Terminal } from 'lucide-react';
import type { FixTask, FixTaskStatusCounts, TaskSort, TaskStatus, TaskStatusFilter } from '../../types';
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
  repositoryOwnerFilter: string;
  repositoryNameFilter: string;
  languageFilter: string;
  buildSystemFilter: string;
  createdAfterFilter: string;
  createdBeforeFilter: string;
  statusCounts: FixTaskStatusCounts | null;
  taskSort: TaskSort;
  loading: boolean;
  totalCount: number;
  canLoadMore: boolean;
  loadingMore: boolean;
  canClearFilters: boolean;
  onStatusFilterChange: (status: TaskStatusFilter) => void;
  onSearchQueryChange: (query: string) => void;
  onRepositoryOwnerFilterChange: (repositoryOwner: string) => void;
  onRepositoryNameFilterChange: (repositoryName: string) => void;
  onLanguageFilterChange: (language: string) => void;
  onBuildSystemFilterChange: (buildSystem: string) => void;
  onCreatedAfterFilterChange: (createdAfter: string) => void;
  onCreatedBeforeFilterChange: (createdBefore: string) => void;
  onTaskSortChange: (sort: TaskSort) => void;
  onClearFilters: () => void;
  onSelectTask: (taskId: string) => void;
  onLoadMoreTasks: () => void;
}

export function TaskListPanel({
  tasks,
  selectedTask,
  statusFilter,
  searchQuery,
  repositoryOwnerFilter,
  repositoryNameFilter,
  languageFilter,
  buildSystemFilter,
  createdAfterFilter,
  createdBeforeFilter,
  statusCounts,
  taskSort,
  loading,
  totalCount,
  canLoadMore,
  loadingMore,
  canClearFilters,
  onStatusFilterChange,
  onSearchQueryChange,
  onRepositoryOwnerFilterChange,
  onRepositoryNameFilterChange,
  onLanguageFilterChange,
  onBuildSystemFilterChange,
  onCreatedAfterFilterChange,
  onCreatedBeforeFilterChange,
  onTaskSortChange,
  onClearFilters,
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
            aria-label={status}
          >
            <span>{status}</span>
            <span className="filter-count" aria-hidden="true">{statusCount(statusCounts, status)}</span>
          </button>
        ))}
      </div>
      <div className="task-search">
        <label className="task-search-label" htmlFor="task-search-input">Search tasks</label>
        <input
          className="task-search-input"
          id="task-search-input"
          type="search"
          value={searchQuery}
          onChange={(event) => onSearchQueryChange(event.target.value)}
          placeholder="Task, repository, issue, status, comment, failure"
        />
        <label className="task-repository-owner-label" htmlFor="task-repository-owner-input">Filter repository owner</label>
        <input
          className="task-repository-owner-input"
          id="task-repository-owner-input"
          value={repositoryOwnerFilter}
          onChange={(event) => onRepositoryOwnerFilterChange(event.target.value)}
          placeholder="bingqin2"
        />
        <label className="task-repository-name-label" htmlFor="task-repository-name-input">Filter repository name</label>
        <input
          className="task-repository-name-input"
          id="task-repository-name-input"
          value={repositoryNameFilter}
          onChange={(event) => onRepositoryNameFilterChange(event.target.value)}
          placeholder="PatchPilot"
        />
        <label className="task-language-label" htmlFor="task-language-input">Filter language</label>
        <input
          className="task-language-input"
          id="task-language-input"
          value={languageFilter}
          onChange={(event) => onLanguageFilterChange(event.target.value)}
          placeholder="node"
        />
        <label className="task-build-system-label" htmlFor="task-build-system-input">Filter build system</label>
        <input
          className="task-build-system-input"
          id="task-build-system-input"
          value={buildSystemFilter}
          onChange={(event) => onBuildSystemFilterChange(event.target.value)}
          placeholder="npm"
        />
        <label className="task-created-after-label" htmlFor="task-created-after-input">Filter created after</label>
        <input
          className="task-created-after-input"
          id="task-created-after-input"
          value={createdAfterFilter}
          onChange={(event) => onCreatedAfterFilterChange(event.target.value)}
          placeholder="2026-06-20T01:00:00Z"
        />
        <label className="task-created-before-label" htmlFor="task-created-before-input">Filter created before</label>
        <input
          className="task-created-before-input"
          id="task-created-before-input"
          value={createdBeforeFilter}
          onChange={(event) => onCreatedBeforeFilterChange(event.target.value)}
          placeholder="2026-06-21T01:00:00Z"
        />
        <label className="task-sort-label" htmlFor="task-sort-select">Sort tasks</label>
        <select
          className="task-sort-select"
          id="task-sort-select"
          value={taskSort}
          onChange={(event) => onTaskSortChange(event.target.value as TaskSort)}
        >
          <option value="createdAtDesc">Newest first</option>
          <option value="createdAtAsc">Oldest first</option>
        </select>
        {canClearFilters ? (
          <button className="secondary-button task-clear-filters-button" type="button" onClick={onClearFilters}>
            Clear filters
          </button>
        ) : null}
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
            {task.language && task.buildSystem ? (
              <span className="task-adapter">
                <strong>{task.language} / {task.buildSystem}</strong>
                {task.verificationCommand ? <span>{task.verificationCommand}</span> : null}
              </span>
            ) : null}
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
            {emptyTaskListMessage(
              statusFilter,
              searchQuery,
              repositoryOwnerFilter,
              repositoryNameFilter,
              languageFilter,
              buildSystemFilter,
              createdAfterFilter,
              createdBeforeFilter
            )}
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

function emptyTaskListMessage(
  statusFilter: TaskStatusFilter,
  searchQuery: string,
  repositoryOwnerFilter: string,
  repositoryNameFilter: string,
  languageFilter: string,
  buildSystemFilter: string,
  createdAfterFilter: string,
  createdBeforeFilter: string
) {
  if (searchQuery.trim()) {
    return `No tasks match "${searchQuery.trim()}".`;
  }
  if (repositoryOwnerFilter.trim() || repositoryNameFilter.trim()) {
    return 'No tasks match selected repository filters.';
  }
  if (languageFilter.trim() || buildSystemFilter.trim()) {
    return 'No tasks match selected adapter filters.';
  }
  if (createdAfterFilter.trim() || createdBeforeFilter.trim()) {
    return 'No tasks match selected created time filters.';
  }
  return `No ${statusFilter} tasks found.`;
}

function statusCount(statusCounts: FixTaskStatusCounts | null, status: TaskStatusFilter) {
  if (!statusCounts) {
    return 0;
  }
  if (status === 'ALL') {
    return statusCounts.totalCount;
  }
  if (status === 'PENDING') {
    return statusCounts.pendingCount;
  }
  if (status === 'RUNNING') {
    return statusCounts.runningCount;
  }
  if (status === 'RUNNING_TESTS') {
    return statusCounts.runningTestsCount;
  }
  if (status === 'COMPLETED') {
    return statusCounts.completedCount;
  }
  if (status === 'FAILED') {
    return statusCounts.failedCount;
  }
  return statusCounts.cancelledCount;
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
