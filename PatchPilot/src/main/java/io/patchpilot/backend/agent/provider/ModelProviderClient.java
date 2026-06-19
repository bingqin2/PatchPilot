package io.patchpilot.backend.agent.provider;

import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;

public interface ModelProviderClient {

    ModelProviderResponse complete(ModelProviderRequest request);
}
