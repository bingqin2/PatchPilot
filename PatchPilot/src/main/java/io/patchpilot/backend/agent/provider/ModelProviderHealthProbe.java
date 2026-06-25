package io.patchpilot.backend.agent.provider;

@FunctionalInterface
public interface ModelProviderHealthProbe {

    String check(String baseUrl, String apiKey, String model);
}
