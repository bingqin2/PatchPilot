package io.patchpilot.backend.safety;

import io.patchpilot.backend.safety.domain.GeneratedDiffRiskDecision;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class GeneratedDiffRiskGate {

    private static final Pattern DIFF_PATH_PATTERN = Pattern.compile("^diff --git a/(.+?) b/(.+)$");

    private final GeneratedDiffSafetyPolicy safetyPolicy;

    public GeneratedDiffRiskGate() {
        this(new GeneratedDiffSafetyPolicy());
    }

    public GeneratedDiffRiskGate(GeneratedDiffSafetyPolicy safetyPolicy) {
        this.safetyPolicy = safetyPolicy;
    }

    public GeneratedDiffRiskDecision evaluate(String diff) {
        if (diff == null || diff.isBlank()) {
            return GeneratedDiffRiskDecision.accepted();
        }

        int changedLines = 0;
        int filesChanged = 0;
        for (String line : diff.lines().toList()) {
            java.util.regex.Matcher pathMatcher = DIFF_PATH_PATTERN.matcher(line);
            if (pathMatcher.matches()) {
                filesChanged++;
                String newPath = pathMatcher.group(2);
                if (safetyPolicy.isProtectedPath(newPath)) {
                    return GeneratedDiffRiskDecision.rejected("sensitive path " + newPath);
                }
            }

            if (safetyPolicy.isBinaryDiffLine(line)) {
                return GeneratedDiffRiskDecision.rejected("binary file change");
            }
            if (safetyPolicy.isChangedContentLine(line)) {
                changedLines++;
            }
            if (safetyPolicy.isAddedSecretLikeLine(line)) {
                return GeneratedDiffRiskDecision.rejected("secret-like added line");
            }
        }

        if (filesChanged > safetyPolicy.maxFilesChanged()) {
            return GeneratedDiffRiskDecision.rejected("too many files changed: " + filesChanged);
        }
        if (changedLines > safetyPolicy.maxChangedLines()) {
            return GeneratedDiffRiskDecision.rejected("too many changed lines: " + changedLines);
        }

        return GeneratedDiffRiskDecision.accepted();
    }
}
