import type { BackendHealth, ConfigurationSummary } from '../../types';

interface ConnectivityPanelProps {
  backendHealth: BackendHealth | null;
  configuration: ConfigurationSummary | null;
  hasStoredAdminToken: boolean;
  error: string | null;
}

export function ConnectivityPanel({
  backendHealth,
  configuration,
  hasStoredAdminToken,
  error
}: ConnectivityPanelProps) {
  const protectedApiBlocked = error === 'Admin token is required';
  const protectedApiReady = Boolean(configuration) && !protectedApiBlocked;
  const statusReady = Boolean(backendHealth) && protectedApiReady && (hasStoredAdminToken || !configuration?.adminTokenConfigured);
  const nextAction = connectivityAction(backendHealth, protectedApiBlocked, hasStoredAdminToken);

  return (
    <section className={`panel connectivity-panel connectivity-panel-${statusReady ? 'ready' : 'attention'}`} aria-label="Connectivity">
      <div className="panel-header">
        <div>
          <h2>Connectivity</h2>
          <p>{statusReady ? 'API connectivity ready' : 'API connectivity needs attention'}</p>
        </div>
      </div>
      <div className="connectivity-grid">
        <div>
          <span>Backend</span>
          <strong>{backendHealth ? `Backend ${backendHealth.status}` : 'Backend unavailable'}</strong>
          <p>{backendHealth?.service ?? 'Check backend process and Vite proxy target.'}</p>
        </div>
        <div>
          <span>Browser token</span>
          <strong>{hasStoredAdminToken ? 'Browser token saved' : 'Browser token missing'}</strong>
          <p>{hasStoredAdminToken ? 'Requests can attach the admin header.' : 'Save a dashboard admin token if APIs are protected.'}</p>
        </div>
        <div>
          <span>Protected APIs</span>
          <strong>{protectedApiReady ? 'Protected APIs reachable' : protectedApiBlocked ? 'Protected APIs blocked' : 'Protected APIs loading'}</strong>
          <p>{protectedApiBlocked ? 'Admin token is required.' : configuration ? 'Configuration summary loaded.' : 'Waiting for configuration summary.'}</p>
        </div>
      </div>
      {nextAction ? <p className="connectivity-action">{nextAction}</p> : null}
    </section>
  );
}

function connectivityAction(backendHealth: BackendHealth | null, protectedApiBlocked: boolean, hasStoredAdminToken: boolean) {
  if (!backendHealth) {
    return 'Start the backend or check PATCHPILOT_FRONTEND_BACKEND_URL.';
  }
  if (protectedApiBlocked && !hasStoredAdminToken) {
    return 'Save the dashboard admin token to retry protected API calls.';
  }
  if (protectedApiBlocked) {
    return 'Update the saved dashboard admin token and retry protected API calls.';
  }
  return '';
}
