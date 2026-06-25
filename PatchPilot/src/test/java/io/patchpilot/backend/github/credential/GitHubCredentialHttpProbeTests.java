package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubCredentialHttpProbeTests {

    @Test
    void should_send_read_only_authenticated_user_probe() {
        RecordingHttpClient httpClient = new RecordingHttpClient(200, "{\"login\":\"octocat\"}");
        GitHubCredentialHttpProbe probe = new GitHubCredentialHttpProbe(httpClient);

        probe.check("github-token");

        assertThat(httpClient.request().method()).isEqualTo("GET");
        assertThat(httpClient.request().uri()).isEqualTo(URI.create("https://api.github.com/user"));
        assertThat(httpClient.request().headers().firstValue("Authorization")).contains("Bearer github-token");
        assertThat(httpClient.request().headers().firstValue("Accept")).contains("application/vnd.github+json");
        assertThat(httpClient.request().headers().firstValue("X-GitHub-Api-Version")).contains("2022-11-28");
    }

    @Test
    void should_fail_when_github_returns_non_success_status() {
        GitHubCredentialHttpProbe probe = new GitHubCredentialHttpProbe(new RecordingHttpClient(403, "{\"message\":\"Forbidden\"}"));

        assertThatThrownBy(() -> probe.check("github-token"))
                .isInstanceOf(GitHubCredentialReadinessException.class)
                .hasMessage("GitHub credential probe failed: HTTP 403");
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final int statusCode;
        private final String responseBody;
        private HttpRequest request;

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
            return URI.create("https://api.github.com/user");
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
