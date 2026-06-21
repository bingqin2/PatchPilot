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

export function number(value?: number | null) {
  return new Intl.NumberFormat(undefined).format(value ?? 0);
}

export function usd(value?: number | null) {
  const amount = value ?? 0;
  if (amount > 0 && amount < 0.01) {
    return `$${amount.toFixed(4)}`;
  }
  return new Intl.NumberFormat(undefined, {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount);
}

export function compactTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(new Date(value));
}

export function compactDateTime(value: string) {
  return new Intl.DateTimeFormat(undefined, {
    month: 'short',
    day: '2-digit',
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
