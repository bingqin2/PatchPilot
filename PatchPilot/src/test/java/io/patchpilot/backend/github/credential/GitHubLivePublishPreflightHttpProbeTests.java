package io.patchpilot.backend.github.credential;

import io.patchpilot.backend.github.credential.domain.GitHubCredentialReadinessException;
import io.patchpilot.backend.github.credential.domain.GitHubLivePublishPreflightProbeResult;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSession;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GitHubLivePublishPreflightHttpProbeTests {

    @Test
    void should_read_branch_and_open_pull_request_metadata_without_mutating_github() {
        RecordingHttpClient httpClient = new RecordingHttpClient(List.of(
                response(200, """
                        [
                          {"name": "main"},
                          {"name": "patchpilot/task-1"},
                          {"name": "feature/manual"}
                        ]
                        """),
                response(200, """
                        [
                          {"html_url": "https://github.com/bingqin2/PatchPilot/pull/4", "head": {"ref": "patchpilot/task-1"}},
                          {"html_url": "https://github.com/bingqin2/PatchPilot/pull/5", "head": {"ref": "feature/manual"}}
                        ]
                        """)
        ));
        GitHubLivePublishPreflightHttpProbe probe = new GitHubLivePublishPreflightHttpProbe(httpClient);

        GitHubLivePublishPreflightProbeResult result = probe.check("github-token", "bingqin2", "PatchPilot");

        assertThat(result.defaultBranch()).isEqualTo("main");
        assertThat(result.branchNames()).containsExactly("main", "patchpilot/task-1", "feature/manual");
        assertThat(result.openPatchpilotPullRequestUrls()).containsExactly("https://github.com/bingqin2/PatchPilot/pull/4");
        assertThat(httpClient.requests()).hasSize(2);
        assertThat(httpClient.requests().get(0).method()).isEqualTo("GET");
        assertThat(httpClient.requests().get(0).uri().toString())
                .isEqualTo("https://api.github.com/repos/bingqin2/PatchPilot/branches?per_page=100");
        assertThat(httpClient.requests().get(1).method()).isEqualTo("GET");
        assertThat(httpClient.requests().get(1).uri().toString())
                .isEqualTo("https://api.github.com/repos/bingqin2/PatchPilot/pulls?state=open&per_page=100");
        assertThat(httpClient.requests().get(0).headers().firstValue("Authorization")).contains("Bearer github-token");
    }

    @Test
    void should_url_encode_repository_path_segments() {
        RecordingHttpClient httpClient = new RecordingHttpClient(List.of(
                response(200, "[{\"name\":\"main\"}]"),
                response(200, "[]")
        ));
        GitHubLivePublishPreflightHttpProbe probe = new GitHubLivePublishPreflightHttpProbe(httpClient);

        probe.check("github-token", "owner with space", "repo/name");

        assertThat(httpClient.requests().get(0).uri().toString())
                .isEqualTo("https://api.github.com/repos/owner%20with%20space/repo%2Fname/branches?per_page=100");
    }

    @Test
    void should_fail_when_branch_request_is_not_successful() {
        GitHubLivePublishPreflightHttpProbe probe = new GitHubLivePublishPreflightHttpProbe(
                new RecordingHttpClient(List.of(response(403, "{\"message\":\"Forbidden\"}")))
        );

        assertThatThrownBy(() -> probe.check("github-token", "bingqin2", "PatchPilot"))
                .isInstanceOf(GitHubCredentialReadinessException.class)
                .hasMessageContaining("GitHub live publish preflight failed: branches HTTP 403");
    }

    @Test
    void should_fail_when_pull_request_request_is_not_successful() {
        GitHubLivePublishPreflightHttpProbe probe = new GitHubLivePublishPreflightHttpProbe(
                new RecordingHttpClient(List.of(
                        response(200, "[{\"name\":\"main\"}]"),
                        response(500, "{\"message\":\"Server Error\"}")
                ))
        );

        assertThatThrownBy(() -> probe.check("github-token", "bingqin2", "PatchPilot"))
                .isInstanceOf(GitHubCredentialReadinessException.class)
                .hasMessageContaining("GitHub live publish preflight failed: pulls HTTP 500");
    }

    @Test
    void should_fail_when_branch_response_is_invalid() {
        GitHubLivePublishPreflightHttpProbe probe = new GitHubLivePublishPreflightHttpProbe(
                new RecordingHttpClient(List.of(
                        response(200, "{\"name\":\"main\"}"),
                        response(200, "[]")
                ))
        );

        assertThatThrownBy(() -> probe.check("github-token", "bingqin2", "PatchPilot"))
                .isInstanceOf(GitHubCredentialReadinessException.class)
                .hasMessageContaining("invalid branches response");
    }

    private static Response response(int statusCode, String body) {
        return new Response(statusCode, body);
    }

    private record Response(int statusCode, String body) {
    }

    private static final class RecordingHttpClient extends HttpClient {

        private final List<Response> responses;
        private final List<HttpRequest> requests = new ArrayList<>();
        private int index;

        private RecordingHttpClient(List<Response> responses) {
            this.responses = responses;
        }

        private List<HttpRequest> requests() {
            return requests;
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
        public Optional<Executor> executor() {
            return Optional.empty();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
                throws IOException, InterruptedException {
            requests.add(request);
            Response response = responses.get(Math.min(index++, responses.size() - 1));
            HttpResponse.BodySubscriber<T> subscriber = responseBodyHandler.apply(new ResponseInfo(response.statusCode()));
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override
                public void request(long n) {
                }

                @Override
                public void cancel() {
                }
            });
            subscriber.onNext(List.of(ByteBuffer.wrap(response.body().getBytes(java.nio.charset.StandardCharsets.UTF_8))));
            subscriber.onComplete();
            T body = subscriber.getBody().toCompletableFuture().join();
            return new RecordingHttpResponse<>(response.statusCode(), body, request.uri());
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
            throw new UnsupportedOperationException("sendAsync is not used by this test");
        }

        @Override
        public <T> CompletableFuture<HttpResponse<T>> sendAsync(
                HttpRequest request,
                HttpResponse.BodyHandler<T> responseBodyHandler,
                HttpResponse.PushPromiseHandler<T> pushPromiseHandler
        ) {
            throw new UnsupportedOperationException("sendAsync is not used by this test");
        }
    }

    private record RecordingHttpResponse<T>(int statusCode, T body, URI uri) implements HttpResponse<T> {

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
            return HttpHeaders.of(java.util.Map.of(), (key, value) -> true);
        }

        @Override
        public Optional<SSLSession> sslSession() {
            return Optional.empty();
        }

        @Override
        public URI uri() {
            return uri;
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }

    private record ResponseInfo(int statusCode) implements HttpResponse.ResponseInfo {

        @Override
        public HttpHeaders headers() {
            return HttpHeaders.of(java.util.Map.of(), (key, value) -> true);
        }

        @Override
        public HttpClient.Version version() {
            return HttpClient.Version.HTTP_1_1;
        }
    }
}
