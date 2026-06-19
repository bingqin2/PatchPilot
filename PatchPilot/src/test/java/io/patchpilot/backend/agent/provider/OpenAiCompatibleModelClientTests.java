package io.patchpilot.backend.agent.provider;

import io.patchpilot.backend.agent.config.AgentProperties;
import io.patchpilot.backend.agent.provider.domain.ModelProviderRequest;
import io.patchpilot.backend.agent.provider.domain.ModelProviderResponse;
import io.patchpilot.backend.agent.provider.domain.ModelProviderException;
import io.patchpilot.backend.task.domain.vo.FixTaskModelCallVo;
import io.patchpilot.backend.task.service.FixTaskModelCallService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskModelCallService;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiCompatibleModelClientTests {

    @Test
    void should_send_chat_completion_request_and_record_success_audit() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
                200,
                """
                        {
                          "choices": [
                            {
                              "message": {
                                "role": "assistant",
                                "content": "Use FileWriteTool to update Calculator.java"
                              }
                            }
                          ],
                          "usage": {
                            "prompt_tokens": 42,
                            "completion_tokens": 18,
                            "total_tokens": 60
                          }
                        }
                        """
        );
        FixTaskModelCallService modelCallService = new InMemoryFixTaskModelCallService();
        OpenAiCompatibleModelClient client = new OpenAiCompatibleModelClient(
                httpClient,
                properties("secret-key"),
                modelCallService
        );

        ModelProviderResponse response = client.complete(request());

        assertThat(response.content()).isEqualTo("Use FileWriteTool to update Calculator.java");
        assertThat(response.promptTokens()).isEqualTo(42);
        assertThat(response.completionTokens()).isEqualTo(18);
        assertThat(response.totalTokens()).isEqualTo(60);
        assertThat(httpClient.request().method()).isEqualTo("POST");
        assertThat(httpClient.request().uri()).isEqualTo(URI.create("https://llm.example.test/v1/chat/completions"));
        assertThat(httpClient.request().headers().firstValue("Authorization")).contains("Bearer secret-key");
        assertThat(httpClient.request().headers().firstValue("Content-Type")).contains("application/json");
        assertThat(httpClient.body()).contains("\"model\":\"gpt-4.1-mini\"");
        assertThat(httpClient.body()).contains("\"role\":\"system\"");
        assertThat(httpClient.body()).contains("\"content\":\"You are PatchPilot.\"");
        assertThat(httpClient.body()).contains("\"role\":\"user\"");
        assertThat(httpClient.body()).contains("\"content\":\"Fix calculator bug\"");

        List<FixTaskModelCallVo> modelCalls = modelCallService.listModelCalls("task-123");
        assertThat(modelCalls).hasSize(1);
        assertThat(modelCalls.get(0).provider()).isEqualTo("openai-compatible");
        assertThat(modelCalls.get(0).model()).isEqualTo("gpt-4.1-mini");
        assertThat(modelCalls.get(0).promptSummary()).isEqualTo("Fix calculator bug");
        assertThat(modelCalls.get(0).responseSummary()).isEqualTo("Use FileWriteTool to update Calculator.java");
        assertThat(modelCalls.get(0).promptTokens()).isEqualTo(42);
        assertThat(modelCalls.get(0).completionTokens()).isEqualTo(18);
        assertThat(modelCalls.get(0).totalTokens()).isEqualTo(60);
        assertThat(modelCalls.get(0).success()).isTrue();
        assertThat(modelCalls.get(0).errorMessage()).isNull();
    }

    @Test
    void should_fail_before_sending_request_when_api_key_is_missing() {
        RecordingHttpClient httpClient = new RecordingHttpClient(200, "{}");
        OpenAiCompatibleModelClient client = new OpenAiCompatibleModelClient(
                httpClient,
                properties(" "),
                new InMemoryFixTaskModelCallService()
        );

        assertThatThrownBy(() -> client.complete(request()))
                .isInstanceOf(ModelProviderException.class)
                .hasMessage("Model provider API key is required");
        assertThat(httpClient.request()).isNull();
    }

    @Test
    void should_record_failed_audit_when_provider_returns_error() {
        RecordingHttpClient httpClient = new RecordingHttpClient(429, "{\"error\":{\"message\":\"rate limited\"}}");
        FixTaskModelCallService modelCallService = new InMemoryFixTaskModelCallService();
        OpenAiCompatibleModelClient client = new OpenAiCompatibleModelClient(
                httpClient,
                properties("secret-key"),
                modelCallService
        );

        assertThatThrownBy(() -> client.complete(request()))
                .isInstanceOf(ModelProviderException.class)
                .hasMessage("Model provider request failed: HTTP 429");

        List<FixTaskModelCallVo> modelCalls = modelCallService.listModelCalls("task-123");
        assertThat(modelCalls).hasSize(1);
        assertThat(modelCalls.get(0).success()).isFalse();
        assertThat(modelCalls.get(0).errorMessage()).isEqualTo("Model provider request failed: HTTP 429");
        assertThat(modelCalls.get(0).promptTokens()).isZero();
        assertThat(modelCalls.get(0).completionTokens()).isZero();
        assertThat(modelCalls.get(0).totalTokens()).isZero();
    }

    private static ModelProviderRequest request() {
        return new ModelProviderRequest(
                "task-123",
                "You are PatchPilot.",
                "Fix calculator bug"
        );
    }

    private static AgentProperties properties(String apiKey) {
        AgentProperties properties = new AgentProperties();
        properties.setProvider("openai-compatible");
        properties.setModel("gpt-4.1-mini");
        properties.setBaseUrl("https://llm.example.test/v1");
        properties.setApiKey(apiKey);
        return properties;
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final int statusCode;
        private final String responseBody;
        private HttpRequest request;
        private String body;

        private RecordingHttpClient(int statusCode, String responseBody) {
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        @Override
        public Optional<CookieHandler> cookieHandler() {
            return Optional.empty();
        }

        @Override
        public Optional<Duration> connectTimeout() {
            return Optional.empty();
        }

        @Override
        public Redirect followRedirects() {
            return Redirect.NEVER;
        }

        @Override
        public Optional<ProxySelector> proxy() {
            return Optional.empty();
        }

        @Override
        public SSLContext sslContext() {
            return null;
        }

        @Override
        public SSLParameters sslParameters() {
            return null;
        }

        @Override
        public Optional<Authenticator> authenticator() {
            return Optional.empty();
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }

        @Override
        public Optional<java.util.concurrent.Executor> executor() {
            return Optional.empty();
        }

        @Override
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException {
            this.request = request;
            this.body = requestBody(request);
            @SuppressWarnings("unchecked")
            T castBody = (T) responseBody;
            return new RecordingHttpResponse<>(statusCode, castBody);
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            throw new UnsupportedOperationException("sendAsync is not used");
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler
        ) {
            throw new UnsupportedOperationException("sendAsync is not used");
        }

        private HttpRequest request() {
            return request;
        }

        private String body() {
            return body;
        }
    }

    private static String requestBody(HttpRequest request) {
        RecordingSubscriber subscriber = new RecordingSubscriber();
        request.bodyPublisher()
                .orElseThrow(() -> new IllegalStateException("Expected request body"))
                .subscribe(subscriber);
        return subscriber.body();
    }

    private static final class RecordingSubscriber implements Flow.Subscriber<ByteBuffer> {

        private final StringBuilder body = new StringBuilder();

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(ByteBuffer item) {
            byte[] bytes = new byte[item.remaining()];
            item.get(bytes);
            body.append(new String(bytes));
        }

        @Override
        public void onError(Throwable throwable) {
            throw new IllegalStateException(throwable);
        }

        @Override
        public void onComplete() {
        }

        private String body() {
            return body.toString();
        }
    }

    private record RecordingHttpResponse<T>(int statusCode, T body) implements HttpResponse<T> {

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public Optional<HttpResponse<T>> previousResponse() {
            return Optional.empty();
        }

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(java.util.Map.of(), (name, value) -> true);
        }

        @Override
        public URI uri() {
            return URI.create("https://llm.example.test");
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }
    }
}
