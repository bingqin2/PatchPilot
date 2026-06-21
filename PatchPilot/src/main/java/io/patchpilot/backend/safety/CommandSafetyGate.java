package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.SafetyGateDecision;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Service
public class CommandSafetyGate {

    private static final String AGENT_FIX_COMMAND = "/agent fix";

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
}
