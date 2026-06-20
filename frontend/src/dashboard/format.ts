import type { FixTask } from '../types';

export function percent(value?: number) {
  return `${Math.round((value ?? 0) * 100)}%`;
}

export function duration(value?: number | null) {
  if (!value) {
    return '0 ms';
  }
  if (value < 1000) {
    return `${value} ms`;
  }
  return `${(value / 1000).toFixed(1)}s`;
}

export function compactTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(new Date(value));
}

export function pullRequestNumber(url: string) {
  return url.split('/').filter(Boolean).at(-1) ?? 'link';
}

export function issueUrl(task: FixTask) {
  return `https://github.com/${task.repositoryOwner}/${task.repositoryName}/issues/${task.issueNumber}`;
}
