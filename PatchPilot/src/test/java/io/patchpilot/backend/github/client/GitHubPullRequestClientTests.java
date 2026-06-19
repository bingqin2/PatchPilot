package io.patchpilot.backend.github.client;

import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.GitHubPullRequestException;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubPullRequestClientTests {

    @Test
    void should_create_pull_request_with_expected_request() {
        RecordingHttpClient httpClient = new RecordingHttpClient(201, "{\"html_url\":\"https://github.com/octocat/hello-world/pull/7\"}");
        GitHubPullRequestClient client = new GitHubPullRequestClient(httpClient, properties("secret-token"));

        PullRequestResult result = client.createPullRequest(command());

        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/pull/7");
        assertThat(httpClient.request().method()).isEqualTo("POST");
        assertThat(httpClient.request().uri()).isEqualTo(URI.create("https://api.github.com/repos/octocat/hello-world/pulls"));
        assertThat(httpClient.request().headers().firstValue("Authorization")).contains("Bearer secret-token");
        assertThat(httpClient.request().headers().firstValue("Accept")).contains("application/vnd.github+json");
        assertThat(httpClient.request().headers().firstValue("X-GitHub-Api-Version")).contains("2022-11-28");
        assertThat(httpClient.body()).contains("\"title\":\"PatchPilot fix for #42\"");
        assertThat(httpClient.body()).contains("\"head\":\"octocat:patchpilot/task-123\"");
        assertThat(httpClient.body()).contains("\"base\":\"main\"");
        assertThat(httpClient.body()).contains("\"body\":\"Fixes #42");
    }

    @Test
    void should_fail_before_sending_request_when_token_is_missing() {
        RecordingHttpClient httpClient = new RecordingHttpClient(201, "{\"html_url\":\"https://github.com/octocat/hello-world/pull/7\"}");
        GitHubPullRequestClient client = new GitHubPullRequestClient(httpClient, properties(" "));

        assertThatThrownBy(() -> client.createPullRequest(command()))
                .isInstanceOf(GitHubPullRequestException.class)
                .hasMessage("GitHub token is required to create Pull Requests");
        assertThat(httpClient.request()).isNull();
    }

    @Test
    void should_fail_when_github_returns_non_created_status() {
        RecordingHttpClient httpClient = new RecordingHttpClient(422, "{\"message\":\"Validation Failed\"}");
        GitHubPullRequestClient client = new GitHubPullRequestClient(httpClient, properties("secret-token"));

        assertThatThrownBy(() -> client.createPullRequest(command()))
                .isInstanceOf(GitHubPullRequestException.class)
                .hasMessage("GitHub pull request creation failed: HTTP 422");
    }

    private static GitHubProperties properties(String token) {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken(token);
        return properties;
    }

    private static CreatePullRequestCommand command() {
        return new CreatePullRequestCommand(
                "octocat",
                "hello-world",
                "octocat:patchpilot/task-123",
                "main",
                "PatchPilot fix for #42",
                "Fixes #42\n\nTriggered by: alice\nBranch: patchpilot/task-123"
        );
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
            return URI.create("https://api.github.com");
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
