package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.config.SafetyProperties;
import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import io.patchpilot.backend.safety.domain.SafetyGateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class CommandSafetyGate {

    private static final String AGENT_FIX_COMMAND = "/agent fix";
    private static final String NOT_ACTIONABLE_REASON = "Unsafe request rejected: instruction is not actionable";
    private static final Pattern FILE_PATH_PATTERN = Pattern.compile(
            "(^|\\s)([a-z0-9_.-]+/)+[a-z0-9_.-]+(\\s|$)"
    );
    private static final Pattern FILE_NAME_PATTERN = Pattern.compile(
            "(^|\\s)[a-z0-9_.-]+\\.(java|kt|ts|tsx|js|jsx|py|md|yml|yaml|json|xml|properties|sql|css|html)(\\s|$)"
    );
    private final Set<String> allowedTriggerUsers;
    private final Set<String> allowedRepositories;

    public CommandSafetyGate() {
        this(new SafetyProperties());
    }

    @Autowired
    public CommandSafetyGate(SafetyProperties safetyProperties) {
        this.allowedTriggerUsers = normalizedSet(safetyProperties.getAllowedTriggerUsers());
        this.allowedRepositories = normalizedSet(safetyProperties.getAllowedRepositories());
    }

    public SafetyGateDecision evaluate(String triggerComment) {
        if (!StringUtils.hasText(triggerComment)) {
            return SafetyGateDecision.rejected("Unsafe request rejected: empty command");
        }
        String normalized = normalize(triggerComment);
        if (!isAgentFixCommand(normalized)) {
            return SafetyGateDecision.rejected("Unsafe request rejected: unsupported command");
        }
        if (containsDangerousInstruction(normalized)) {
            return SafetyGateDecision.rejected("Unsafe request rejected: destructive or secret-exfiltration instruction");
        }
        if (!isActionableInstruction(normalized)) {
            return SafetyGateDecision.rejected(NOT_ACTIONABLE_REASON);
        }
        return SafetyGateDecision.accepted();
    }

    public SafetyGateDecision evaluate(SafetyGateRequest request) {
        SafetyGateDecision commandDecision = evaluate(request.triggerComment());
        if (!commandDecision.allowed()) {
            return commandDecision;
        }
        if (!isTriggerUserAllowed(request.triggerUser())) {
            return SafetyGateDecision.rejected("Unsafe request rejected: trigger user is not allowed");
        }
        if (!isRepositoryAllowed(request.repositoryOwner(), request.repositoryName())) {
            return SafetyGateDecision.rejected("Unsafe request rejected: repository is not allowed");
        }
        return SafetyGateDecision.accepted();
    }

    public boolean isAgentFixCommand(String triggerComment) {
        if (!StringUtils.hasText(triggerComment)) {
            return false;
        }
        String normalized = normalize(triggerComment);
        return AGENT_FIX_COMMAND.equals(normalized) || normalized.startsWith(AGENT_FIX_COMMAND + " ");
    }

    private static boolean containsDangerousInstruction(String normalizedCommand) {
        return containsAny(normalizedCommand, "delete the repository", "remove the repository", "destroy the repository")
                || containsAny(normalizedCommand, "leak secret", "leak secrets", "print secret", "print secrets")
                || containsAny(normalizedCommand, "exfiltrate", "steal token", "steal tokens", "dump env", "dump environment")
                || containsAny(normalizedCommand, "rm -rf", "curl ", "wget ", "chmod 777", "sudo ");
    }

    private static boolean isActionableInstruction(String normalizedCommand) {
        String instruction = normalizedCommand.substring(AGENT_FIX_COMMAND.length()).trim();
        if (!StringUtils.hasText(instruction) || isKnownVagueInstruction(instruction)) {
            return false;
        }
        return isSupportedPatchOperation(instruction)
                || containsLikelyFileReference(instruction)
                || containsActionableProblemSignal(instruction);
    }

    private static boolean isKnownVagueInstruction(String instruction) {
        return containsAny(instruction, "make it better", "do something", "whatever", "random words")
                || Set.of("help", "fix", "fix it", "improve", "hello", "hi", "test").contains(instruction);
    }

    private static boolean isSupportedPatchOperation(String instruction) {
        if (instruction.startsWith("touch ")) {
            return hasTokenAfterOperation(instruction, "touch ");
        }
        if (instruction.startsWith("replace ")) {
            return instruction.substring("replace ".length()).trim().split("\\s+").length >= 2;
        }
        return false;
    }

    private static boolean hasTokenAfterOperation(String instruction, String operation) {
        return instruction.substring(operation.length()).trim().split("\\s+").length >= 1;
    }

    private static boolean containsLikelyFileReference(String instruction) {
        return FILE_PATH_PATTERN.matcher(instruction).find() || FILE_NAME_PATTERN.matcher(instruction).find();
    }

    private static boolean containsActionableProblemSignal(String instruction) {
        if (instruction.length() < 20 || instruction.split("\\s+").length < 4) {
            return false;
        }
        return containsAny(
                instruction,
                "error", "exception", "failing", "failed", "failure", "bug",
                "test fails", "tests fail", "compile", "build fails", "stack trace",
                "nullpointer", "null pointer", "npe", "timeout", "crash"
        );
    }

    private static boolean containsAny(String value, String... fragments) {
        for (String fragment : fragments) {
            if (value.contains(fragment)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private boolean isTriggerUserAllowed(String triggerUser) {
        return allowedTriggerUsers.isEmpty()
                || (StringUtils.hasText(triggerUser) && allowedTriggerUsers.contains(normalize(triggerUser)));
    }

    private boolean isRepositoryAllowed(String repositoryOwner, String repositoryName) {
        if (allowedRepositories.isEmpty()) {
            return true;
        }
        if (!StringUtils.hasText(repositoryOwner) || !StringUtils.hasText(repositoryName)) {
            return false;
        }
        return allowedRepositories.contains(normalize(repositoryOwner + "/" + repositoryName));
    }

    private static Set<String> normalizedSet(List<String> values) {
        Set<String> normalizedValues = new HashSet<>();
        if (values == null) {
            return normalizedValues;
        }
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                normalizedValues.add(normalize(value));
            }
        }
        return normalizedValues;
    }
}
