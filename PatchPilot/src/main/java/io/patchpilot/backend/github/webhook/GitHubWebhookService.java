package io.patchpilot.backend.github.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.patchpilot.backend.agent.tool.IssueCommentTool;
import io.patchpilot.backend.github.IssueContextService;
import io.patchpilot.backend.github.client.GitHubIssueContextClient;
import io.patchpilot.backend.github.client.domain.GitHubIssueContext;
import io.patchpilot.backend.github.client.domain.IssueCommentResult;
import io.patchpilot.backend.github.config.GitHubProperties;
import io.patchpilot.backend.github.webhook.domain.RecordWebhookDeliveryDiagnosticCommand;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryDiagnosticStatus;
import io.patchpilot.backend.github.webhook.domain.WebhookDeliveryOutcomeType;
import io.patchpilot.backend.github.webhook.service.WebhookDeliveryDiagnosticService;
import io.patchpilot.backend.github.webhook.service.impl.InMemoryWebhookDeliveryDiagnosticService;
import io.patchpilot.backend.safety.CommandSafetyGate;
import io.patchpilot.backend.safety.NoOpTriggerIntentClassifier;
import io.patchpilot.backend.safety.NoOpTriggerQuarantineService;
import io.patchpilot.backend.safety.NoOpTriggerRateLimitService;
import io.patchpilot.backend.safety.TriggerDecisionEvidenceFormatter;
import io.patchpilot.backend.safety.domain.RejectedTriggerCategory;
import io.patchpilot.backend.safety.domain.RecordRejectedTriggerCommand;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentClassificationRequest;
import io.patchpilot.backend.safety.domain.TriggerIntentDecision;
import io.patchpilot.backend.safety.domain.TriggerIntentIssueComment;
import io.patchpilot.backend.safety.domain.TriggerQuarantineDecision;
import io.patchpilot.backend.safety.domain.TriggerQuarantineRequest;
import io.patchpilot.backend.safety.domain.TriggerRateLimitDecision;
import io.patchpilot.backend.safety.domain.TriggerRateLimitRequest;
import io.patchpilot.backend.safety.domain.RejectedTriggerAuditVo;
import io.patchpilot.backend.safety.service.RejectedTriggerAuditService;
import io.patchpilot.backend.safety.service.TriggerIntentClassifier;
import io.patchpilot.backend.safety.service.TriggerQuarantineService;
import io.patchpilot.backend.safety.service.TriggerRateLimitService;
import io.patchpilot.backend.safety.service.impl.InMemoryRejectedTriggerAuditService;
import io.patchpilot.backend.task.domain.bo.CreateFixTaskCommand;
import io.patchpilot.backend.task.domain.bo.FixTaskCreationResult;
import io.patchpilot.backend.task.domain.bo.RecordFixTaskPreExecutionDecisionCommand;
import io.patchpilot.backend.task.domain.enums.FixTaskTimelineEventType;
import io.patchpilot.backend.task.domain.vo.FixTaskVo;
import io.patchpilot.backend.task.domain.vo.TriggerEvaluationDecisionVo;
import io.patchpilot.backend.task.service.FixTaskDispatcher;
import io.patchpilot.backend.task.service.FixTaskPreExecutionDecisionService;
import io.patchpilot.backend.task.service.FixTaskService;
import io.patchpilot.backend.task.service.FixTaskTimelineService;
import io.patchpilot.backend.task.service.LogSummary;
import io.patchpilot.backend.task.service.impl.InMemoryFixTaskPreExecutionDecisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class GitHubWebhookService {

    private static final String ISSUE_COMMENT_EVENT = "issue_comment";
    private static final String CREATED_ACTION = "created";

    private final ObjectMapper objectMapper;
    private final FixTaskService fixTaskService;
    private final FixTaskDispatcher fixTaskDispatcher;
    private final IssueCommentTool issueCommentTool;
    private final FixTaskTimelineService fixTaskTimelineService;
    private final CommandSafetyGate commandSafetyGate;
    private final RejectedTriggerAuditService rejectedTriggerAuditService;
    private final TriggerRateLimitService triggerRateLimitService;
    private final TriggerQuarantineService triggerQuarantineService;
    private final TriggerIntentClassifier triggerIntentClassifier;
    private final WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService;
    private final IssueContextService issueContextService;
    private final FixTaskPreExecutionDecisionService preExecutionDecisionService;
    private final ConcurrentMap<String, WebhookHandleResult> deliveryResults = new ConcurrentHashMap<>();

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                new InMemoryRejectedTriggerAuditService(),
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                triggerRateLimitService,
                triggerIntentClassifier,
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                new InMemoryRejectedTriggerAuditService(),
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                webhookDeliveryDiagnosticService
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            CommandSafetyGate commandSafetyGate
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                new InMemoryRejectedTriggerAuditService(),
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                new CommandSafetyGate(),
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                webhookDeliveryDiagnosticService
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                new NoOpTriggerIntentClassifier(),
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                new NoOpTriggerQuarantineService(),
                new NoOpTriggerRateLimitService(),
                triggerIntentClassifier,
                new InMemoryWebhookDeliveryDiagnosticService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                new InMemoryWebhookDeliveryDiagnosticService(),
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            IssueContextService issueContextService,
            FixTaskPreExecutionDecisionService preExecutionDecisionService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                new InMemoryWebhookDeliveryDiagnosticService(),
                issueContextService,
                preExecutionDecisionService
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            IssueContextService issueContextService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                new InMemoryWebhookDeliveryDiagnosticService(),
                issueContextService,
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService
    ) {
        this(
                objectMapper,
                fixTaskService,
                fixTaskDispatcher,
                issueCommentTool,
                fixTaskTimelineService,
                rejectedTriggerAuditService,
                commandSafetyGate,
                triggerQuarantineService,
                triggerRateLimitService,
                triggerIntentClassifier,
                webhookDeliveryDiagnosticService,
                defaultIssueContextService(),
                new InMemoryFixTaskPreExecutionDecisionService()
        );
    }

    @Autowired
    public GitHubWebhookService(
            ObjectMapper objectMapper,
            FixTaskService fixTaskService,
            FixTaskDispatcher fixTaskDispatcher,
            IssueCommentTool issueCommentTool,
            FixTaskTimelineService fixTaskTimelineService,
            RejectedTriggerAuditService rejectedTriggerAuditService,
            CommandSafetyGate commandSafetyGate,
            TriggerQuarantineService triggerQuarantineService,
            TriggerRateLimitService triggerRateLimitService,
            TriggerIntentClassifier triggerIntentClassifier,
            WebhookDeliveryDiagnosticService webhookDeliveryDiagnosticService,
            IssueContextService issueContextService,
            FixTaskPreExecutionDecisionService preExecutionDecisionService
    ) {
        this.objectMapper = objectMapper;
        this.fixTaskService = fixTaskService;
        this.fixTaskDispatcher = fixTaskDispatcher;
        this.issueCommentTool = issueCommentTool;
        this.fixTaskTimelineService = fixTaskTimelineService;
        this.rejectedTriggerAuditService = rejectedTriggerAuditService;
        this.commandSafetyGate = commandSafetyGate;
        this.triggerQuarantineService = triggerQuarantineService;
        this.triggerRateLimitService = triggerRateLimitService;
        this.triggerIntentClassifier = triggerIntentClassifier;
        this.webhookDeliveryDiagnosticService = webhookDeliveryDiagnosticService;
        this.issueContextService = issueContextService;
        this.preExecutionDecisionService = preExecutionDecisionService;
    }

    public WebhookHandleResult handle(String event, String deliveryId, String payload) {
        if (!StringUtils.hasText(deliveryId)) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.BAD_REQUEST,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Missing X-GitHub-Delivery header"
            );
            throw new InvalidWebhookPayloadException("Missing X-GitHub-Delivery header");
        }
        WebhookHandleResult existingResult = deliveryResults.get(deliveryId);
        if (existingResult != null) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY,
                    existingResult.taskId(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Duplicate delivery ignored"
            );
            return WebhookHandleResult.duplicate(existingResult.taskId());
        }
        WebhookHandleResult result = route(event, deliveryId, payload);
        deliveryResults.put(deliveryId, result);
        return result;
    }

    private WebhookHandleResult route(String event, String deliveryId, String payload) {
        if (!ISSUE_COMMENT_EVENT.equals(event)) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.IGNORED,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Ignored unsupported GitHub event"
            );
            return WebhookHandleResult.ignored();
        }
        JsonNode root = parsePayload(payload);
        if (!CREATED_ACTION.equals(requiredText(root, "action"))) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.IGNORED,
                    null,
                    optionalText(root, "repository", "owner", "login"),
                    optionalText(root, "repository", "name"),
                    optionalLong(root, null, "issue", "number"),
                    optionalText(root, "comment", "user", "login"),
                    optionalText(root, "comment", "body"),
                    "Ignored issue_comment action: " + requiredText(root, "action")
            );
            return WebhookHandleResult.ignored();
        }
        String commentBody = requiredText(root, "comment", "body");
        if (!commandSafetyGate.isAgentFixCommand(commentBody)) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.IGNORED,
                    null,
                    optionalText(root, "repository", "owner", "login"),
                    optionalText(root, "repository", "name"),
                    optionalLong(root, null, "issue", "number"),
                    optionalText(root, "comment", "user", "login"),
                    commentBody,
                    "Ignored non-/agent fix comment"
            );
            return WebhookHandleResult.ignored();
        }
        String repositoryOwner = requiredText(root, "repository", "owner", "login");
        String repositoryName = requiredText(root, "repository", "name");
        String triggerUser = requiredText(root, "comment", "user", "login");
        long issueNumber = requiredLong(root, "issue", "number");
        SafetyGateDecision safetyDecision = commandSafetyGate.evaluate(new SafetyGateRequest(
                repositoryOwner,
                repositoryName,
                triggerUser,
                commentBody
        ));
        if (!safetyDecision.allowed() && !canClassifyWithIssueContext(safetyDecision)) {
            RejectedTriggerAuditVo rejectedTrigger = recordRejectedTrigger(
                    deliveryId,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    safetyDecision.reason(),
                    safetyDecision.category()
            );
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.REJECTED,
                    null,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    safetyDecision.reason(),
                    WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                    rejectedTrigger.id(),
                    rejectedTriggerUrl(rejectedTrigger.id())
            );
            return WebhookHandleResult.rejected();
        }
        WebhookHandleResult duplicateDeliveryResult = fixTaskService.findTaskByDeliveryId(deliveryId)
                .map(task -> {
                    recordDelivery(
                            deliveryId,
                            event,
                            WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY,
                            task.id(),
                            repositoryOwner,
                            repositoryName,
                            issueNumber,
                            triggerUser,
                            commentBody,
                            "Duplicate delivery ignored"
                    );
                    return WebhookHandleResult.duplicate(task.id());
                })
                .orElse(null);
        if (duplicateDeliveryResult != null) {
            return duplicateDeliveryResult;
        }
        WebhookHandleResult activeTaskResult = fixTaskService.findActiveTaskForIssue(
                        repositoryOwner,
                        repositoryName,
                        issueNumber
                )
                .map((activeTask) -> handleActiveTaskExists(
                        activeTask,
                        deliveryId,
                        event,
                        repositoryOwner,
                        repositoryName,
                        issueNumber,
                        triggerUser,
                        commentBody
                ))
                .orElse(null);
        if (activeTaskResult != null) {
            return activeTaskResult;
        }
        TriggerQuarantineDecision quarantineDecision = triggerQuarantineService.check(new TriggerQuarantineRequest(
                "issue_comment",
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser
        ));
        if (!quarantineDecision.allowed()) {
            RejectedTriggerAuditVo rejectedTrigger = recordRejectedTrigger(
                    deliveryId,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    quarantineDecision.reason(),
                    quarantineDecision.category()
            );
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.REJECTED,
                    null,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    quarantineDecision.reason(),
                    WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                    rejectedTrigger.id(),
                    rejectedTriggerUrl(rejectedTrigger.id())
            );
            return WebhookHandleResult.rejected();
        }
        TriggerRateLimitDecision rateLimitDecision = triggerRateLimitService.checkAndRecord(new TriggerRateLimitRequest(
                "issue_comment",
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser
        ));
        if (!rateLimitDecision.allowed()) {
            RejectedTriggerAuditVo rejectedTrigger = recordRejectedTrigger(
                    deliveryId,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    rateLimitDecision.reason(),
                    rateLimitDecision.category()
            );
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.REJECTED,
                    null,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    rateLimitDecision.reason(),
                    WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                    rejectedTrigger.id(),
                    rejectedTriggerUrl(rejectedTrigger.id())
            );
            return WebhookHandleResult.rejected();
        }
        TriggerClassificationResult triggerClassificationResult = classifyTriggerIntent(
                deliveryId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                commentBody
        );
        TriggerIntentDecision triggerIntentDecision = triggerClassificationResult.decision();
        if (!triggerIntentDecision.shouldExecute()) {
            RejectedTriggerAuditVo rejectedTrigger = recordRejectedTrigger(
                    deliveryId,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    triggerIntentDecision.rejectionReason(),
                    triggerIntentDecision.rejectionCategory()
            );
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.REJECTED,
                    null,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    triggerIntentDecision.rejectionReason(),
                    WebhookDeliveryOutcomeType.REJECTED_TRIGGER,
                    rejectedTrigger.id(),
                    rejectedTriggerUrl(rejectedTrigger.id())
            );
            return WebhookHandleResult.rejected();
        }
        FixTaskCreationResult creationResult = fixTaskService.createFixTaskIfAbsent(new CreateFixTaskCommand(
                repositoryOwner,
                repositoryName,
                issueNumber,
                optionalLong(root, 0, "installation", "id"),
                triggerUser,
                commentBody,
                deliveryId,
                requiredLong(root, "comment", "id")
        ));
        FixTaskVo task = creationResult.task();
        if (!creationResult.created()) {
            recordDelivery(
                    deliveryId,
                    event,
                    WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY,
                    task.id(),
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    commentBody,
                    "Duplicate delivery ignored"
            );
            return WebhookHandleResult.duplicate(task.id());
        }
        preExecutionDecisionService.recordDecision(new RecordFixTaskPreExecutionDecisionCommand(
                task.id(),
                "ISSUE_COMMENT",
                "ALLOWED",
                decision(safetyDecision.allowed(), safetyDecision.reason(), safetyDecision.category()),
                decision(true, "No active task exists for this issue", RejectedTriggerCategory.UNKNOWN),
                decision(quarantineDecision.allowed(), quarantineDecision.reason(), quarantineDecision.category()),
                decision(rateLimitDecision.allowed(), rateLimitDecision.reason(), rateLimitDecision.category()),
                decision(true, triggerIntentDecision.reason(), triggerIntentDecision.rejectionCategory()),
                triggerClassificationResult.issueContextLoaded(),
                Instant.now()
        ));
        recordTimelineEvent(
                task.id(),
                FixTaskTimelineEventType.TRIGGER_ACCEPTED,
                TriggerDecisionEvidenceFormatter.accepted(
                        safetyDecision,
                        triggerIntentDecision,
                        triggerClassificationResult.issueContextLoaded()
                )
        );
        recordTimelineEvent(task.id(), FixTaskTimelineEventType.TASK_CREATED, "Task accepted from /agent fix");
        createStatusComment(task);
        fixTaskDispatcher.dispatch(task.id());
        recordDelivery(
                deliveryId,
                event,
                WebhookDeliveryDiagnosticStatus.TASK_CREATED,
                task.id(),
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                commentBody,
                "Task created from /agent fix"
        );
        return WebhookHandleResult.taskCreated(task.id());
    }

    private WebhookHandleResult handleActiveTaskExists(
            FixTaskVo activeTask,
            String deliveryId,
            String event,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String commentBody
    ) {
        recordTimelineEvent(
                activeTask.id(),
                FixTaskTimelineEventType.ACTIVE_TASK_EXISTS,
                "Ignored duplicate /agent fix while task is active"
        );
        updateStatusComment(() -> issueCommentTool.updateActiveTaskExists(activeTask));
        recordDelivery(
                deliveryId,
                event,
                WebhookDeliveryDiagnosticStatus.ACTIVE_TASK_EXISTS,
                activeTask.id(),
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                commentBody,
                "Ignored duplicate /agent fix while task is active"
        );
        return WebhookHandleResult.activeTaskExists(activeTask.id());
    }

    private TriggerClassificationResult classifyTriggerIntent(
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String commentBody
    ) {
        try {
            boolean issueContextLoaded = triggerIntentClassifier.supportsIssueContextClassification();
            GitHubIssueContext issueContext = issueContextLoaded
                    ? issueContextService.loadIssueContext(repositoryOwner, repositoryName, issueNumber)
                    : new GitHubIssueContext("", "", "", List.of());
            return new TriggerClassificationResult(
                    triggerIntentClassifier.classify(new TriggerIntentClassificationRequest(
                            classificationId(deliveryId),
                            "issue_comment",
                            repositoryOwner,
                            repositoryName,
                            issueNumber,
                            triggerUser,
                            commentBody,
                            issueContext.title(),
                            issueContext.body(),
                            issueComments(issueContext)
                    )),
                    issueContextLoaded
            );
        } catch (RuntimeException exception) {
            return new TriggerClassificationResult(
                    TriggerIntentDecision.rejected(
                            "Model trigger classification failed: unable to load issue context: " + failureReason(exception)
                    ),
                    false
            );
        }
    }

    private boolean canClassifyWithIssueContext(SafetyGateDecision safetyDecision) {
        return RejectedTriggerCategory.NOT_ACTIONABLE.equals(safetyDecision.category())
                && triggerIntentClassifier.supportsIssueContextClassification();
    }

    private RejectedTriggerAuditVo recordRejectedTrigger(
            String deliveryId,
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason,
            String category
    ) {
        IssueCommentResult refusalComment = createRefusalComment(
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                reason,
                category
        );
        return rejectedTriggerAuditService.recordRejectedTrigger(new RecordRejectedTriggerCommand(
                "issue_comment",
                deliveryId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                reason,
                category,
                refusalComment == null ? null : refusalComment.id(),
                refusalComment == null ? null : refusalComment.url()
        ));
    }

    private IssueCommentResult createRefusalComment(
            String repositoryOwner,
            String repositoryName,
            long issueNumber,
            String triggerUser,
            String triggerComment,
            String reason,
            String category
    ) {
        try {
            return issueCommentTool.commentRejected(
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    triggerComment,
                    reason,
                    category
            );
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private void createStatusComment(FixTaskVo task) {
        try {
            IssueCommentResult commentResult = issueCommentTool.commentAccepted(task);
            fixTaskService.attachStatusComment(task.id(), commentResult.id(), commentResult.url());
            recordTimelineEvent(task.id(), FixTaskTimelineEventType.STATUS_COMMENT_CREATED, "Status comment created");
        } catch (RuntimeException exception) {
            recordTimelineEvent(
                    task.id(),
                    FixTaskTimelineEventType.STATUS_COMMENT_FAILED,
                    "Status comment failed: " + statusCommentFailureReason(exception)
            );
            // GitHub comment feedback must not block the durable task workflow.
        }
    }

    private static void updateStatusComment(Runnable update) {
        try {
            update.run();
        } catch (RuntimeException exception) {
            // GitHub comment feedback must not block the durable task workflow.
        }
    }

    private void recordTimelineEvent(String taskId, FixTaskTimelineEventType eventType, String message) {
        try {
            fixTaskTimelineService.recordEvent(taskId, eventType, message);
        } catch (RuntimeException exception) {
            // Timeline feedback must not block the durable task workflow.
        }
    }

    private static String statusCommentFailureReason(RuntimeException exception) {
        if (!StringUtils.hasText(exception.getMessage())) {
            return exception.getClass().getSimpleName();
        }
        return LogSummary.truncateFailureReason(exception.getMessage());
    }

    private void recordDelivery(
            String deliveryId,
            String event,
            WebhookDeliveryDiagnosticStatus status,
            String taskId,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment,
            String message
    ) {
        recordDelivery(
                deliveryId,
                event,
                status,
                taskId,
                repositoryOwner,
                repositoryName,
                issueNumber,
                triggerUser,
                triggerComment,
                message,
                defaultOutcomeType(status),
                defaultOutcomeId(status, taskId),
                defaultOutcomeUrl(status, taskId)
        );
    }

    private void recordDelivery(
            String deliveryId,
            String event,
            WebhookDeliveryDiagnosticStatus status,
            String taskId,
            String repositoryOwner,
            String repositoryName,
            Long issueNumber,
            String triggerUser,
            String triggerComment,
            String message,
            WebhookDeliveryOutcomeType outcomeType,
            String outcomeId,
            String outcomeUrl
    ) {
        try {
            webhookDeliveryDiagnosticService.record(new RecordWebhookDeliveryDiagnosticCommand(
                    deliveryId,
                    event,
                    status,
                    taskId,
                    repositoryOwner,
                    repositoryName,
                    issueNumber,
                    triggerUser,
                    triggerComment,
                    LogSummary.truncateFailureReason(message),
                    outcomeType,
                    outcomeId,
                    outcomeUrl
            ));
        } catch (RuntimeException exception) {
            // Diagnostics must not block webhook handling.
        }
    }

    private static WebhookDeliveryOutcomeType defaultOutcomeType(WebhookDeliveryDiagnosticStatus status) {
        return switch (status) {
            case TASK_CREATED, ACTIVE_TASK_EXISTS -> WebhookDeliveryOutcomeType.TASK;
            case DUPLICATE_DELIVERY -> WebhookDeliveryOutcomeType.DUPLICATE;
            case REJECTED -> WebhookDeliveryOutcomeType.REJECTED_TRIGGER;
            case IGNORED -> WebhookDeliveryOutcomeType.IGNORED;
            case INVALID_SIGNATURE, BAD_REQUEST, FAILED -> WebhookDeliveryOutcomeType.ERROR;
        };
    }

    private static String defaultOutcomeId(WebhookDeliveryDiagnosticStatus status, String taskId) {
        return status == WebhookDeliveryDiagnosticStatus.TASK_CREATED
                || status == WebhookDeliveryDiagnosticStatus.ACTIVE_TASK_EXISTS
                || status == WebhookDeliveryDiagnosticStatus.DUPLICATE_DELIVERY
                ? taskId
                : null;
    }

    private static String defaultOutcomeUrl(WebhookDeliveryDiagnosticStatus status, String taskId) {
        return defaultOutcomeId(status, taskId) == null ? null : "/tasks/" + taskId;
    }

    private static String rejectedTriggerUrl(String rejectedTriggerId) {
        return "#rejected-trigger-" + rejectedTriggerId;
    }

    private static String classificationId(String deliveryId) {
        return java.util.UUID.nameUUIDFromBytes(("issue-comment:" + deliveryId).getBytes(java.nio.charset.StandardCharsets.UTF_8))
                .toString();
    }

    private static List<TriggerIntentIssueComment> issueComments(GitHubIssueContext issueContext) {
        return issueContext.comments().stream()
                .map(comment -> new TriggerIntentIssueComment(comment.author(), comment.body()))
                .toList();
    }

    private static String failureReason(RuntimeException exception) {
        return exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
    }

    private static TriggerEvaluationDecisionVo decision(boolean allowed, String reason, String category) {
        return new TriggerEvaluationDecisionVo(allowed, reason, category);
    }

    private static IssueContextService defaultIssueContextService() {
        return new IssueContextService(new GitHubIssueContextClient(new GitHubProperties()));
    }

    private JsonNode parsePayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (IOException exception) {
            throw new InvalidWebhookPayloadException("Malformed GitHub webhook payload");
        }
    }

    private static String requiredText(JsonNode root, String... path) {
        JsonNode node = requiredNode(root, path);
        if (!node.isTextual()) {
            throw new InvalidWebhookPayloadException("Expected text field: " + String.join(".", path));
        }
        return node.asText();
    }

    private static long requiredLong(JsonNode root, String... path) {
        JsonNode node = requiredNode(root, path);
        if (!node.canConvertToLong()) {
            throw new InvalidWebhookPayloadException("Expected numeric field: " + String.join(".", path));
        }
        return node.asLong();
    }

    private static long optionalLong(JsonNode root, long defaultValue, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (!node.canConvertToLong()) {
            throw new InvalidWebhookPayloadException("Expected numeric field: " + String.join(".", path));
        }
        return node.asLong();
    }

    private static Long optionalLong(JsonNode root, Long defaultValue, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (!node.canConvertToLong()) {
            throw new InvalidWebhookPayloadException("Expected numeric field: " + String.join(".", path));
        }
        return node.asLong();
    }

    private static String optionalText(JsonNode root, String... path) {
        JsonNode node = nodeAt(root, path);
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isTextual()) {
            throw new InvalidWebhookPayloadException("Expected text field: " + String.join(".", path));
        }
        return node.asText();
    }

    private static JsonNode requiredNode(JsonNode root, String... path) {
        JsonNode current = nodeAt(root, path);
        if (current == null || current.isNull()) {
            throw new InvalidWebhookPayloadException("Missing field: " + String.join(".", path));
        }
        return current;
    }

    private static JsonNode nodeAt(JsonNode root, String... path) {
        JsonNode current = root;
        for (String segment : path) {
            current = current == null ? null : current.get(segment);
        }
        return current;
    }

    private record TriggerClassificationResult(TriggerIntentDecision decision, boolean issueContextLoaded) {
    }
}
