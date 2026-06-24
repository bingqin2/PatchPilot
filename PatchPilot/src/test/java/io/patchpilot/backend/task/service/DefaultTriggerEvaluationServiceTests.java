package io.patchpilot.backend.task.service;

import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GetIssueContextCommand;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.GitHubIssueContextComment;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.NoOpTriggerQuarantineService;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.EvaluateTriggerCommand;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationResultVo;
import io.patchpilot.backend.task.service.impl.DefaultTriggerEvaluationService;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTriggerEvaluationServiceTests {

    private final InMemoryFixTaskService fixTaskService = new InMemoryFixTaskService();

    @Test
    void should_block_when_active_task_exists_without_recording_rate_limit_or_model_call() {
        fixTaskService.createFixTask(new CreateFixTaskCommand(
                "bingqin2",
                "PatchPilot",
                7,
                0,
                "local-operator",
                "/agent fix touch docs/existing.md",
                "delivery-active",
                0
        ));
        RecordingTriggerRateLimitService rateLimitService = new RecordingTriggerRateLimitService(
                TriggerRateLimitDecision.accepted()
        );
        RecordingTriggerIntentClassifier triggerIntentClassifier = new RecordingTriggerIntentClassifier(
                TriggerIntentDecision.shouldExecute("should not be called")
        );
        TriggerEvaluationService service = new DefaultTriggerEvaluationService(
                fixTaskService,
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                rateLimitService,
                triggerIntentClassifier,
                new RecordingIssueContextService(issueContext())
        );

        TriggerEvaluationResultVo result = service.evaluate(command("/agent fix touch docs/manual-task.md"));

        assertThat(result.status()).isEqualTo("BLOCKED");
        assertThat(result.wouldCreateTask()).isFalse();
        assertThat(result.blockedReason()).isEqualTo("An active task already exists for this issue");
        assertThat(result.activeTaskDecision().allowed()).isFalse();
        assertThat(result.rateLimitDecision()).isNull();
        assertThat(result.triggerIntentDecision()).isNull();
        assertThat(rateLimitService.checkRequest()).isNull();
        assertThat(rateLimitService.recordRequest()).isNull();
        assertThat(triggerIntentClassifier.request()).isNull();
        assertThat(fixTaskService.listTasks()).hasSize(1);
    }

    @Test
    void should_use_issue_context_model_classification_for_short_command_without_creating_task() {
        RecordingTriggerRateLimitService rateLimitService = new RecordingTriggerRateLimitService(
                TriggerRateLimitDecision.accepted()
        );
        RecordingTriggerIntentClassifier triggerIntentClassifier = new RecordingTriggerIntentClassifier(
                TriggerIntentDecision.shouldExecute("Issue context describes a concrete failing test."),
                true
        );
        RecordingIssueContextService issueContextService = new RecordingIssueContextService(issueContext());
        TriggerEvaluationService service = new DefaultTriggerEvaluationService(
                fixTaskService,
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                rateLimitService,
                triggerIntentClassifier,
                issueContextService
        );

        TriggerEvaluationResultVo result = service.evaluate(command("/agent fix"));

        assertThat(result.status()).isEqualTo("WOULD_CREATE_TASK");
        assertThat(result.wouldCreateTask()).isTrue();
        assertThat(result.safetyDecision().allowed()).isFalse();
        assertThat(result.safetyDecision().category()).isEqualTo("NOT_ACTIONABLE");
        assertThat(result.issueContextLoaded()).isTrue();
        assertThat(result.triggerIntentDecision().allowed()).isTrue();
        assertThat(result.triggerIntentDecision().reason()).isEqualTo("Issue context describes a concrete failing test.");
        assertThat(issueContextService.command().owner()).isEqualTo("bingqin2");
        assertThat(triggerIntentClassifier.request().issueTitle()).isEqualTo("Dashboard filter returns empty results");
        assertThat(triggerIntentClassifier.request().recentIssueComments())
                .extracting(comment -> comment.author() + ": " + comment.body())
                .containsExactly("alice: Reproduce with the failed status filter and task #123.");
        assertThat(rateLimitService.checkRequest()).isNotNull();
        assertThat(rateLimitService.recordRequest()).isNull();
        assertThat(fixTaskService.listTasks()).isEmpty();
    }

    private static EvaluateTriggerCommand command(String triggerComment) {
        return new EvaluateTriggerCommand(
                "bingqin2",
                "PatchPilot",
                7,
                "local-operator",
                triggerComment
        );
    }

    private static GitHubIssueContext issueContext() {
        return new GitHubIssueContext(
                "Dashboard filter returns empty results",
                "The dashboard calls /api/tasks?status=FAILED and renders no rows even though failed tasks exist.",
                "https://github.com/bingqin2/PatchPilot/issues/7",
                List.of(new GitHubIssueContextComment(
                        123,
                        "alice",
                        "Reproduce with the failed status filter and task #123.",
                        "2026-06-24T00:00:00Z",
                        "https://github.com/bingqin2/PatchPilot/issues/7#issuecomment-123"
                ))
        );
    }

    private static final class RecordingTriggerIntentClassifier implements TriggerIntentClassifier {

        private final TriggerIntentDecision decision;
        private final boolean supportsIssueContextClassification;
        private TriggerIntentClassificationRequest request;

        private RecordingTriggerIntentClassifier(TriggerIntentDecision decision) {
            this(decision, false);
        }

        private RecordingTriggerIntentClassifier(
                TriggerIntentDecision decision,
                boolean supportsIssueContextClassification
        ) {
            this.decision = decision;
            this.supportsIssueContextClassification = supportsIssueContextClassification;
        }

        @Override
        public TriggerIntentDecision classify(TriggerIntentClassificationRequest request) {
            this.request = request;
            return decision;
        }

        @Override
        public boolean supportsIssueContextClassification() {
            return supportsIssueContextClassification;
        }

        private TriggerIntentClassificationRequest request() {
            return request;
        }
    }

    private static final class RecordingIssueContextService extends IssueContextService {

        private final GitHubIssueContext context;
        private GetIssueContextCommand command;

        private RecordingIssueContextService(GitHubIssueContext context) {
            super(new GitHubIssueContextClient(new GitHubProperties()));
            this.context = context;
        }

        @Override
        public GitHubIssueContext loadIssueContext(String repositoryOwner, String repositoryName, long issueNumber) {
            command = new GetIssueContextCommand(repositoryOwner, repositoryName, issueNumber, 5);
            return context;
        }

        private GetIssueContextCommand command() {
            return command;
        }
    }

    private static final class RecordingTriggerRateLimitService implements TriggerRateLimitService {

        private final TriggerRateLimitDecision decision;
        private TriggerRateLimitRequest checkRequest;
        private TriggerRateLimitRequest recordRequest;

        private RecordingTriggerRateLimitService(TriggerRateLimitDecision decision) {
            this.decision = decision;
        }

        @Override
        public TriggerRateLimitDecision check(TriggerRateLimitRequest request) {
            checkRequest = request;
            return decision;
        }

        @Override
        public TriggerRateLimitDecision checkAndRecord(TriggerRateLimitRequest request) {
            recordRequest = request;
            return decision;
        }

        private TriggerRateLimitRequest checkRequest() {
            return checkRequest;
        }

        private TriggerRateLimitRequest recordRequest() {
            return recordRequest;
        }
    }
}
