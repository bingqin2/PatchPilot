package io.patchpilot.backend.github.client;

import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextException;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubIssueContextClientTests {

    @Test
    void should_fetch_issue_and_recent_comments() {
        RecordingHttpClient httpClient = new RecordingHttpClient(List.of(
                response(200, """
                        {
                          "title": "Calculator add returns the wrong value",
                          "body": "The add method returns subtraction when both inputs are positive.",
                          "html_url": "https://github.com/octocat/hello-world/issues/42"
                        }
                        """),
                response(200, """
                        [
                          {
                            "id": 1001,
                            "body": "I reproduced this with CalculatorTest#addsNumbers.",
                            "user": { "login": "alice" },
                            "created_at": "2026-06-20T01:00:00Z",
                            "html_url": "https://github.com/octocat/hello-world/issues/42#issuecomment-1001"
                          },
                          {
                            "id": 1002,
                            "body": null,
                            "user": { "login": "bob" },
                            "created_at": "2026-06-20T01:05:00Z",
                            "html_url": "https://github.com/octocat/hello-world/issues/42#issuecomment-1002"
                          }
                        ]
                        """)
        ));
        GitHubIssueContextClient client = new GitHubIssueContextClient(httpClient, properties("secret-token"));

        GitHubIssueContext context = client.getIssueContext(new GetIssueContextCommand(
                "octocat",
                "hello-world",
                42,
                2
        ));

        assertThat(context.title()).isEqualTo("Calculator add returns the wrong value");
        assertThat(context.body()).isEqualTo("The add method returns subtraction when both inputs are positive.");
        assertThat(context.url()).isEqualTo("https://github.com/octocat/hello-world/issues/42");
        assertThat(context.comments()).hasSize(2);
        assertThat(context.comments().get(0).author()).isEqualTo("alice");
        assertThat(context.comments().get(0).body()).isEqualTo("I reproduced this with CalculatorTest#addsNumbers.");
        assertThat(context.comments().get(0).url())
                .isEqualTo("https://github.com/octocat/hello-world/issues/42#issuecomment-1001");
        assertThat(context.comments().get(1).body()).isEmpty();
        assertThat(httpClient.requests())
                .extracting(HttpRequest::uri)
                .containsExactly(
                        URI.create("https://api.github.com/repos/octocat/hello-world/issues/42"),
                        URI.create("https://api.github.com/repos/octocat/hello-world/issues/42/comments?per_page=2")
                );
        assertThat(httpClient.requests().get(0).headers().firstValue("Authorization")).contains("Bearer secret-token");
        assertThat(httpClient.requests().get(1).headers().firstValue("Accept")).contains("application/vnd.github+json");
    }

    @Test
    void should_fail_before_request_when_token_is_missing() {
        RecordingHttpClient httpClient = new RecordingHttpClient(List.of(response(200, "{}")));
        GitHubIssueContextClient client = new GitHubIssueContextClient(httpClient, properties(" "));

        assertThatThrownBy(() -> client.getIssueContext(new GetIssueContextCommand("octocat", "hello-world", 42, 3)))
                .isInstanceOf(GitHubIssueContextException.class)
                .hasMessage("GitHub token is required to read Issue context");
        assertThat(httpClient.requests()).isEmpty();
    }

    @Test
    void should_fail_when_issue_request_returns_non_ok_status() {
        RecordingHttpClient httpClient = new RecordingHttpClient(List.of(response(403, "{\"message\":\"Forbidden\"}")));
        GitHubIssueContextClient client = new GitHubIssueContextClient(httpClient, properties("secret-token"));

        assertThatThrownBy(() -> client.getIssueContext(new GetIssueContextCommand("octocat", "hello-world", 42, 3)))
                .isInstanceOf(GitHubIssueContextException.class)
                .hasMessage("GitHub issue context read failed: HTTP 403");
    }

    private static GitHubProperties properties(String token) {
        GitHubProperties properties = new GitHubProperties();
        properties.setToken(token);
        return properties;
    }

    private static RecordingHttpResponse<String> response(int statusCode, String body) {
        return new RecordingHttpResponse<>(statusCode, body);
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final List<RecordingHttpResponse<String>> responses;
        private final List<HttpRequest> requests = new ArrayList<>();
        private int index;

        private RecordingHttpClient(List<RecordingHttpResponse<String>> responses) {
            this.responses = responses;
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
            requests.add(request);
            @SuppressWarnings("unchecked")
            HttpResponse<T> response = (HttpResponse<T>) responses.get(index++);
            return response;
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

        private List<HttpRequest> requests() {
            return requests;
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
            return HttpHeaders.of(java.util.Map.of(), (left, right) -> true);
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return null;
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
