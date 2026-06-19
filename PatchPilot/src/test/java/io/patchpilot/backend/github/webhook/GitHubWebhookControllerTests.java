package io.patchpilot.backend.github.webhook;

import io.patchpilot.backend.agent.tool.CommitTool;
import io.patchpilot.backend.agent.tool.DiffTool;
import io.patchpilot.backend.agent.tool.PullRequestTool;
import io.patchpilot.backend.agent.tool.PushTool;
import io.patchpilot.backend.agent.workflow.PatchWorkflow;
import io.patchpilot.backend.agent.workflow.domain.PatchWorkflowResult;
import io.patchpilot.backend.github.client.GitHubPullRequestClient;
import io.patchpilot.backend.github.client.domain.CreatePullRequestCommand;
import io.patchpilot.backend.github.client.domain.PullRequestResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import io.patchpilot.backend.runner.domain.vo.TestRunResult;
import io.patchpilot.backend.runner.service.MavenTestRunner;
import io.patchpilot.backend.workspace.domain.bo.CloneWorkspaceCommand;
import io.patchpilot.backend.workspace.domain.vo.PreparedWorkspaceResult;
import io.patchpilot.backend.workspace.domain.vo.WorkspaceCloneResult;
import io.patchpilot.backend.workspace.runner.GitCommandRunner;
import io.patchpilot.backend.workspace.service.WorkspaceService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "patchpilot.github.webhook-secret=test-secret")
class GitHubWebhookControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void should_reject_invalid_signature() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-invalid")
                        .header("X-Hub-Signature-256", "sha256=invalid")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void should_ignore_non_triggering_issue_comment() throws Exception {
        String payload = issueCommentPayload("created", "please help", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-ignore")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IGNORED"))
                .andExpect(jsonPath("$.data.taskId").value(nullValue()));
    }

    @Test
    void should_ignore_unsupported_event() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issues")
                        .header("X-GitHub-Delivery", "delivery-unsupported")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("IGNORED"))
                .andExpect(jsonPath("$.data.taskId").value(nullValue()));
    }

    @Test
    void should_create_task_for_agent_fix_issue_comment() throws Exception {
        String payload = issueCommentPayload("created", "  /agent fix  ", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-create")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data.taskId").value(not(nullValue())));
    }

    @Test
    void should_create_task_for_agent_fix_issue_comment_with_patch_instruction() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix touch docs/demo.md", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-create-with-patch-instruction")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data.taskId").value(not(nullValue())));
    }

    @Test
    void should_dispatch_created_task_to_completion() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix", "octocat", "hello-world");

        String taskId = mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-dispatch")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TASK_CREATED"))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"taskId\":\"([^\"]+)\".*", "$1");

        awaitTaskStatus(taskId, "COMPLETED");
    }

    @Test
    void should_create_task_when_repository_webhook_has_no_installation() throws Exception {
        String payload = issueCommentPayloadWithoutInstallation("created", "/agent fix", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-no-installation")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data.taskId").value(not(nullValue())));
    }

    @Test
    void should_not_create_second_task_for_duplicate_delivery() throws Exception {
        String payload = issueCommentPayload("created", "/agent fix", "octocat", "hello-world");

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-duplicate")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TASK_CREATED"))
                .andExpect(jsonPath("$.data.taskId").value(not(nullValue())));

        mockMvc.perform(post("/api/github/webhook")
                        .header("X-GitHub-Event", "issue_comment")
                        .header("X-GitHub-Delivery", "delivery-duplicate")
                        .header("X-Hub-Signature-256", signature(payload))
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DUPLICATE_DELIVERY"))
                .andExpect(jsonPath("$.data.taskId").value(not(nullValue())));
    }

    private static String issueCommentPayload(String action, String commentBody, String owner, String repositoryName) {
        return """
                {
                  "action": "%s",
                  "installation": {
                    "id": 12345
                  },
                  "repository": {
                    "name": "%s",
                    "owner": {
                      "login": "%s"
                    }
                  },
                  "issue": {
                    "number": 42
                  },
                  "comment": {
                    "id": 98765,
                    "body": "%s",
                    "user": {
                      "login": "alice"
                    }
                  }
                }
                """.formatted(action, repositoryName, owner, commentBody);
    }

    private static String issueCommentPayloadWithoutInstallation(String action, String commentBody, String owner, String repositoryName) {
        return """
                {
                  "action": "%s",
                  "repository": {
                    "name": "%s",
                    "owner": {
                      "login": "%s"
                    }
                  },
                  "issue": {
                    "number": 42
                  },
                  "comment": {
                    "id": 98765,
                    "body": "%s",
                    "user": {
                      "login": "alice"
                    }
                  }
                }
                """.formatted(action, repositoryName, owner, commentBody);
    }

    private static String signature(String payload) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec("test-secret".getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder(digest.length * 2);
        for (byte value : digest) {
            hex.append(String.format("%02x", value));
        }
        return "sha256=" + hex;
    }

    private void awaitTaskStatus(String taskId, String expectedStatus) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
        AssertionError lastError = null;
        while (System.nanoTime() < deadline) {
            try {
                mockMvc.perform(get("/api/tasks/{id}", taskId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.status").value(expectedStatus));
                return;
            } catch (AssertionError error) {
                lastError = error;
                Thread.sleep(25);
            }
        }
        if (lastError != null) {
            throw lastError;
        }
    }

    @TestConfiguration
    static class WebhookTestConfiguration {

        @Bean
        @Primary
        WorkspaceService workspaceService() {
            return new WorkspaceService() {
                @Override
                public WorkspaceCloneResult cloneRepository(CloneWorkspaceCommand command) {
                    return cloneResult(command);
                }

                @Override
                public PreparedWorkspaceResult prepareRepository(CloneWorkspaceCommand command) {
                    WorkspaceCloneResult result = cloneResult(command);
                    return new PreparedWorkspaceResult(
                            result.taskId(),
                            result.workspaceDir(),
                            result.repositoryDir(),
                            "patchpilot/" + command.taskId()
                    );
                }

                private WorkspaceCloneResult cloneResult(CloneWorkspaceCommand command) {
                    return new WorkspaceCloneResult(
                            command.taskId(),
                            Path.of("/tmp/patchpilot-test", command.taskId()),
                            Path.of("/tmp/patchpilot-test", command.taskId(), "repo")
                    );
                }
            };
        }

        @Bean
        @Primary
        MavenTestRunner mavenTestRunner() {
            return new MavenTestRunner() {
                @Override
                public TestRunResult runTests(Path repositoryDir) {
                    return new TestRunResult("./mvnw test", 0, "tests passed");
                }
            };
        }

        @Bean
        @Primary
        PatchWorkflow patchWorkflow() {
            return new PatchWorkflow() {
                @Override
                public PatchWorkflowResult apply(FixTaskVo task, Path repositoryDir) {
                    return new PatchWorkflowResult(false, "test patch skipped");
                }
            };
        }

        @Bean
        @Primary
        DiffTool diffTool() {
            return new DiffTool(new GitCommandRunner(new GitHubProperties())) {
                @Override
                public String diff(Path repositoryDir) {
                    return "test diff";
                }
            };
        }

        @Bean
        @Primary
        CommitTool commitTool() {
            return new CommitTool(new GitCommandRunner(new GitHubProperties())) {
                @Override
                public String commitAll(Path repositoryDir, String message) {
                    return "test commit";
                }
            };
        }

        @Bean
        @Primary
        PushTool pushTool() {
            return new PushTool(new GitCommandRunner(new GitHubProperties())) {
                @Override
                public String pushBranch(Path repositoryDir, String branchName) {
                    return "test push";
                }
            };
        }

        @Bean
        @Primary
        PullRequestTool pullRequestTool() {
            return new PullRequestTool(new GitHubPullRequestClient(new GitHubProperties()) {
                @Override
                public PullRequestResult createPullRequest(CreatePullRequestCommand command) {
                    return new PullRequestResult("https://github.com/octocat/hello-world/pull/7");
                }
            });
        }
    }
}
