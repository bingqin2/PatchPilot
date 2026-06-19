package io.patchpilot.backend.github.client;

import io.patchpilot.backend.github.client.domain.CreateIssueCommentCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueCommentException;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
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

class GitHubIssueCommentClientTests {

    @Test
    void should_create_issue_comment_with_expected_request() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
                201,
                "{\"id\":123,\"html_url\":\"https://github.com/octocat/hello-world/issues/42#issuecomment-123\"}"
        );
        GitHubIssueCommentClient client = new GitHubIssueCommentClient(httpClient, properties("secret-token"));

        IssueCommentResult result = client.createIssueComment(command());

        assertThat(result.id()).isEqualTo(123);
        assertThat(result.url()).isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-123");
        assertThat(httpClient.request().method()).isEqualTo("POST");
        assertThat(httpClient.request().uri())
                .isEqualTo(URI.create("https://api.github.com/repos/octocat/hello-world/issues/42/comments"));
        assertThat(httpClient.request().headers().firstValue("Authorization")).contains("Bearer secret-token");
        assertThat(httpClient.request().headers().firstValue("Accept")).contains("application/vnd.github+json");
        assertThat(httpClient.request().headers().firstValue("X-GitHub-Api-Version")).contains("2022-11-28");
        assertThat(httpClient.request().headers().firstValue("Content-Type")).contains("application/json");
        assertThat(httpClient.body()).contains("\"body\":\"PatchPilot completed the task.\\n\\nPR: https://github.com/octocat/hello-world/pull/7\"");
    }

    @Test
    void should_fail_before_sending_request_when_token_is_missing() {
        RecordingHttpClient httpClient = new RecordingHttpClient(
                201,
                "{\"id\":123,\"html_url\":\"https://github.com/octocat/hello-world/issues/42#issuecomment-123\"}"
        );
        GitHubIssueCommentClient client = new GitHubIssueCommentClient(httpClient, properties(" "));

        assertThatThrownBy(() -> client.createIssueComment(command()))
                .isInstanceOf(GitHubIssueCommentException.class)
                .hasMessage("GitHub token is required to create Issue comments");
        assertThat(httpClient.request()).isNull();
    }

    @Test
    void should_fail_when_github_returns_non_created_status() {
        RecordingHttpClient httpClient = new RecordingHttpClient(403, "{\"message\":\"Resource not accessible\"}");
        GitHubIssueCommentClient client = new GitHubIssueCommentClient(httpClient, properties("secret-token"));

        assertThatThrownBy(() -> client.createIssueComment(command()))
                .isInstanceOf(GitHubIssueCommentException.class)
                .hasMessage("GitHub issue comment creation failed: HTTP 403");
    }

    private static GitHubProperties properties(String token) {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken(token);
        return properties;
    }

    private static CreateIssueCommentCommand command() {
        return new CreateIssueCommentCommand(
                "octocat",
                "hello-world",
                42,
                "PatchPilot completed the task.\n\nPR: https://github.com/octocat/hello-world/pull/7"
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
        public Version version() {
            return Version.HTTP_1_1;
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
        public Optional<javax.net.ssl.SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return URI.create("https://api.github.com");
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
